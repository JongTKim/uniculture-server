package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.PostTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    void deleteAllByPostId(Long postId);
}
