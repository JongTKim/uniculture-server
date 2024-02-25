package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    Optional<PostLike> findByMember_IdAndPost_Id(Long memberId, Long postId);

    void deleteByMember_IdAndPost_Id(Long memberId, Long postId);
}
