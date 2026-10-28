package com.otter.intranet.post.dto;

import com.otter.intranet.post.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PostCreateRequest(

        @NotNull(message = "게시글 종류를 선택해 주세요.")
        PostCategory category,

        @NotBlank(message = "제목을 입력해 주세요.")
        @Size(max = 200, message = "제목은 200자 이하여야 합니다.")
        String title,

        @NotBlank(message = "내용을 입력해 주세요.")
        @Size(max = 10000, message = "내용은 10,000자 이하여야 합니다.")
        String content,

        boolean anonymous,

        // 업로드한 이미지의 저장 키 전달
        // 이미지 미첨부 시 null 허용
        @Size(max = 500, message = "이미지 키는 500자 이하여야 합니다.")
        String imageKey
) {
}