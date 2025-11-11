package com.board.boardserver.repository;

import com.board.boardserver.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByBoardId(Long boardId);
    List<Post> findByUserId(Long userId);
    List<Post> findByBoardIdOrderByCreatedAtDesc(Long boardId);
}
