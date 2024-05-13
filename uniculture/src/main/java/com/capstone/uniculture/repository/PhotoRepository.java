package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Post.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findAllByPostId(Long postId);
}
