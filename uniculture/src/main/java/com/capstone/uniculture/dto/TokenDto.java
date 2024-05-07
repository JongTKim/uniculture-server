package com.capstone.uniculture.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String username;
    private String grantType;
    private String accessToken;
    private Long tokenExpiresIn;
}
