package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike,Long> {

    Optional<PostLike> findByMember_IdAndPost_Id(Long memberId, Long postId);

    void deleteByMember_IdAndPost_Id(Long memberId, Long postId);

    @Query("SELECT p.member FROM PostLike p WHERE p.post.id = :postId")
    Set<Long> findByChecked(Long postId);
}
