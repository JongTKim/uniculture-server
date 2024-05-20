package com.capstone.uniculture.dto.Friend;

import com.capstone.uniculture.entity.Member.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class DetailFriendResponseDto {

    private Long id;
    private String nickname;
    private Integer age;
    private Gender gender;
    private String introduce;
    private Map<String,Integer> canLanguages;
    private Map<String,Integer> wantLanguages;
    private List<String> hobbies;

    // -- 이미지 관련 --
    private String profileurl;
    private String country;

    public static DetailFriendResponseDto fromMember(Member member){
        return DetailFriendResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduce(member.getIntroduce())
                .age(member.getAge())
                .gender(member.getGender())
                .canLanguages(member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantLanguages(member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                // member.getMyHobbyList 부터 이미 SELECT * from MyHobby WHERE member_id=? 가 날라간다.
                // 그리고 가져온후 영속성 컨텍스트에 넣어두고 찾아가는 것
                .hobbies(member.getMyHobbyList().stream().map(MyHobby::getHobbyName).collect(Collectors.toList()))
                .profileurl(member.getProfileUrl())
                .country(member.getCountry())
                .build();
    }

}