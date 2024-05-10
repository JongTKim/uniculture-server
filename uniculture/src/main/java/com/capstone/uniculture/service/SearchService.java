package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.SearchCountDto;
import com.capstone.uniculture.repository.MemberRepository;
import com.capstone.uniculture.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class SearchService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public SearchCountDto countSearch(String content, List<String> tag) {

        Long allCount = 0L, postCount = 0L, friendCount = 0L, memberCount = 0L;

        if(tag != null && !tag.isEmpty()){ // 태그가 딸려왔다면
            postCount = postRepository.countPostsByContentAndHashtags(content, tag);
            friendCount = 0L;
            memberCount = 0L;
        }
        else{ // 태그가 안딸려오고 내용만 왔다면
            try{
                // 로그인 상태, 멤버는 자신 제거해줘야함
                Long memberId = SecurityUtil.getCurrentMemberId();
                postCount = postRepository.countPostsByContent(content);
                friendCount = memberRepository.countMemberByMyFriend(memberId, content);
                memberCount = memberRepository.countMemberByNotMyFriend(memberId, content);
            }catch (RuntimeException e){
                // 로그아웃 상태인거니깐 친구는 무조건 0
                postCount = postRepository.countPostsByContent(content);
                friendCount = 0L;
                memberCount = memberRepository.countMemberByNickname(content);
            }
        }

        allCount = postCount + friendCount + memberCount;
        return new SearchCountDto(allCount, postCount, friendCount, memberCount);


    }
}
