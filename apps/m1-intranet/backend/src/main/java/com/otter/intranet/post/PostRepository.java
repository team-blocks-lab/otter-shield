package com.otter.intranet.post;

import org.springframework.data.jpa.repository.JpaRepository;

// Post 엔티티의 DB 저장·조회·삭제 및 페이지 조회 기능 제공
// 게시글 식별자 타입으로 Long 사용
public interface PostRepository extends JpaRepository<Post, Long> {
}