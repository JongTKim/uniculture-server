package com.capstone.uniculture.dto.Post;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateDto {

    @Schema(description = "제목")
    private String title;

    @Schema(description = "내용")
    private String contents;

    @Schema(description = "게시글 타입")
    private PostType posttype;
}
