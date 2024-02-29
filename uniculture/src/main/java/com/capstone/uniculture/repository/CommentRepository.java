package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
}
