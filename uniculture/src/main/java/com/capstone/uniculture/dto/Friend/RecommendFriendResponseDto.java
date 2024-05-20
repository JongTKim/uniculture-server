package com.capstone.uniculture.dto.Friend;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.MyLanguage;
import com.capstone.uniculture.entity.Member.WantLanguage;
import lombok.*;
import org.hibernate.annotations.SecondaryRow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class RecommendFriendResponseDto {

    private Long id;
    private String nickname;
    private Integer age;
    private Gender gender;
    private String introduce;
    private Map<String,Integer> canLanguages;
    private Map<String,Integer> wantLanguages;
    private List<RecommendHobby> hobbies;

    // 상세 친구 조회와 비교에서 추가된 내용
    private Long similarity;
    private Boolean isOpen;

    // -- 이미지 관련 --
    private String profileurl;
    private String country;



    public static RecommendFriendResponseDto fromMember(Member member, Boolean isOpen, Long similarity){
        return RecommendFriendResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduce(member.getIntroduce())
                .age(member.getAge())
                .gender(member.getGender())
                .canLanguages(member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantLanguages(member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                .similarity(similarity)
                .isOpen(isOpen)
                .profileurl(member.getProfileUrl())
                .country(member.getCountry())
                .build();
    }

}