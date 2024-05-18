package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Member.Response.SimpleMemberProfileDto;
import com.capstone.uniculture.dto.Post.Response.PostDetailDto;
import com.capstone.uniculture.dto.Post.Response.PostSearchDto;
import com.capstone.uniculture.dto.SearchCountDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.entity.Post.PostTag;
import com.capstone.uniculture.repository.FriendRequestRepository;
import com.capstone.uniculture.repository.FriendshipRepository;
import com.capstone.uniculture.repository.MemberRepository;
import com.capstone.uniculture.repository.PostRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final FriendService friendService;
    private final FriendshipRepository friendshipRepository;

    public SearchCountDto countSearch(String content, List<String> tag) {

        Long allCount = 0L, postCount = 0L, friendCount = 0L, memberCount = 0L;

        if(tag != null && !tag.isEmpty()){ // 태그가 딸려왔다면
            postCount = postRepository.countPosts(content, tag);
            friendCount = 0L;
            memberCount = 0L;
        }
        else if(content != null && !content.isEmpty()){ // 태그가 안딸려오고 내용만 왔다면
            try{
                // 로그인 상태, 멤버는 자신 제거해줘야함
                Long memberId = SecurityUtil.getCurrentMemberId();
                postCount = postRepository.countPostsByContent(content);
                friendCount = memberRepository.countMemberByMyFriend(memberId, content);
                memberCount = memberRepository.countMemberByNickname(memberId, content);
            }catch (RuntimeException e){
                // 로그아웃 상태인거니깐 친구는 무조건 0
                postCount = postRepository.countPostsByContent(content);
                friendCount = 0L;
                memberCount = memberRepository.countMemberByNickname(null, content);
            }
        }

        allCount = postCount + friendCount + memberCount;
        return new SearchCountDto(allCount, postCount, friendCount, memberCount);


    }

    // 내가 로그인 상태이면 친구 여부 계산, 로그아웃 상태이면 친구 여부는 모두 0
    public Page<SimpleMemberProfileDto> searchMember(String nickname, Pageable pageable) {

        List<Member> members;
        List<SimpleMemberProfileDto> memberProfileDto;

        try {
            Long memberId = SecurityUtil.getCurrentMemberId();
            // 로그인 상태일경우
            members = memberRepository.findAllByNicknameNotMine(memberId, nickname, pageable);
            memberProfileDto = friendService.CheckStatus(members, memberId);
        }catch(RuntimeException e){
            // 로그인 상태가 아닐경우
            members = memberRepository.findAllByNickname(nickname, pageable);
            memberProfileDto = members.stream().map(SimpleMemberProfileDto::fromMember).toList();
        }
        return new PageImpl<>(memberProfileDto, pageable, members.size());
    }

    public Page<SimpleMemberProfileDto> searchFriend(String nickname, Pageable pageable) {

        Long memberId = SecurityUtil.getCurrentMemberId();

        List<Member> friendList = friendshipRepository.findAllByFromMember_Id(memberId, nickname, pageable);

        List<SimpleMemberProfileDto> friendListDto = friendList.stream().map(SimpleMemberProfileDto::fromMember).toList();

        return new PageImpl<>(friendListDto, pageable, friendList.size());
    }


    public Page<PostDetailDto> searchPost(String content, List<String> tag, Pageable pageable) {

        Specification<Post> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("postCategory"), PostCategory.NORMAL)); // category는 필수

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

        List<PostDetailDto> list = page.getContent().stream().map(PostDetailDto::fromEntity).toList();

        return new PageImpl<>(list, pageable, list.size());

    }
}
