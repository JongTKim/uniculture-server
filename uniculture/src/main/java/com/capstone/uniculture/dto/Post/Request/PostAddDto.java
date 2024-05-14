package com.capstone.uniculture.dto.Post.Request;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.entity.Post.PostStatus;
import com.capstone.uniculture.entity.Post.PostType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostAddDto {
    private String title;
    private String contents;
    private PostType posttype;
    private PostCategory postCategory;
    private List<String> tag;
    private String imgUrl;

    public Post toPost(){
        return Post.builder()
                .title(title)
                .content(contents)
                .posttype(posttype)
                .postCategory(postCategory)
                .postStatus(PostStatus.START)
                .build();
    }
}
