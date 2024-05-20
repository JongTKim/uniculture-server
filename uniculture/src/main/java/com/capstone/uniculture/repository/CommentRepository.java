package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    // 대댓글이 아닌 댓글중에서만 Paging 을 해서 가져온다.
    @Query("SELECT c FROM Comment c JOIN FETCH c.member WHERE c.post.id = :postId AND c.parent.id IS NULL")
    List<Comment> findCommentsByOnlyParent(@Param("postId") Long postId, Pageable pageable);

    // 어떠한 게시글에 대댓글을 제외한 댓글만의 개수를 Count 한다.
    @Query("SELECT COUNT(*) FROM Comment c WHERE c.post.id = :postId AND c.parent.id IS NULL")
    Long countCommentByPost_IdOnlyParent(Long postId);

    @Query("SELECT COUNT(*) FROM Comment c WHERE c.post.id = :postId")
    Long countCommentByPost_Id(Long postId);
}
