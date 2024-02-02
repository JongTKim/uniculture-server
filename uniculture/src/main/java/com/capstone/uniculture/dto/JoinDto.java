package com.capstone.uniculture.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JoinDto {
    private String username;
    private String password;
    private String role;
}
