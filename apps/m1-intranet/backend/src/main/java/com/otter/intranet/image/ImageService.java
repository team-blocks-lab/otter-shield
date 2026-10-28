package com.otter.intranet.image;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class ImageService {

    private static final long MAX_FILE_SIZE = 5L * 1024 * 1024;
    private static final int MAX_DIMENSION = 4096;

    // 서버가 발급한 UUID 기반 PNG 파일명만 허용
    // 임의 경로 및 상위 폴더 접근 문자열 차단
    private static final Pattern IMAGE_KEY_PATTERN = Pattern.compile(
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-"
                    + "[0-9a-f]{4}-[0-9a-f]{12}\\.png$"
    );

    private final Path uploadDir;

    public ImageService(
            @Value("${app.upload-dir}") String uploadDir
    ) {
        // 실행 위치에 따른 상대 경로를 절대 경로로 정규화
        this.uploadDir = Path.of(uploadDir)
                .toAbsolutePath()
                .normalize();
    }

    public String save(MultipartFile file) {
        // 사진 미첨부 요청의 정상 처리
        if (file == null || file.isEmpty()) {
            return null;
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw badRequest("사진은 5MB 이하여야 합니다.");
        }

        try {
            BufferedImage image = readValidatedImage(file);

            // 원본 파일을 그대로 저장하지 않고 PNG로 재인코딩
            // 파일명·확장자 위장 및 원본 메타데이터의 직접 저장 방지
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            if (!ImageIO.write(image, "png", output)) {
                throw badRequest("사진을 변환할 수 없습니다.");
            }

            byte[] imageBytes = output.toByteArray();

            // 변환 후 파일에도 동일한 저장 크기 제한 적용
            if (imageBytes.length > MAX_FILE_SIZE) {
                throw badRequest(
                        "변환된 사진이 5MB를 초과합니다. "
                                + "사진 크기를 줄여 주세요."
                );
            }

            // 사용자 파일명 대신 서버 발급 식별자로 저장
            String imageKey = UUID.randomUUID() + ".png";
            Path target = resolvePath(imageKey);

            Files.createDirectories(uploadDir);

            // 기존 파일 덮어쓰기 방지
            Files.write(
                    target,
                    imageBytes,
                    StandardOpenOption.CREATE_NEW,
                    StandardOpenOption.WRITE
            );

            return imageKey;

        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "사진 저장 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    public Resource load(String imageKey) {
        Path target = resolvePath(imageKey);

        if (!Files.isRegularFile(target) || !Files.isReadable(target)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "사진을 찾을 수 없습니다."
            );
        }

        try {
            return new UrlResource(target.toUri());
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "사진 조회 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    public void delete(String imageKey) {
        if (imageKey == null) {
            return;
        }

        try {
            // 이미 삭제된 파일에 대한 중복 삭제 허용
            Files.deleteIfExists(resolvePath(imageKey));
        } catch (IOException exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "사진 삭제 중 오류가 발생했습니다.",
                    exception
            );
        }
    }

    private BufferedImage readValidatedImage(
            MultipartFile file
    ) throws IOException {

        try (
                InputStream input = file.getInputStream();
                ImageInputStream imageInput =
                        ImageIO.createImageInputStream(input)
        ) {
            if (imageInput == null) {
                throw badRequest("이미지 파일을 읽을 수 없습니다.");
            }

            // 파일 확장자나 요청의 Content-Type 대신 실제 이미지 형식 검사
            Iterator<ImageReader> readers =
                    ImageIO.getImageReaders(imageInput);

            if (!readers.hasNext()) {
                throw badRequest("JPEG 또는 PNG 사진만 첨부할 수 있습니다.");
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(imageInput, true, true);

                String format = reader.getFormatName()
                        .toLowerCase(Locale.ROOT);

                if (!format.equals("jpeg") && !format.equals("png")) {
                    throw badRequest(
                            "JPEG 또는 PNG 사진만 첨부할 수 있습니다."
                    );
                }

                // 전체 이미지 디코딩 전 가로·세로 크기 검사
                // 작은 파일에 과도한 해상도를 담은 요청의 메모리 사용 제한
                int width = reader.getWidth(0);
                int height = reader.getHeight(0);

                if (width < 1 || height < 1
                        || width > MAX_DIMENSION
                        || height > MAX_DIMENSION) {
                    throw badRequest(
                            "사진의 가로·세로는 각각 4096픽셀 이하여야 합니다."
                    );
                }

                return reader.read(0);

            } finally {
                // 이미지 리더의 내부 자원 해제
                reader.dispose();
            }

        } catch (IOException exception) {
            throw badRequest("손상되었거나 읽을 수 없는 사진입니다.");
        }
    }

    private Path resolvePath(String imageKey) {
        if (imageKey == null
                || !IMAGE_KEY_PATTERN.matcher(imageKey).matches()) {
            throw badRequest("올바르지 않은 이미지 키입니다.");
        }

        return uploadDir.resolve(imageKey).normalize();
    }

    private ResponseStatusException badRequest(String message) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                message
        );
    }
}