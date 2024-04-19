package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findCommentByPostId(@Param("postId") Long postId, Pageable pageable);


    @Query("SELECT c FROM Comment c " +
            "JOIN FETCH c.member " +
            "WHERE c.post.id = :postId " +
            "AND c.parent.id IS NULL")
    Page<Comment> findCommentsByBoardIdOrderByParentIdAscCreatedAtAsc(@Param("postId") Long postId, Pageable pageable);

    Long countCommentByPost_Id(Long postId);
}
