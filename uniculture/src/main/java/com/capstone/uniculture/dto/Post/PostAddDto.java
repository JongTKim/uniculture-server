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
public class PostAddDto {
    private String title;
    private String contents;
    private PostType posttype;

    public Post toPost(){
        return Post.builder()
                .title(title)
                .content(contents)
                .posttype(posttype)
                .build();
    }
}
