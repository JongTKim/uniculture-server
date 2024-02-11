package com.capstone.uniculture.service;

import com.capstone.uniculture.dto.MemberRequestDto;
import com.capstone.uniculture.dto.TokenDto;
import com.capstone.uniculture.dto.UpdateMemberDto;
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


    public String signup(MemberRequestDto memberRequestDto){
        if(memberRepository.existsByEmail(memberRequestDto.getEmail())){
            throw new RuntimeException("이미 가입되어 있는 유저입니다");
        }
        Member member = memberRequestDto.toMember(passwordEncoder);
        memberRepository.save(member);
        return "회원가입 완료";
    }

    public boolean checkNicknameUnique(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

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

    public void updateUser(UpdateMemberDto updateMemberDto) {

        // 1. 예전 비밀번호가 맞는지 확인

        // 2. 현재 비밀번호 업데이트

        // 3. 닉네임 중복확인

        // 4. 닉네임 업데이트

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
}
