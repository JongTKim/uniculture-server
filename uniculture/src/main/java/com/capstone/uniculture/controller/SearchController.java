package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Member.Response.SimpleMemberProfileDto;
import com.capstone.uniculture.dto.Post.Response.PostDetailDto;
import com.capstone.uniculture.dto.Post.Response.PostSearchDto;
import com.capstone.uniculture.dto.SearchCountDto;
import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.service.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "통합검색", description = "통합검색(Search) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @Operation(summary = "총 검색 개수(게시물+친구+전체멤버)")
    @GetMapping("/search/count")
    public ResponseEntity<SearchCountDto> countSearch(
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tag){
        return ResponseEntity.ok(searchService.countSearch(content, tag));
    }

    @Operation(summary = "전체 멤버중 검색")
    @GetMapping("/search/member")
    public ResponseEntity<Page<SimpleMemberProfileDto>> searchMember(
            @PageableDefault(size=10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam String nickname){
        return ResponseEntity.ok(searchService.searchMember(nickname, pageable));
    }

    @Operation(summary = "내 친구중 검색")
    @GetMapping("/auth/search/friend")
    public ResponseEntity<Page<SimpleMemberProfileDto>> searchFriend(
            @PageableDefault(size=10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam String nickname){
        return ResponseEntity.ok(searchService.searchFriend(nickname, pageable));
    }

    @Operation(summary = "전체 글중 검색")
    @GetMapping("/search/post")
    public ResponseEntity<Page<PostDetailDto>> searchPost(
            @PageableDefault(size=10, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tag) {
        return ResponseEntity.ok(searchService.searchPost(content, tag, pageable));
    }

}
