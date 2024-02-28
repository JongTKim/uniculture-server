package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Post.PostAddDto;
import com.capstone.uniculture.dto.Post.PostDetailDto;
import com.capstone.uniculture.dto.Post.PostUpdateDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Post.Post;

import com.capstone.uniculture.entity.Post.PostLike;
import com.capstone.uniculture.repository.MemberRepository;
import com.capstone.uniculture.repository.PostLikeRepository;
import com.capstone.uniculture.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.rmi.AlreadyBoundException;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;


    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("찾는 게시물이 존재하지 않습니다."));
    }

    // 게시물 생성
    public String createPost(PostAddDto postAddDto) {
        // 1. 게시물 작성하는 Member 찾기
        Member member = findMember(SecurityUtil.getCurrentMemberId());

        // 2. 빌더 패턴을 사용하여 게시물 객체 생성
        Post post = Post.builder()
                .title(postAddDto.getTitle())
                .content(postAddDto.getContents())
                .posttype(postAddDto.getPosttype())
                .build();

        // 3. 멤버 설정
        post.setMember(member);

        // 4. Repository 에 저장
        postRepository.save(post);

        return "게시물 생성 성공";
    }

    // 게시물 업데이트
    public String updatePost(Long postId, PostUpdateDto postUpdateDto){
        // 1. 수정할 게시물 찾기.
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("찾는 게시물이 없습니다."));

        // 2. 수정할 게시물이 자기것이 맞는지 확인하기
        if(post.getMember().getId() != SecurityUtil.getCurrentMemberId()){
            throw new RuntimeException("자신의 게시물이 아닙니다.");
        }

        // 3. 수정하기
        post.update(postUpdateDto);

        return "게시물 수정 성공";
    }

    // 게시물 조회

    // 게시물 삭제
    public void deletePost(Long postId){
        postRepository.deleteById(postId);
    }

    // 게시물 좋아요
    public void likePost(Long postId){
        Long memberId = SecurityUtil.getCurrentMemberId();
        Optional<PostLike> postLike = postLikeRepository.findByMember_IdAndPost_Id(memberId, postId);
        if(postLike.isPresent()){
            throw new IllegalArgumentException("이미 좋아요를 누른 게시물입니다.");
        }
        Post post = findPost(postId);
        Member member = findMember(memberId);
        postLikeRepository.save(new PostLike(member,post));
        post.likePost();
    }

    // 게시물 안좋아요
    public void unlikePost(Long postId){
        Long memberId = SecurityUtil.getCurrentMemberId();
        Optional<PostLike> postLike = postLikeRepository.findByMember_IdAndPost_Id(memberId, postId);
        if(postLike.isEmpty()){
            throw new IllegalArgumentException("좋아요를 누르지 않은 게시물입니다.");
        }
        Post post = findPost(postId);
        postLikeRepository.deleteByMember_IdAndPost_Id(memberId,postId);
        post.unlikePost();
    }

    public PostDetailDto getPost(Long postId) {

        Post post = findPost(postId);

        post.upViewCount();

        return PostDetailDto.fromEntity(post);
    }
}
