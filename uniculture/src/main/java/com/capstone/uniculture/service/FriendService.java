package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.Friend.FriendResponseDto;
import com.capstone.uniculture.entity.Friend.FriendRequest;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Friend.RequestStatus;
import com.capstone.uniculture.repository.FriendRequestRepository;
import com.capstone.uniculture.repository.FriendshipRepository;
import com.capstone.uniculture.repository.MemberRepository;
import com.sun.jdi.request.InvalidRequestStateException;
import lombok.RequiredArgsConstructor;
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
    public String friendRequest(Long id, Long toId){

        // 1. 먼저, 두 사람이 이미 친구관계가 아닌지 확인
        if(friendshipRepository.existsByFromMember_IdAndToMember_Id(id, toId)){
            throw new InvalidRequestStateException("이미 친구관계 입니다");
        }
        // 2. 두 사람이 서로에게 친구신청을 한것이 있는지 확인(둘다 없어야함)
        if(friendRequestRepository.existsBySender_IdAndReceiver_Id(id, toId) ||
        friendRequestRepository.existsBySender_IdAndReceiver_Id(toId,id)){
            throw new InvalidRequestStateException("서로에게 요청된 친구신청이 이미 존재합니다");
        }

        // 2. 두 사람(신청자, 상대)의 정보 획득
        Member sender = findMember(id);
        Member receiver = findMember(toId);

        // 3. 친구신청 해주기
        FriendRequest friendRequest = new FriendRequest(sender, receiver, RequestStatus.PENDING);
        friendRequestRepository.save(friendRequest);

        return "친구 요청에 성공하였습니다";
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
        return findMember(id).getFriends()
                .stream().map(member -> FriendResponseDto.fromMember(member))
                .collect(Collectors.toList());
    }
    // 나에게 온 친구 신청 목록 조회
    public List<FriendResponseDto> listOfFriendRequest(Long id){
        return findMember(id).getReceivedRequests()
                .stream().map(friendRequest -> FriendResponseDto.fromMember(friendRequest.getSender()))
                .collect(Collectors.toList());
    }

    // 내가 보낸 친구 신청목록 조회
    public List<FriendResponseDto> listOfMyFriendRequest(Long id){
        return friendRequestRepository.findBySenderId(id)
                .stream().map(friendRequest -> FriendResponseDto.fromMember(friendRequest.getReceiver()))
                .collect(Collectors.toList());
    }


}
