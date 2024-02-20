package com.capstone.uniculture.dto;

import com.capstone.uniculture.entity.Member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Data
@NoArgsConstructor
public class MyPageDto {
    private Long id;
    private String email;
    private String profileurl;
    private String nickname;
    private Integer age;
    private String gender;
    //private List<String> post;
    private Integer postnum;
    private Integer friendnum;
    private Integer receiverequestNum;

    public MyPageDto(Member member) throws IOException {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
        this.age = member.getAge();
        if(member.getGender() != null){
            this.gender = member.getGender().toString();
        }
        /*
        if(member.getProfileUrl() != null){
            File imageFile = new File(member.getProfileUrl());
            this.profileUrl = org.apache.commons.codec.binary.Base64.encodeBase64String(Files.readAllBytes(imageFile.toPath()));
        }*/
        this.receiverequestNum = member.getReceivedRequests().size();
        this.friendnum = member.getFriends().size();
        this.postnum = member.getPost().size();
    }
}
