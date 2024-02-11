package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.*;
import com.capstone.uniculture.entity.Member;
import com.capstone.uniculture.jwt.TokenProvider;
import com.capstone.uniculture.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
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

import java.util.Collections;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final AuthenticationManagerBuilder managerBuilder;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;


    // 회원 가입
    public String signup(MemberRequestDto memberRequestDto){
        if(memberRepository.existsByEmail(memberRequestDto.getEmail())){
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        Member member = memberRequestDto.toMember(passwordEncoder);
        memberRepository.save(member);
        return "회원가입 완료";
    }

    // 닉네임 중복
    public boolean checkNicknameUnique(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    // 로그인
    public TokenDto login(MemberRequestDto requestDto) {
        UsernamePasswordAuthenticationToken authenticationToken = requestDto.toAuthentication();

        Authentication authentication = managerBuilder.getObject().authenticate(authenticationToken);

        return tokenProvider.generateTokenDto(authentication);
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

    // 회원 수정
    public void updateUser(UpdateMemberDto updateMemberDto) {

        // 1. 예전 비밀번호가 맞는지 확인

        // 2. 현재 비밀번호 업데이트

        // 3. 닉네임 중복확인

        // 4. 닉네임 업데이트

    }

    // 회원 조회
    public MyPageDto findUser(Long id){
        Member member = memberRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
        return new MyPageDto(member);
        // 이 과정에서 프록시 -> 실객체 의 변환이 일어남
    }

    // 로그아웃
    public TokenDto logout(){
        // 1. 새로운 토큰을 만들어서 유효기간을 아주짧게 설정한후 전송

        // 2. 클라이언트는 그 토큰을 받아서 로컬스토리지에 저장, 다음에 전송하게되면 유효기간 만료로 로그인 상태 X가됨
        return new TokenDto();
    }

    // 회원 삭제
    public String deleteUser(Long id){
        memberRepository.deleteById(id);
        return "회원 탈퇴 완료";
    }

    // 프로필 변경창 선택시 현재 프로필(초기화면)
    public MyPageDto EditUserProfile(Long id){
        Member member = memberRepository.findById(id).orElseThrow(()->new IllegalArgumentException("찾는 사용자가 존재하지 않습니다."));
        return new MyPageDto(member);
    }

    // 프로필 변경
    public String UpdateUserProfile(Long id, UpdateMemberDto updateMemberDto){
        if(updateMemberDto.getExPassword() != null && updateMemberDto.getNewPassword() != null){
            changePassword(id, updateMemberDto.getExPassword(), updateMemberDto.getNewPassword());
        }
        if(updateMemberDto.getNickname() != null){
            changeNickname(id, updateMemberDto.getNickname());
        }
        return "수정 성공";
    }
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

    // 개인정보 변경창 선택시 현재 개인정보(초기화면)

}
