package com.capstone.uniculture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchCountDto {

    private Long total;
    private Long post;
    private Long friend;
    private Long member;
}
