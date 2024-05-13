package com.capstone.uniculture.dto.Post.Request;

import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostStatus;
import com.capstone.uniculture.entity.Post.PostType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Schema(title = "게시글 모집완료 여부(스터디에서만 사용)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private PostStatus postStatus;

    private Long imageNum;

    private List<String> tag;
}
