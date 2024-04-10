package com.capstone.uniculture.service;

import com.capstone.uniculture.entity.Post.PostTag;
import com.capstone.uniculture.repository.PostTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PostTagService {

    private final PostTagRepository postTagRepository;

    public void createByList(List<PostTag> postTagList){
        postTagRepository.saveAll(postTagList);
    }

    public void deleteAllById(Long postId){postTagRepository.deleteAllByPostId(postId);}
}
