package com.capstone.uniculture.dto.Member.Request;

import com.capstone.uniculture.entity.Member.Authority;
import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequestDto {
    private String email;
    private String password;
    private String nickname;
    private String country;
    private Gender gender;
    private Integer age;
    private Integer year;
    private Integer month;
    private Integer day;

    public Member toMember(PasswordEncoder passwordEncoder) { // DTO -> Entity
        return Member.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .authority(Authority.ROLE_USER)
                .country(country)
                .gender(gender)
                .age(age)
                .born(LocalDate.of(year,month,day))
                .build();
    }
}