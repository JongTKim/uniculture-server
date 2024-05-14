package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    /*
    @Query(value = "SELECT pt.hashtag FROM post_tag pt " +
            "WHERE pt.created_date >= DATEADD(DAY, -7, CURRENT_DATE) " +
            "GROUP BY pt.hashtag " +
            "ORDER BY COUNT(*) DESC LIMIT 5", nativeQuery = true)
    List<String> findMostUsedPostTagLastWeek();
    */
    @Query("SELECT pt.hashtag FROM PostTag pt " +
            "WHERE pt.createdDate >= :startDate " +
            "GROUP BY pt.hashtag " +
            "ORDER BY COUNT(*) DESC")
    Page<String> findMostUsedPostTagLastWeek(@Param("startDate") LocalDateTime startDate, Pageable pageable);


    void deleteAllByPostId(Long postId);

}
