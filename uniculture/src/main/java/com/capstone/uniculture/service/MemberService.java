package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.*;
import com.capstone.uniculture.dto.Member.MemberRequestDto;
import com.capstone.uniculture.dto.Member.ResponseProfileDto;
import com.capstone.uniculture.dto.Member.UpdateMemberDto;
import com.capstone.uniculture.dto.Member.UpdateProfileDto;
import com.capstone.uniculture.entity.Member.*;
import com.capstone.uniculture.jwt.TokenProvider;
import com.capstone.uniculture.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final AuthenticationManagerBuilder managerBuilder;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final PostRepository postRepository;
    private final FriendshipRepository friendshipRepository;
    private final FriendRequestRepository friendRequestRepository;

    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    /* @Value("${file.upload-dir}")
    private String uploadDir;*/

    private final MyHobbyService myHobbyService;
    private final MyLanguageService myLanguageService;
    private final WantLanguageService wantLanguageService;


    // 회원 가입
    public String signup(MemberRequestDto memberRequestDto){

        // 2. 패스워드 암호화, DTO -> Entity
        Member member = memberRequestDto.toMember(passwordEncoder);
        // 3. DB에 저장
        memberRepository.save(member);
        return "회원가입 완료";
    }

    public boolean checkEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }

    // 자신 조회
    public ResponseProfileDto findUser(Long id) throws IOException {
        // 1. ID를 기반으로 DB 에서 Member 객체 생성
        Member member = findMember(id);
        // 2. 사용이유는 프록시객체 때문에
        Integer friendRequestNum = friendRequestRepository.countByMember(member);
        Integer postNum = postRepository.countByMember(member);
        Integer friendNum = friendshipRepository.countByMember(member);
        // 3. 리턴해줄 DTO 생성. 이 과정에서 컬렉션 필드에서는 프록시 -> 실객체 의 변환이 일어남
        return ResponseProfileDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduce(member.getIntroduce())
                .receiverequestnum(friendRequestNum)
                .age(member.getAge())
                .gender(member.getGender())
                .postnum(postNum)
                .canlanguages(member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantlanguages(member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                .hobbies(member.getMyHobbyList().stream().map(myHobby -> myHobby.getHobbyName()).collect(Collectors.toList()))
                .friendnum(friendNum)
                .build();
    }

    // 타인 조회 - 로그인 상태일때
    public ResponseProfileDto findOtherLogin(Long id, Long myId) throws IOException {
        // 1. ID를 기반으로 DB 에서 Member 객체 생성
        Member member = findMember(id);
        // 2. 사용이유는 프록시객체 때문에
        Integer postNum = postRepository.countByMember(member);
        Integer friendNum = friendshipRepository.countByMember(member);

        Integer friendStatus;
        if(friendshipRepository.existsByFromMember_IdAndToMember_Id(id,myId)){ // 친구면
            friendStatus = 1;
        }
        else{
            if(friendRequestRepository.existsBySender_IdAndReceiver_Id(id,myId)){ // 상대가 나한테 친구요청을 보냈으면
                friendStatus = 4;
            } else if (friendRequestRepository.existsBySender_IdAndReceiver_Id(myId, id)) { // 내가 상대한테 보낸게 있으면
                friendStatus = 3;
            } else{ // 둘다 보낸거 없고 친구 상태 아니면
              friendStatus = 2;
            }
        }

        // 3. 리턴해줄 DTO 생성. 이 과정에서 컬렉션 필드에서는 프록시 -> 실객체 의 변환이 일어남
        return ResponseProfileDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduce(member.getIntroduce())
                .age(member.getAge())
                .gender(member.getGender())
                .postnum(postNum)
                .canlanguages(member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantlanguages(member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                .hobbies(member.getMyHobbyList().stream().map(myHobby -> myHobby.getHobbyName()).collect(Collectors.toList()))
                .friendnum(friendNum)
                .friendstatus(friendStatus)
                .build();
    }

    // 타인 조회 - 로그아웃 상태일때
    public ResponseProfileDto findOtherLogout(String nickname) throws IOException {
        // 1. ID를 기반으로 DB 에서 Member 객체 생성
        Member member = findMemberByNickname(nickname);
        // 2. 사용이유는 프록시객체 때문에
        Integer postNum = postRepository.countByMember(member);
        Integer friendNum = friendshipRepository.countByMember(member);
        // 3. 리턴해줄 DTO 생성. 이 과정에서 컬렉션 필드에서는 프록시 -> 실객체 의 변환이 일어남
        return ResponseProfileDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduce(member.getIntroduce())
                .age(member.getAge())
                .gender(member.getGender())
                .postnum(postNum)
                .canlanguages(member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantlanguages(member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                .hobbies(member.getMyHobbyList().stream().map(myHobby -> myHobby.getHobbyName()).collect(Collectors.toList()))
                .friendnum(friendNum)
                .friendstatus(2)
                .build();
    }



    // 회원 수정 中 프로필 수정 초기화면
    public UpdateProfileDto EditUserProfile(Long id){
        Member member = findMember(id);
        return new UpdateProfileDto(member);
    }


    // 회원 수정 中 프로필 수정
    public String UpdateUserProfile(Long id, UpdateProfileDto updateProfileDto) throws IOException {
        Member member = findMember(id);
        // 1. 원래 내용 삭제

        List<MyHobby> myHobbyList = member.getMyHobbyList();
        List<MyLanguage> myLanguages = member.getMyLanguages();
        List<WantLanguage> wantLanguages = member.getWantLanguages();

        myHobbyList.forEach(myHobbyService::delete);
        myLanguages.forEach(myLanguageService::delete);
        wantLanguages.forEach(wantLanguageService::delete);

        myHobbyList.clear();
        myLanguages.clear();
        wantLanguages.clear();


        // 2. 새로운 내용 투입
        if(updateProfileDto.getMyHobbyList() != null && !updateProfileDto.getMyHobbyList().isEmpty()) {
            List<MyHobby> newMyHobby = updateProfileDto.getMyHobbyList()
                    .stream().map(myHobby -> new MyHobby(member, myHobby))
                    .collect(Collectors.toList());
            myHobbyService.createByList(newMyHobby);
        }
        if(updateProfileDto.getMyLanguages() != null && !updateProfileDto.getMyLanguages().isEmpty()){
            List<MyLanguage> newMyLanguage = updateProfileDto.getMyLanguages()
                    .entrySet()
                    .stream().map(myLanguage -> new MyLanguage(member, myLanguage.getKey(), myLanguage.getValue()))
                    .collect(Collectors.toList());
            myLanguageService.createByList(newMyLanguage);
        }
        if(updateProfileDto.getWantLanguage() != null && !updateProfileDto.getWantLanguage().isEmpty()){
            List<WantLanguage> newWantLanguage = updateProfileDto.getWantLanguage()
                    .entrySet()
                    .stream().map(wantLanguage -> new WantLanguage(member, wantLanguage.getKey(), wantLanguage.getValue()))
                    .collect(Collectors.toList());
            wantLanguageService.createByList(newWantLanguage);
        }

        /*
        // 3. 프사 설정
        if(!profileImg.isEmpty()) {
            String fileName = UUID.randomUUID().toString() + "_" + profileImg.getOriginalFilename();
            String filepath = "/src/main/resources/static" + File.separator + fileName;

            File file = new File(filepath);
            profileImg.transferTo(file);

            Files files = new Files(fileName, filepath);
            fileRepository.save(files);

            member.setProfileUrl(updateProfileDto.getProfileUrl());
        }
        */

        // 4. 소개 설정
        member.setIntroduce(updateProfileDto.getIntroduce());

        return "수정 성공";
    }

    // 회원 수정 中 개인정보 수정 초기화면
    public UpdateMemberDto EditUserInformation(Long id) throws IOException {
        Member member = findMember(id);
        return new UpdateMemberDto(member);
    }

    // 회원 수정 中 개인정보 수정
    public ResponseEntity UpdateUserInformation(Long id, UpdateMemberDto updateMemberDto){
        // 0. 멤버찾기
        Member member = findMember(id);
        // 1. 비밀번호 수정사항이 있는지 확인
        if(updateMemberDto.getExPassword() != null && updateMemberDto.getNewPassword() != null){
            // 1-1. 비밀번호 교체 로직 실행. 만약 예전 비밀번호가 틀렸으면 400 예외발생
            if (this.checkPassword(member.getId(), updateMemberDto.getExPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현재 비밀번호가 일치하지 않습니다");
            }
            // 1-2. 비밀번호 맞을시 교체 로직
            member.setPassword(passwordEncoder.encode(updateMemberDto.getNewPassword()));
        }
        // 2. 닉네임 수정사항이 있는지 확인
        if(updateMemberDto.getNickname() != null){
            // 2-1. 닉네임 교체 로직 실행. 만약 이미 존재하는 이메일이라면 예외발생
            member.setNickname(updateMemberDto.getNickname());
        }
        // 3. 성별과 나이수정 - 설정안하더라도 기본값으로 들어가는 것들이라 수정사항 확인필요 X
        member.setAge(updateMemberDto.getAge());
        member.setGender(updateMemberDto.getGender());
        member.setBorn(LocalDate.of(updateMemberDto.getYear(), updateMemberDto.getMonth(), updateMemberDto.getDay()));

        return ResponseEntity.ok("수정 성공");
    }

    // 회원 삭제
    // ** 추후 순차 삭제로 바꿀 필요
    public String deleteUser(Long id){
        memberRepository.deleteById(id);
        return "회원 탈퇴 완료";
    }

    //---------- MEMBER CRUD CLEAR ----------//


    private Member findMember(Long id) {
        return memberRepository.findById(id).orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    public Member findMemberByNickname(String nickname){
        return memberRepository.findByNickname(nickname).orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
    }

    // 닉네임 중복
    public boolean checkNicknameUnique(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 로그인
    public TokenDto login(MemberRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();
        // 여기서 비밀번호를 조회하고 인증된 객체를 Authentication 에 넣어줌
        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);
        // 인증 객체를 가지고 토큰 생성
        return tokenProvider.generateTokenDto(authentication);
    }

    // 로그아웃
    public String logout(){
        // 1. 새로운 토큰을 만들어서 유효기간을 아주짧게 설정한후 전송
        return tokenProvider.generateShortTokenDto();
        // 2. 클라이언트는 그 토큰을 받아서 로컬스토리지에 저장, 다음에 전송하게되면 유효기간 만료로 로그인 상태 X가됨
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .map(this::createUserDetails)
                .orElseThrow(() -> new UsernameNotFoundException(username + "을 DB 에서 찾을수 없습니다"));
    }

    private UserDetails createUserDetails(Member member) {
        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(member.getAuthority().toString());

        return new User(
                String.valueOf(member.getId()),
                member.getPassword(),
                Collections.singleton(grantedAuthority)
        );
    }

    // 프로필 변경 로직 中 비밃번호 변경, 닉네임 변경

    public boolean checkPassword(Long memberId, String exPassword){
        Member member = findMember(memberId);
        if(!passwordEncoder.matches(exPassword, member.getPassword())){ // 비밀번호가 일치하지 않으면 true 반환
            return true;
        }
        return false; // 일치하면 false 반환
    }



}
