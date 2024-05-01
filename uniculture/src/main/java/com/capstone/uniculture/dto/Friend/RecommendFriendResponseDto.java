package com.capstone.uniculture.dto.Friend;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.MyLanguage;
import com.capstone.uniculture.entity.Member.WantLanguage;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SecondaryRow;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class RecommendFriendResponseDto {

    private Long id;
    // private String profileUrl;
    private String nickname;
    private Integer age;
    private Gender gender;
    private String introduce;
    private Map<String,Integer> canLanguages;
    private Map<String,Integer> wantLanguages;
    private List<RecommendHobby> hobbies;
    private Boolean isOpen;

    @Builder
    public RecommendFriendResponseDto(Long id, String nickname, String introduce, Integer age, Gender gender,
                                      Map<String, Integer> canLanguages, Map<String, Integer> wantLanguages,
                                      Boolean isOpen) {
        this.id = id;
        this.nickname = nickname;
        this.introduce = introduce;
        this.age = age;
        this.gender = gender;
        this.canLanguages = canLanguages;
        this.wantLanguages = wantLanguages;
        this.isOpen = isOpen;
    }


    public static RecommendFriendResponseDto fromMember(Member member, Boolean isOpen){
        return RecommendFriendResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduce(member.getIntroduce())
                .age(member.getAge())
                .gender(member.getGender())
                .canLanguages(member.getMyLanguages().stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantLanguages(member.getWantLanguages().stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                .isOpen(isOpen)
                .build();
    }

}