package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Friend.DetailFriendResponseDto;
import com.capstone.uniculture.dto.Friend.FriendResponseDto;
import com.capstone.uniculture.dto.Friend.FriendSearchDto;
import com.capstone.uniculture.entity.Friend.FriendRequest;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Friend.RequestStatus;
import com.capstone.uniculture.repository.FriendRequestRepository;
import com.capstone.uniculture.repository.FriendshipRepository;
import com.capstone.uniculture.repository.MemberRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;

    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(
                ()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    private FriendRequest findFriendRequest(Long senderId, Long receiverId) {
        FriendRequest friendRequest = friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId).orElseThrow(
                () -> new IllegalArgumentException("찾는 친구 요청이 존재하지 않습니다")
        );
        return friendRequest;
    }

    // 친구 요청 신청
    public ResponseEntity friendRequest(Long id, Long toId){

        // 1. 먼저, 두 사람이 이미 친구관계가 아닌지 확인
        if(friendshipRepository.existsByFromMember_IdAndToMember_Id(id, toId)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 친구관계 입니다");
        }
        // 2. 두 사람이 서로에게 친구신청을 한것이 있는지 확인(둘다 없어야함)
        if(friendRequestRepository.existsBySender_IdAndReceiver_Id(id, toId) ||
        friendRequestRepository.existsBySender_IdAndReceiver_Id(toId,id)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("친구신청 한 것이 있습니다");
        }

        // 2. 두 사람(신청자, 상대)의 정보 획득
        Member sender = findMember(id);
        Member receiver = findMember(toId);

        // 3. 친구신청 해주기
        FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);

        return ResponseEntity.ok("친구 요청에 성공하였습니다");
    }

    // 친구 요청 취소
    public String revokeFriendRequest(Long senderId, Long receiverId){
        FriendRequest friendRequest = findFriendRequest(senderId, receiverId);
        friendRequestRepository.delete(friendRequest);
        return "친구 요청 취소에 성공하였습니다";
    }

    // 친구 삭제 - 양방향 모두 삭제해줘야한다
    public String deleteFriend(Long memberId, Long targetId) {
        if(friendshipRepository.existsByFromMember_IdAndToMember_Id(memberId, targetId) &&
                friendshipRepository.existsByFromMember_IdAndToMember_Id(targetId,memberId))
        {
            friendshipRepository.delete(
                    friendshipRepository.findByFromMember_IdAndToMember_Id(memberId, targetId)
            );
            friendshipRepository.delete(
                    friendshipRepository.findByFromMember_IdAndToMember_Id(targetId, memberId)
            );
        }else{
            throw new InvalidRequestStateException("두 상대가 서로 친구 관계가 아닙니다");
        }
        return "친구 삭제 성공";
    }

    // 친구 요청 수락 - 신청한사람의 닉네임이 날아온다고 가정한다
    public String acceptFriendRequest(Long receiverId, Long senderId) {
        FriendRequest friendRequest = findFriendRequest(senderId,receiverId);
        // 1. 신청자와 수락자 두 Member 를 찾아주고
        Member receiver = friendRequest.getReceiver();
        Member sender = friendRequest.getSender();
        // 2. 서로 친구관계를 맺어주고
        sender.addFriend(receiver);
        receiver.addFriend(sender);
        // 3. 친구 요청은 삭제시킨다
        friendRequestRepository.delete(friendRequest);
        return "친구 수락 성공";
    }

    // 친구 요청 거절
    public String rejectFriendRequest(Long receiverId, Long senderId){
        FriendRequest friendRequest = findFriendRequest(senderId, receiverId);
        friendRequestRepository.delete(friendRequest);
        // Cascade 옵션에 의해 Receiver 쪽의 Request 는 자동으로 삭제된다.
        return "친구 거절 성공";
    }

    // Stream 은 값이 Null 이더라도 빈 리스트를 반환해주어 NullPointerException 을 방지할수있다.
    // 친구 목록 조회
    public List<FriendResponseDto> listOfFriends(Long id){
        /*return friendshipRepository.findById(id)
                .stream().map(friendship ->
                {
                    Member toMember = friendship.getToMember();
                    return FriendResponseDto.fromMember(toMember);
                }).collect(Collectors.toList());*/

        return findMember(id).getFriends()
                .stream().map(FriendResponseDto::fromMember)
                .collect(Collectors.toList());
    }

    public Page<DetailFriendResponseDto> listOfFriends2(String name, Long id, Pageable pageable){

        Page<Member> friendList = null;

        if(name != null){ // 만약, 이름 검색조건이 입력되었다면
            friendList = friendshipRepository.findFriendsByNickname(id, name, pageable);
        }
        else { // 아니면 내 친구 전체 상세조회
            friendList = friendshipRepository.findAllByFromMember_Id(id, pageable);
        }
        List<DetailFriendResponseDto> list = friendList.stream().map(DetailFriendResponseDto::fromMember).toList();
        return new PageImpl<>(list, pageable, friendList.getTotalElements());
    }
    // 나에게 온 친구 신청 목록 조회
    public List<FriendResponseDto> listOfFriendRequest(Long id){
        return friendRequestRepository.findByReceiverId(id)
                .stream().map(friendRequest -> FriendResponseDto.fromMember(friendRequest.getSender()))
                .collect(Collectors.toList());
    }

    // 내가 보낸 친구 신청목록 조회
    public List<FriendResponseDto> listOfMyFriendRequest(Long id){
        return friendRequestRepository.findBySenderId(id)
                .stream().map(friendRequest -> FriendResponseDto.fromMember(friendRequest.getReceiver()))
                .collect(Collectors.toList());
    }


    public Page<DetailFriendResponseDto> getMyFriendBySearch(FriendSearchDto searchData, Pageable pageable) {

        Long memberId = SecurityUtil.getCurrentMemberId();

        Page<Member> result = null;

        if(searchData.getMax_age() != null && searchData.getMax_age() != null){ // 둘다 null 이 아닐때만
            result = friendshipRepository.findFriendsByAge(memberId, searchData.getMin_age(), searchData.getMax_age(), pageable);
        } else if(searchData.getGender() != null){
            result = friendshipRepository.findFriendsByGender(memberId, searchData.getGender(), pageable);
        } else if(searchData.getHobby() != null){
            result = friendshipRepository.findFriendsByHobbyName(memberId, searchData.getHobby(), pageable);
        } else if(searchData.getCanLanguages() != null){
            result = friendshipRepository.findFriendsByMyLanguage(memberId, searchData.getCanLanguages(), pageable);
        } else if(searchData.getWantLanguages() != null){
            result = friendshipRepository.findFriendsByWantLanguage(memberId, searchData.getWantLanguages(), pageable);
        }

        List<DetailFriendResponseDto> list = null;

        // 하나도 입력되지 않을 경우를 대비
        if(result != null) {
            list = result.getContent().stream()
                    .map(DetailFriendResponseDto::fromMember).toList();
        }

        return new PageImpl<>(list, pageable, result.getTotalElements());


    }
}
