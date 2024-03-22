package com.capstone.uniculture.dto.Friend;

import com.capstone.uniculture.entity.Member.Gender;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class FriendSearchDto {

    String canLanguages;
    String wantLanguages;
    String hobby;
    Integer age;
    Gender gender;

    @Builder
    public FriendSearchDto(String canLanguages, String wantLanguages, String hobby, Integer age, Gender gender) {
        this.canLanguages = canLanguages;
        this.wantLanguages = wantLanguages;
        this.hobby = hobby;
        this.age = age;
        this.gender = gender;
    }

    public static FriendSearchDto createSearchData(String canLanguages, String wantLanguages, String hobby, Integer age, Gender gender){
        return FriendSearchDto.builder()
                .canLanguages(canLanguages)
                .wantLanguages(wantLanguages)
                .hobby(hobby)
                .age(age)
                .gender(gender)
                .build();
    }
}
