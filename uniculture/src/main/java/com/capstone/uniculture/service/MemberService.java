package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.*;
import com.capstone.uniculture.entity.*;
import com.capstone.uniculture.jwt.TokenProvider;
import com.capstone.uniculture.repository.FileRepository;
import com.capstone.uniculture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.processing.Find;
import org.hibernate.sql.Update;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final AuthenticationManagerBuilder managerBuilder;
    private final MemberRepository memberRepository;
    private final FileRepository fileRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    @Value("${file.upload-dir}")
    private String uploadDir;

    private final MyHobbyService myHobbyService;
    private final MyLanguageService myLanguageService;
    private final WantLanguageService wantLanguageService;


    // 회원 가입
    public String signup(MemberRequestDto memberRequestDto){
        // 1. 이메일 중복 확인
        if(memberRepository.existsByEmail(memberRequestDto.getEmail())){
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        // 2. 패스워드 암호화, DTO -> Entity
        Member member = memberRequestDto.toMember(passwordEncoder);
        // 3. DB에 저장
        memberRepository.save(member);
        return "회원가입 완료";
    }

    // 회원 조회
    public MyPageDto findUser(Long id){
        // 1. ID를 기반으로 DB 에서 Member 객체 생성
        Member member = FindMember(id);
        // 2. 리턴해줄 DTO 생성. 이 과정에서 프록시 -> 실객체 의 변환이 일어남
        // ** 이 부분도 따로 설정 필요
        return new MyPageDto(member);
    }

    // 회원 수정 中 프로필 수정 초기화면
    public UpdateProfileDto EditUserProfile(Long id){
        Member member = FindMember(id);
        return new UpdateProfileDto(member);
    }


    // 회원 수정 中 프로필 수정
    public String UpdateUserProfile(Long id, UpdateProfileDto updateProfileDto, MultipartFile profileImg) throws IOException {
        Member member = FindMember(id);
        // 1. 원래 내용 삭제
        member.getMyHobbyList().forEach(myHobbyService::delete);
        member.getMyLanguages().forEach(myLanguageService::delete);
        member.getWantLanguages().forEach(wantLanguageService::delete);

        // 2. 새로운 내용 투입
        if(!updateProfileDto.getMyHobbyList().isEmpty()) {
            List<MyHobby> newMyHobby = updateProfileDto.getMyHobbyList()
                    .stream().map(myHobby -> new MyHobby(member, myHobby))
                    .collect(Collectors.toList());
            myHobbyService.createByList(newMyHobby);
        }
        if(!updateProfileDto.getMyLanguages().isEmpty()){
            List<MyLanguage> newMyLanguage = updateProfileDto.getMyLanguages()
                    .entrySet()
                    .stream().map(myLanguage -> new MyLanguage(member, myLanguage.getKey(), myLanguage.getValue()))
                    .collect(Collectors.toList());
        }
        if(!updateProfileDto.getWantLanguage().isEmpty()){
            List<WantLanguage> newWantLanguage = updateProfileDto.getWantLanguage()
                    .entrySet()
                    .stream().map(wantLanguage -> new WantLanguage(member, wantLanguage.getKey(), wantLanguage.getValue()))
                    .collect(Collectors.toList());
        }

        // 3. 프사 설정
        String fileName = UUID.randomUUID().toString() + "_" + profileImg.getOriginalFilename();
        String filepath = uploadDir + File.separator + fileName;

        File file = new File(filepath);
        profileImg.transferTo(file);

        Files files = new Files(fileName, filepath);
        fileRepository.save(files);

        member.setProfileUrl(updateProfileDto.getProfileUrl());

        // 4. 소개 설정
        member.setIntroduce(updateProfileDto.getIntroduce());

        return "수정 성공";
    }

    // 회원 수정 中 개인정보 수정 초기화면
    public MyPageDto EditUserInformation(Long id){
        Member member = FindMember(id);
        return new MyPageDto(member);
    }

    // 회원 수정 中 개인정보 수정
    public String UpdateUserInformation(Long id, UpdateMemberDto updateMemberDto){
        // 1. 비밀번호 수정사항이 있는지 확인
        if(updateMemberDto.getExPassword() != null && updateMemberDto.getNewPassword() != null){
            // 1-1. 비밀번호 교체 로직 실행. 만약 예전 비밀번호가 틀렸으면 예외발생
            changePassword(id, updateMemberDto.getExPassword(), updateMemberDto.getNewPassword());
        }
        // 2. 닉네임 수정사항이 있는지 확인
        if(updateMemberDto.getNickname() != null){
            // 2-1. 닉네임 교체 로직 실행. 만약 이미 존재하는 이메일이라면 예외발생
            changeNickname(id, updateMemberDto.getNickname());
        }
        return "수정 성공";
    }

    // 회원 삭제
    // ** 추후 순차 삭제로 바꿀 필요
    public String deleteUser(Long id){
        memberRepository.deleteById(id);
        return "회원 탈퇴 완료";
    }

    //---------- MEMBER CRUD CLEAR ----------//

    private Member FindMember(Long id) {
        return memberRepository.findById(id).orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
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
    public String changePassword(Long memberId, String exPassword, String newPassword) {
        Member member = memberRepository.findById(memberId).get();
        if(!passwordEncoder.matches(exPassword, member.getPassword())){
            throw new RuntimeException("비밀번호가 맞지 않습니다");
        }
        member.setPassword(passwordEncoder.encode(newPassword));
        return "수정이 완료되었습니다";
    }
    public String changeNickname(Long memberId, String nickname) {
        if(memberRepository.existsByNickname(nickname)){
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }
        memberRepository.findById(memberId).get().setNickname(nickname);
        return "수정이 완료되었습니다";
    }


}
