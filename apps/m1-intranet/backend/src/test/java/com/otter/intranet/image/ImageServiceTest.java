package com.otter.intranet.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    // 실제 업로드 경로와 분리된 테스트 전용 임시 폴더
    @TempDir
    Path uploadDir;

    private ImageService imageService;

    @BeforeEach
    void setUp() {
        // Spring 서버 실행 없이 사진 처리 기능만 검증하기 위한 직접 생성
        imageService = new ImageService(uploadDir.toString());
    }

    @Test
    @DisplayName("정상 사진 저장 및 조회")
    void saveAndLoadImage() throws Exception {
        MockMultipartFile file = createPngFile();

        String imageKey = imageService.save(file);

        // 원본 파일명 대신 서버에서 생성한 UUID 기반 저장명 확인
        assertNotNull(imageKey);
        assertTrue(imageKey.matches(
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.png"
        ));
        assertTrue(Files.exists(uploadDir.resolve(imageKey)));

        Resource resource = imageService.load(imageKey);

        // 저장된 파일의 실제 이미지 형식 및 크기 확인
        try (InputStream input = resource.getInputStream()) {
            BufferedImage savedImage = ImageIO.read(input);

            assertNotNull(savedImage);
            assertEquals(10, savedImage.getWidth());
            assertEquals(10, savedImage.getHeight());
        }
    }

    @Test
    @DisplayName("저장된 사진 삭제")
    void deleteImage() throws Exception {
        String imageKey = imageService.save(createPngFile());

        imageService.delete(imageKey);

        assertFalse(Files.exists(uploadDir.resolve(imageKey)));
    }

    @Test
    @DisplayName("사진 미첨부 시 저장 생략")
    void returnNullWhenImageIsMissing() {
        assertNull(imageService.save(null));
    }

    @Test
    @DisplayName("사진 확장자로 위장한 일반 파일 차단")
    void rejectFakeImage() {
        // 파일명과 MIME 유형이 사진이어도 실제 내용이 다르면 업로드 거절
        MockMultipartFile file = new MockMultipartFile(
                "image",
                "fake.png",
                "image/png",
                "This is not an image.".getBytes(StandardCharsets.UTF_8)
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> imageService.save(file)
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("5MB 초과 파일 차단")
    void rejectOversizedImage() {
        byte[] content = new byte[5 * 1024 * 1024 + 1];

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "large.png",
                "image/png",
                content
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> imageService.save(file)
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("업로드 폴더 외부 경로 접근 차단")
    void rejectInvalidImageKey() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> imageService.load("../application.properties")
        );

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    @DisplayName("존재하지 않는 사진 조회 시 404 반환")
    void rejectMissingImage() {
        String imageKey = "00000000-0000-0000-0000-000000000000.png";

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> imageService.load(imageKey)
        );

        assertEquals(404, exception.getStatusCode().value());
    }

    private MockMultipartFile createPngFile() throws Exception {
        // 외부 사진 파일 없이 테스트에 필요한 작은 PNG 생성
        BufferedImage image = new BufferedImage(
                10,
                10,
                BufferedImage.TYPE_INT_RGB
        );

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);

        // 브라우저가 전송하는 업로드 파일을 대신하는 테스트 객체
        return new MockMultipartFile(
                "image",
                "sample.png",
                "image/png",
                output.toByteArray()
        );
    }
}