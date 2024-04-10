package com.capstone.uniculture.dto.Post.Request;

import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.entity.Post.PostStatus;
import com.capstone.uniculture.entity.Post.PostType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.print.Pageable;

@Getter
@Setter
public class PostListRequestDto {

    @JsonProperty(defaultValue = "NORMAL")
    private PostCategory ca;

    @JsonProperty(required = false)
    private PostType pt;

    @JsonProperty
    private PostStatus ps;
}