package com.capstone.uniculture.dto.Member.Response;

import com.capstone.uniculture.dto.Friend.DetailFriendResponseDto;
import com.capstone.uniculture.entity.Member.*;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {
    //아이디 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Long id;
    // 프로필 사진 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private String profileurl;
    private String country;
    // 닉네임 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private String nickname;
    // 한줄소개 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private String introduce;
    // 나이 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Integer age;
    // 성별 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Gender gender;
    // 게시물 수 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Integer postnum;
    // 친구 수 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Integer friendnum;
    // 친구 신청 받은 수 (ME)
    private Integer receiverequestnum;
    // 친구 여부 (LOGIN-OTHER)
    private Boolean isfriend;
    private Integer friendstatus;
    // 언어 능력 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Map<String,Integer> canlanguages;
    // 배우고 싶은 언어 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Map<String,Integer> wantlanguages;
    // 취미 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private List<String> hobbies;


    public static ProfileResponseDto fromMember(Member member){
        return ProfileResponseDto.builder()
                .id(member.getId())
                .nickname(member.getNickname())
                .introduce(member.getIntroduce())
                .age(member.getAge())
                .gender(member.getGender())
                .canlanguages(member.getMyLanguages()
                        .stream().collect(Collectors.toMap(MyLanguage::getLanguage, MyLanguage::getLevel)))
                .wantlanguages(member.getWantLanguages()
                        .stream().collect(Collectors.toMap(WantLanguage::getLanguage, WantLanguage::getLevel)))
                .hobbies(member.getMyHobbyList()
                        .stream().map(MyHobby::getHobbyName).collect(Collectors.toList()))
                .build();
    }
}
