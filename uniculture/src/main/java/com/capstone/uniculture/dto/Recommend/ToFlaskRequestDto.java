package com.capstone.uniculture.dto.Recommend;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ToFlaskRequestDto {
    private Long id;
    private List<ProfileRecommendRequestDto> profiles;
}
