package com.capstone.uniculture.dto.Post.Request;

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

    @Schema(title = "제목", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(title = "내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String contents;

    @Schema(title = "게시글 타입", requiredMode = Schema.RequiredMode.REQUIRED)
    private PostType posttype;
}
