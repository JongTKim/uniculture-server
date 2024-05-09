package com.capstone.uniculture.dto.Post.Request;

import com.capstone.uniculture.entity.Post.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostStatusDto {
    private PostStatus status;
}
