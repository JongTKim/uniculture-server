package com.capstone.uniculture.dto.Member.Request;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.MyLanguage;
import com.capstone.uniculture.entity.Member.WantLanguage;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AfterSignupDto {

    private Long id;
    private List<String> purpose;
    private String mainPurpose;
    private List<String> myHobbyList; // 취미
    private Map<String, Integer> canLanguages; // 언어
    private Map<String, Integer> wantLanguage; // 배우고 싶은 언어

}
