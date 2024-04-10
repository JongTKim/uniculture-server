package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Post.Request.PostAddDto;
import com.capstone.uniculture.dto.Post.Request.PostUpdateDto;
import com.capstone.uniculture.dto.Post.Response.PostDetailDto;
import com.capstone.uniculture.dto.Post.Response.PostListDto;
import com.capstone.uniculture.dto.Post.Response.PostSearchDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Post.*;

import com.capstone.uniculture.repository.MemberRepository;
import com.capstone.uniculture.repository.PostLikeRepository;
import com.capstone.uniculture.repository.PostRepository;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostTagService postTagService;


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
        Post post = postAddDto.toPost();

        // 3. 멤버 설정
        post.setMember(member);

        // 4. 태그 설정
        List<PostTag> postTags = postAddDto.getTag()
                .stream().map(tag -> new PostTag(post, tag)).toList();

        // 5. Repository 에 저장(Post, PostTag 각각)
        postRepository.save(post);
        postTagService.createByList(postTags);

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
    public PostDetailDto getPost(Long postId) {
        // 1. 게시물 찾기(FetchJoin 으로 전부 끌어오기)
        Post post = postRepository.findPostWithMemberById(postId)
                    .orElseThrow(()-> new IllegalArgumentException("조회하려는 게시물이 없습니다."));

        // 2. 조회수 증가
        post.upViewCount();
        PostDetailDto postDetailDto = PostDetailDto.fromEntity(post);

        // 3. 현재 로그인 상태인지 확인후 DTO 의 필드 값 변경
        try{
            Long memberId = SecurityUtil.getCurrentMemberId();

            postDetailDto.setIsLogin(true);

            // 사용자가 해당 게시물의 주인인지 확인. Login 시에만 적용
            if(post.getMember().getId() == memberId){
                postDetailDto.setIsMine(true);
            }else{
                postDetailDto.setIsMine(false);
            }
            // 사용자가 해당 게시물의 좋아요를 눌렀는지 판단. Login 시에만 적용
            if(postLikeRepository.findByMember_IdAndPost_Id(memberId, postId).isEmpty()){
                postDetailDto.setIsLike(false);
            } else{
                postDetailDto.setIsLike(true);
            }
        }catch(RuntimeException e){
            postDetailDto.setIsLogin(false);
            postDetailDto.setIsLike(false);
            postDetailDto.setIsMine(false);
        }

        return postDetailDto;
    }


    // 게시물 삭제
    public String deletePost(Long postId){

        Long memberId = SecurityUtil.getCurrentMemberId();

        // 멤버아이디와 포스트아이디넣고 존재하는지 확인해야됨
        Post post = postRepository.findPostByIdAndMemberId(postId, memberId).orElseThrow(
                () -> new IllegalArgumentException("본인이 작성한 글이 아닙니다"));

        postRepository.delete(post);
        return "게시물 삭제 성공";
    }

    // 게시물 좋아요
    public String likePost(Long postId){
        Long memberId = SecurityUtil.getCurrentMemberId();
        Optional<PostLike> postLike = postLikeRepository.findByMember_IdAndPost_Id(memberId, postId);
        if(postLike.isPresent()){
            throw new IllegalArgumentException("이미 좋아요를 누른 게시물입니다.");
        }
        Post post = findPost(postId);
        Member member = findMember(memberId);
        postLikeRepository.save(new PostLike(member,post));
        post.likePost();

        return "좋아요 성공";
    }

    // 게시물 좋아요 취소
    public String unlikePost(Long postId){
        Long memberId = SecurityUtil.getCurrentMemberId();
        Optional<PostLike> postLike = postLikeRepository.findByMember_IdAndPost_Id(memberId, postId);
        if(postLike.isEmpty()){
            throw new IllegalArgumentException("좋아요를 누르지 않은 게시물입니다.");
        }
        Post post = findPost(postId);
        postLikeRepository.deleteByMember_IdAndPost_Id(memberId,postId);
        post.unlikePost();

        return "좋아요 취소 성공";
    }

    // 모든 게시물 조회
    public Page<PostListDto> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAllWithMemberAndComments(pageable);
        List<PostListDto> list = posts.getContent().stream()
                .map(PostListDto::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, posts.getTotalElements());
    }

    // 게시물 타입에 따른 게시물 조회
    public Page<PostListDto> getPostsByType(PostType postType, Pageable pageable) {
        Page<Post> posts = postRepository.findByPostTypeWithMember(postType, pageable);
        List<PostListDto> list = posts.getContent().stream()
                .map(PostListDto::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(list,pageable,posts.getTotalElements());

    }

    // 멤버 아이디에 따른 게시물 조회
    public Page<PostListDto> getPostsByMember(PostCategory postCategory, Long memberId, Pageable pageable) {

        // 1. 카테고리와 아이디로 게시물 목록 가져오기 (Paging 처리)
        Page<Post> posts = postRepository.findByMemberIdWithMember(postCategory, memberId, pageable);

        // 2. Entity -> DTO 변환 (Fetch Join 을 했기에 Post.getMember().getNickname()을 하더라도 추가로 쿼리문이 나가지않음)
        List<PostListDto> list = posts.getContent().stream()
                .map(PostListDto::fromEntity)
                .toList();

        return new PageImpl<>(list,pageable,posts.getTotalElements());
    }


    // 모든 게시물중 검색(카테고리, 제목, 태그에 따른)
    public Page<PostSearchDto> getAllPostsBySearch(PostCategory category, String content, List<String> tag, Pageable pageable) {

        // Page<Post> result = null;

        Specification<Post> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("postCategory"), category)); // category는 필수

            if(content != null){
                predicates.add(criteriaBuilder.like(root.get("title"), "%"+content+"%"));
            }
            if(tag != null){
                Join<Post, PostTag> postTags = root.join("postTags", JoinType.INNER);
                predicates.add(criteriaBuilder.and(postTags.get("hashtag").in(tag)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Post> page = postRepository.findAll(specification, pageable);

        // 만약 Title로 조회한거 라면?
        // result = postRepository.findByTitleAndContentAndAuthorName(category, content, tag, pageable);

        List<PostSearchDto> list = page.getContent().stream()
                .map(PostSearchDto::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    public Page<PostListDto> getMyFriendPosts(Pageable pageable) {

        // 1. 나의 아이디 얻어오기
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 2. 내 친구인 Member 의 게시물만 가져오기
        Page<Post> posts = postRepository.findPostsFromMyFriends(memberId, pageable);

        // 3. Entity -> DTO 변환 (Fetch Join 을 했기에 Post.getMember().getNickname()을 하더라도 추가로 쿼리문이 나가지않음)
        List<PostListDto> list = posts.getContent().stream()
                .map(PostListDto::fromEntity)
                .toList();

        return new PageImpl<>(list,pageable,posts.getTotalElements());
    }
}
