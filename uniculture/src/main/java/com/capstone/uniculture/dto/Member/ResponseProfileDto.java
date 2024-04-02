package com.capstone.uniculture.dto.Member;

import com.capstone.uniculture.entity.Member.Gender;
import com.capstone.uniculture.entity.Member.Member;
import lombok.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseProfileDto {
    //아이디 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private Long id;
    // 프로필 사진 (ME,LOGIN-OTHER,LOGOUT-OTHER)
    private String profileurl;
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

    public ResponseProfileDto(Member member){
        this.id = member.getId();
        this.nickname = member.getNickname();
        this.age = member.getAge();
        /*
        if(member.getProfileUrl() != null){
            File imageFile = new File(member.getProfileUrl());
            this.profileUrl = org.apache.commons.codec.binary.Base64.encodeBase64String(Files.readAllBytes(imageFile.toPath()));
        }*/
        this.receiverequestnum = member.getReceivedRequests().size();
        this.friendnum = member.getFriends().size();
        this.postnum = member.getPost().size();
    }
}
