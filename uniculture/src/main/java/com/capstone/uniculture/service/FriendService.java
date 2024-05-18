package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Friend.*;

import com.capstone.uniculture.dto.Member.Response.SimpleMemberProfileDto;
import com.capstone.uniculture.dto.Recommend.ProfileRecommendRequestDto;
import com.capstone.uniculture.dto.Recommend.ProfileRecommendResponseDto;
import com.capstone.uniculture.dto.Recommend.ToFlaskRequestDto;
import com.capstone.uniculture.entity.Friend.*;
import com.capstone.uniculture.entity.Member.*;
import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.entity.Notification.NotificationType;
import com.capstone.uniculture.repository.*;
import com.sun.jdi.request.InvalidRequestStateException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final MemberRepository memberRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final NotificationRepository notificationRepository;
    private final MyHobbyRepository myHobbyRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRecommendRepository friendRecommendRepository;
    private final EntityManager entityManager;

    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    private FriendRequest findFriendRequest(Long senderId, Long receiverId) {
        return friendRequestRepository.findBySenderIdAndReceiverId(senderId, receiverId)
                .orElseThrow(() -> new IllegalArgumentException("찾는 친구 요청이 존재하지 않습니다"));
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
        Member sender = memberRepository.getReferenceById(id);
        Member receiver = memberRepository.getReferenceById(toId);


        Notification notification = Notification.builder()
                .notificationType(NotificationType.FRIEND)
                .member(receiver)
                .isCheck(Boolean.FALSE)
                .content(sender.getNickname() + "님이 나에게 친구 신청을 보냈습니다!")
                .relatedNum(id)
                .build();
        notificationRepository.save(notification);

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

    public String updateFriendRequestStatus(Long senderId, String status) {
        // 1. 친구 신청 받은사람(나)
        Long receiverId = SecurityUtil.getCurrentMemberId();

        // 2. 친구 요청 찾아주기
        FriendRequest friendRequest = findFriendRequest(senderId,receiverId);

        if(status.equals("accepted")){ // 수락일때
            Member receiver = friendRequest.getReceiver();
            Member sender = friendRequest.getSender();
            // 2. 서로 친구관계를 맺어주고
            sender.addFriend(receiver);
            receiver.addFriend(sender);
            // 3. 친구 요청은 삭제시킨다
            friendRequestRepository.delete(friendRequest);
            return "친구 수락 성공";
        }
        else{ // 거절일때
            friendRequestRepository.delete(friendRequest);
            return "친구 거절 성공";
        }
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

    /**
     * Function : 친구 목록을 간단한 형식으로 조회
     * Parameter : (필수X) nickname - 닉네임 넣어서 검색할때 필요
     */
    public List<SimpleFriendResponseDto> findMyFriendInSimple(String nickname){
        // 1. 내 아이디 찾기
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 2. 내 친구목록 가져오기
        List<Member> myFriendList = friendshipRepository.findAllByFromMember_Id(memberId, nickname, null);

        // 3. DTO로 변환해서 반환
        return myFriendList.stream().map(SimpleFriendResponseDto::fromMember).collect(Collectors.toList());

        /* 과거로직 (and (:nickname is null or p.toMember.nickname LIKE %:nickname%)) 이 문법을 몰랐을때
        if(nickname != null && !nickname.isEmpty()){ friendshipRepository.findAllByFromMember_Id(id, nickname);}
        else{ friendshipRepository.findAllToMemberByFromMember_Id(id, nickname, pageable);}
         */

    }

    /**
     * Function : 친구 목록 세부적인 형식으로 조회
     * Parameter : (필수O) pageable (필수X) nickname - 닉네임 넣어서 검색할때 필요
     */
    public Page<DetailFriendResponseDto> findMyFriendInDetail(String nickname, Pageable pageable){

        // 1. 내 아이디 찾기
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 2. 내 친구목록 가져오기
        List<Member> friendList = friendshipRepository.findAllByFromMember_Id(memberId, nickname, pageable);

        // 3. DTO로 변환
        List<DetailFriendResponseDto> friendListDto = friendList.stream().map(DetailFriendResponseDto::fromMember).toList();

        // 4. 페이징된 형태로 반환
        return new PageImpl<>(friendListDto, pageable, friendListDto.size());
    }

    /**
     * Function : 나에게 온 친구 요청 조회
     */
    public List<SimpleFriendResponseDto> findFriendRequestForMe(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return friendRequestRepository.findByReceiverId(memberId).stream().map(SimpleFriendResponseDto::fromMember).toList();
        /* 과거의 안좋은 사례 -> 불필요한 오버헤드 낭비가 있음
        // 과거의 쿼리문 : SELECT fr FROM FriendRequest fr JOIN FETCH fr.sender WHERE fr.receiver.id = :receiverId
        return friendRequestRepository.findByReceiverId2(memberId)
                .stream().map(friendRequest -> {
                    return SimpleFriendResponseDto.fromMember(friendRequest.getSender());
                })
                .collect(Collectors.toList());
                */
    }


    /**
     * Function : 내가 보낸 친구 요청 조회
     */
    public List<SimpleFriendResponseDto> findFriendRequestFromMe(Long id){
        return friendRequestRepository.findBySenderId(id).stream().map(SimpleFriendResponseDto::fromMember).toList();
    }

    /**
     * Function : 내 친구중 필터 검색 - Specific 사용. 추후 QueryDSL 로 리팩토링 필요
     */
    public Page<DetailFriendResponseDto> getMyFriendBySearch2(String nickname, String hobby, String myLanguage, String wantLanguage, Integer minAge, Integer maxAge, Gender gender, Pageable pageable) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Specification<Friendship> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.equal(root.get("fromMember").get("id"), memberId));


            Join<Friendship, Member> toMemberJoin = root.join("toMember", JoinType.INNER);

            if (hobby != null) {
                Join<Member, MyHobby> myHobbyJoin = toMemberJoin.join("myHobbyList", JoinType.INNER); // 조인 수정
                predicates.add(criteriaBuilder.equal(myHobbyJoin.get("hobbyName"), hobby));
            }

            if (myLanguage != null) {
                Join<Member, MyLanguage> myLanguageJoin = toMemberJoin.join("myLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(myLanguageJoin.get("language"), myLanguage));
            }

            if (wantLanguage != null) {
                Join<Member, WantLanguage> wantLanguageJoin = toMemberJoin.join("wantLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(wantLanguageJoin.get("language"), wantLanguage));
            }

            if(nickname != null){
                predicates.add(criteriaBuilder.like(toMemberJoin.get("nickname"), "%" + nickname + "%"));
            }
            if (minAge != null && maxAge != null) {
                predicates.add(criteriaBuilder.between(toMemberJoin.get("age"), minAge, maxAge));
            }

            if (gender != null) {
                predicates.add(criteriaBuilder.equal(toMemberJoin.get("gender"), gender));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Friendship> page = friendshipRepository.findAll(specification, pageable);

        List<DetailFriendResponseDto> list = page.getContent().stream()
                .map(friendship -> DetailFriendResponseDto.fromMember(friendship.getToMember()))
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    /**
     * Function : 전체 멤버중 필터 검색 (미사용)
     */
    public Page<DetailFriendResponseDto> getMyFriendBySearch4(String hobby, String myLanguage, String wantLanguage, Integer minAge, Integer maxAge, Gender gender, Pageable pageable) {

        Long memberId = SecurityUtil.getCurrentMemberId();

        Specification<Member> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.notEqual(root.get("id"), memberId));

            if (hobby != null) {
                Join<Member, MyHobby> myHobbyJoin = root.join("myHobbyList", JoinType.INNER); // 조인 수정
                predicates.add(criteriaBuilder.equal(myHobbyJoin.get("hobbyName"), hobby));
            }

            if (myLanguage != null) {
                Join<Member, MyLanguage> myLanguageJoin = root.join("myLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(myLanguageJoin.get("language"), myLanguage));
            }

            if (wantLanguage != null) {
                Join<Member, WantLanguage> wantLanguageJoin = root.join("wantLanguages", JoinType.INNER);
                predicates.add(criteriaBuilder.equal(wantLanguageJoin.get("language"), wantLanguage));
            }

            if (minAge != null && maxAge != null) {
                predicates.add(criteriaBuilder.between(root.get("age"), minAge, maxAge));
            }

            if (gender != null) {
                predicates.add(criteriaBuilder.equal(root.get("gender"), gender));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Member> page = memberRepository.findAll(specification, pageable);

        List<DetailFriendResponseDto> list = page.getContent().stream()
                .map(member -> {
                    System.out.println("Member 한명 조회했습니다");
                    return DetailFriendResponseDto.fromMember(member);
                }).toList();

        return new PageImpl<>(list, pageable, page.getTotalElements());
    }

    /**
     * Function : 친구추천 전 하루전까지 추천된 데이터가 있는지 확인
     */
    public List<RecommendFriendResponseDto> beforeRecommendFriend(){

        // 1. 유저 아이디 받아오기
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 2. 하루전까지 추천된 기록이 있나 확인한다. -> 1을 상수로 뺄 필요성
        List<FriendRecommend> idList = friendRecommendRepository.findAlreadyRecommend(LocalDateTime.now().minusDays(1), memberId);

        // 3. 만약 추천된 기록이 있다면 그냥 그거 반환
        if(idList != null && !idList.isEmpty()){ // 캐시에서 가져올수있으면 가져오기
            return idList.stream().map(friendRecommend ->
                RecommendFriendResponseDto.fromMember(friendRecommend.getFriendRecommendPK().getToMember(), friendRecommend.getIsOpen(), friendRecommend.getSimilarity())).toList();
        }
        else{ // 4. 없으면 플라스크 서버에 요청해서 정보 받아와야함
            return recommendFriend(memberId);
        }
    }

    /**
     * Function : 친구추천 주요 로직
     */
    @Transactional
    public List<RecommendFriendResponseDto> recommendFriend(Long memberId) {

        // 1. 일단 친구추천 테이블의 모든 데이터를 삭제하자
        // -> 사유 : 계속 쌓일수있음, 나중에 스프링 배치로 하루지난 데이터 한번에 삭제가능성
        friendRecommendRepository.deleteAllByFriendRecommendPK_FromMemberId(memberId);

        // 2. 내 취미정보 찾아놓기(추후, 취미 비교를 위함)
        List<String> myHobby = myHobbyRepository.findAllByMemberId(memberId);

        // 3. 내 친구를 제외한 모든 멤버의 정보중 29명 가져오기 -> 목적, 취미, 언어는 Proxy 상태
        List<Member> memberList = memberRepository.findNonFriendMemberEdit(memberId, PageRequest.of(0, 29));

        // 4. 나까지 집어넣기(나는 필수로 들어가야됨, 비교를 위해)
        memberList.add(memberRepository.findById(memberId).get());

        // 4. 모든 멤버를 돌면서 추천에 필요한 DTO 객체로 생성하기
        List<ProfileRecommendRequestDto> recommendRequestItems = memberList.stream().map(ProfileRecommendRequestDto::fromEntity).toList();

        // 5. Flask 서버 요청 보내서 받아오기
        ProfileRecommendResponseDto responseDto = sendRequestToFlask(ToFlaskRequestDto.builder()
                .id(memberId)
                .profiles(recommendRequestItems)
                .build());

        // 6. 추천받은 정보로 친구추천 테이블에 저장 -> 캐싱의 개념
        Set<Map.Entry<Long, Long>> entrySet = responseDto.getData().getSortedList().entrySet();

        List<FriendRecommend> friendRecommends = entrySet.stream().map(set -> {
            Member toMember = memberRepository.getReferenceById(set.getKey());
            Member fromMember = memberRepository.getReferenceById(memberId);
            FriendRecommend friendRecommend = new FriendRecommend(fromMember, toMember, set.getValue());
            return friendRecommend;
        }).toList();

        friendRecommendRepository.saveAll(friendRecommends);

        // 7. 추천받은 내용 반환
        return entrySet.stream()
                .map(entry ->
                {
                    Member member = findMember(entry.getKey());
                    List<RecommendHobby> hobbies = new ArrayList<>();
                    member.getMyHobbyList().forEach(p -> {hobbies.add(new RecommendHobby(p.getHobbyName(), myHobby.contains(p.getHobbyName())));});
                    RecommendFriendResponseDto recommendFriendResponseDto = RecommendFriendResponseDto.fromMember(member, false, entry.getValue());
                    recommendFriendResponseDto.setHobbies(hobbies);
                    return recommendFriendResponseDto;
                }).toList();
    }

    /**
     * Function : 플라스크에 요청 보내는 로직
     */
    private ProfileRecommendResponseDto sendRequestToFlask(ToFlaskRequestDto requestDto) {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/json");

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<ProfileRecommendResponseDto> responseEntity = restTemplate.postForEntity(
                "http://localhost:8000/api/v1/profile/recommend",
                new HttpEntity<>(requestDto, headers),
                ProfileRecommendResponseDto.class
        );

        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        }
        else{
            throw new RuntimeException("친구 추천 서버 오류입니다");
        }
    }

    /**
     * Function : 친구 추천 남은 횟수를 반환해줍니다
     */
    public Long recommendCountCheck(){
        Long memberId = SecurityUtil.getCurrentMemberId();
        return memberRepository.countRemainCount(memberId);
    }

    /**
     * Function : 친구 추천 횟수가 남았는지 확인하고 있으면 1 감소시킵니다. -> 위의 메소드와 통합 가능성
     */
    public Boolean recommendCountDown(Long memberId){
        if(memberRepository.countRemainCount(memberId) != 0){
            memberRepository.decrementRemainCount(memberId);
            return true;
        }
        else return false;
    }

    /**
     * Function : 카드오픈
     */
    public void openProfile(Long targetId) {
        Long memberId = SecurityUtil.getCurrentMemberId();

        Member fromMember = memberRepository.getReferenceById(memberId);
        Member toMember = memberRepository.getReferenceById(targetId);

        FriendRecommend friendRecommend = friendRecommendRepository.findById(new FriendRecommendPK(fromMember, toMember))
                .orElseThrow(() -> new IllegalStateException("추천 정보가 없어요"));

        friendRecommend.changeStatus(true);
    }

    /**
     * Function : 상대와 나의 친구상태 확인 - 여러명 확인할때
     */
    public List<SimpleMemberProfileDto> CheckStatus(List<Member> members, Long memberId){
        // 2-1. 내 친구목록에 있는 ID 가져오기
        List<Long> friendsId = friendshipRepository.findMyFriendsId(memberId);
        // 2-2. 나한테 친구신청한 사람 ID 가져오기
        List<Long> senderId = friendRequestRepository.findSenderIdByReceiverId(memberId);
        // 2-3. 내가 친구신청한 사람 ID 가져오기
        List<Long> receiverId = friendRequestRepository.findReceiverIdBySenderId(memberId);

       return members.stream().map(member ->{
            SimpleMemberProfileDto profileDto = SimpleMemberProfileDto.fromMember(member);
            if(friendsId.contains(member.getId())) profileDto.changeStatue(1);
            else if(senderId.contains(member.getId())) profileDto.changeStatue(4);
            else if(receiverId.contains(member.getId())) profileDto.changeStatue(3);
            else profileDto.changeStatue(2);
           // 친구면 1, 나한테 친구 신청한 사람이면 4, 내가 친구 신청한 사람이면 3, 그외 2
            return profileDto;
        }).toList();
    }
}
