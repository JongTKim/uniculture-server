package com.capstone.uniculture.dto.Recommend;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ToFlaskRequestDto {
    private Long id;
    private List<ProfileRecommendRequestDto> profiles;
}
