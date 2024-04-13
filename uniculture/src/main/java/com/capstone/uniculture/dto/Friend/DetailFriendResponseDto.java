package com.capstone.uniculture.dto.Friend;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Member.MyLanguage;
import com.capstone.uniculture.entity.Member.WantLanguage;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class DetailFriendResponseDto {

    private Long id;
    // private String profileUrl;
    private String nickname;
    private Integer age;
    private Gender gender;
    private String introduce;
    private Map<String,Integer> canLanguages;
    private Map<String,Integer> wantLanguages;
    private List<String> hobbies;

    @Builder
    public DetailFriendResponseDto(Long id, String nickname, String introduce, Integer age, Gender gender, Map<String, Integer> canLanguages, Map<String, Integer> wantLanguages, List<String> hobbies) {
        this.id = id;
        this.nickname = nickname;
        this.introduce = introduce;
        this.age = age;
        this.gender = gender;
        this.canLanguages = canLanguages;
        this.wantLanguages = wantLanguages;
        this.hobbies = hobbies;
    }


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
                .hobbies(member.getMyHobbyList().stream().map(myHobby -> myHobby.getHobbyName()).collect(Collectors.toList()))
                .build();
    }

}