package com.capstone.uniculture.dto.Recommend;

import com.capstone.uniculture.entity.Member.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRecommendRequestDto {
    private Long id;
    private List<String> purpose;
    private List<String> interest;
    private List<String> can;
    private List<String> want;

    public static ProfileRecommendRequestDto fromEntity(Member member){
        return ProfileRecommendRequestDto.builder()
                .id(member.getId())
                .purpose(member.getPurpose().stream().map(Purpose::getPurpose).toList())
                .interest(member.getMyHobbyList().stream().map(MyHobby::getHobbyName).toList())
                .can(member.getMyLanguages().stream().map(MyLanguage::getLanguage).toList())
                .want(member.getWantLanguages().stream().map(WantLanguage::getLanguage).toList())
                .build();
    }
}
