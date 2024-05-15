package com.capstone.uniculture.dto.Recommend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRecommendResponseDto {
    private int status;
    private String message;
    private dataObject data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class dataObject {
        private Map<Long, Long> sortedList;
    }
}