package com.otter.intranet.post;

import com.otter.intranet.employee.Employee;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "post")
public class Post {

    // DB의 AUTO_INCREMENT를 이용한 게시글 식별자 생성
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long postId;

    // 여러 게시글이 한 직원을 작성자로 참조하는 다대일 관계
    // 작성자 정보는 필요한 시점에 조회하도록 지연 로딩 적용
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee author;

    // enum 이름을 문자열로 저장
    // 상수 순서 변경에 따른 기존 데이터의 의미 변경 방지
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 20)
    private PostCategory category;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    // 화면의 작성자 표시 방식 결정
    // 익명 게시글도 실제 작성자 관계는 유지
    @Column(name = "anonymous", nullable = false)
    private boolean anonymous;

    // 이미지 파일의 저장 위치를 식별하는 키
    // 이미지가 없는 게시글은 null 허용
    @Column(name = "image_key", length = 500)
    private String imageKey;

    // 최초 생성 시각 유지 및 수정 SQL의 갱신 대상 제외
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 작성자 및 필수 입력값 검증을 거친 게시글 생성
    // 게시글 번호와 생성·수정 시각은 서버 및 DB에서 관리
    public Post(
            Employee author,
            PostCategory category,
            String title,
            String content,
            boolean anonymous,
            String imageKey
    ) {
        this.author = Objects.requireNonNull(
                author,
                "작성자가 필요합니다."
        );

        applyContent(category, title, content, anonymous);
        this.imageKey = imageKey;
    }

    // 작성자·생성일을 유지한 게시글 내용 변경
    // 등록과 동일한 입력 규칙 적용
    public void update(
            PostCategory category,
            String title,
            String content,
            boolean anonymous
    ) {
        applyContent(category, title, content, anonymous);
    }

    public boolean isWrittenBy(Long employeeId) {
        // 로그인 직원 ID와 실제 작성자 ID 비교
        // 익명 게시글에도 동일한 수정·삭제 권한 판단 기준 적용
        return employeeId != null
                && employeeId.equals(author.getEmployeeId());
    }

    // 생성·수정 시 공통 입력 검증 및 필드 반영
    // 검증 완료 후 대입하여 잘못된 값의 부분 반영 방지
    private void applyContent(
            PostCategory category,
            String title,
            String content,
            boolean anonymous
    ) {
        if (category == null) {
            throw new IllegalArgumentException(
                    "게시글 종류가 필요합니다."
            );
        }

        // null·빈 문자열·공백으로만 구성된 제목 차단
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "제목을 입력해 주세요."
            );
        }

        if (title.length() > 200) {
            throw new IllegalArgumentException(
                    "제목은 200자 이하여야 합니다."
            );
        }

        // null·빈 문자열·공백으로만 구성된 내용 차단
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException(
                    "내용을 입력해 주세요."
            );
        }

        // 칭찬 게시글에만 익명 기능 허용
        // 점심 게시글의 익명 등록 및 변경 차단
        if (category == PostCategory.LUNCH && anonymous) {
            throw new IllegalArgumentException(
                    "점심 게시글은 익명으로 등록할 수 없습니다."
            );
        }

        this.category = category;
        this.title = title;
        this.content = content;
        this.anonymous = anonymous;
    }

    @PrePersist
    protected void onCreate() {
        // JPA의 최초 저장 직전 생성·수정 시각 설정
        // 클라이언트 입력값 대신 서버 시각 사용
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        // JPA의 변경 내용 반영 직전 수정 시각 갱신
        // 최초 생성 시각은 유지
        this.updatedAt = LocalDateTime.now();
    }
}