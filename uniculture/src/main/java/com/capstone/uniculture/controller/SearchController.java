package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Post.Response.PostSearchDto;
import com.capstone.uniculture.dto.SearchCountDto;
import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "총 검색 개수")
    @GetMapping("/count")
    public ResponseEntity<SearchCountDto> countSearch(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tag){
        return ResponseEntity.ok(searchService.countSearch(content, tag));
    }


}
