package com.capstone.uniculture.dto.Post;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {
    private String title;
    private String contents;
    private PostType posttype;
}
