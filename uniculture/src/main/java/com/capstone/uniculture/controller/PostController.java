package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Post.Request.PostAddDto;
import com.capstone.uniculture.dto.Post.Request.PostUpdateDto;
import com.capstone.uniculture.dto.Post.Response.PostDetailDto;
import com.capstone.uniculture.dto.Post.Response.PostListDto;
import com.capstone.uniculture.dto.Post.Response.PostSearchDto;
import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.entity.Post.PostType;
import com.capstone.uniculture.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="게시물", description = "게시물(Post) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(summary = "내 게시글 조회")
    @GetMapping("/auth/post")
    public ResponseEntity<Page<PostListDto>> MyPostList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam PostCategory category) {
        Long memberId = SecurityUtil.getCurrentMemberId();
        return ResponseEntity.ok(postService.getPostsByMember(category, memberId, pageable));
    }

    @Operation(summary = "게시글 작성")
    @PostMapping("/auth/post")
    public ResponseEntity addBoard(@RequestBody PostAddDto postAddDto){
        return ResponseEntity.ok(postService.createPost(postAddDto));
    }

    @Operation(summary = "게시글 수정")
    @PatchMapping("/auth/post/{postId}")
    public ResponseEntity updateBoard(
            @Parameter(name = "id", description = "게시글의 ID", in = ParameterIn.PATH)
            @PathVariable("postId") Long postId,
            @RequestBody PostUpdateDto postUpdateDto){
        return ResponseEntity.ok(postService.updatePost(postId,postUpdateDto));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/auth/post/{postId}")
    public ResponseEntity deleteBoard(@PathVariable("postId")Long postId){
        return ResponseEntity.ok(postService.deletePost(postId));
    }

    @Operation(summary = "게시글 1개 상세조회")
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDetailDto> getPost(@PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @Operation(summary = "게시글 검색")
    @GetMapping("/post/search")
    public ResponseEntity<Page<PostSearchDto>> postSearch(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam PostCategory category,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) List<String> tag){
        return ResponseEntity.ok(postService.getAllPostsBySearch(category, content, tag, pageable));
    }
    @Operation(summary = "게시글 전체 조회(최신순)")
    @GetMapping("/post")
    public ResponseEntity<Page<PostListDto>> postList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam PostCategory postCategory){
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @Operation(summary = "일상 게시글 전체 조회")
    @GetMapping("/post/daily")
    public ResponseEntity<Page<PostListDto>> dailyPostList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(postService.getPostsByType(PostType.DAILY, pageable));
    }

    @Operation(summary = "도움 게시글 전체 조회")
    @GetMapping("/post/help")
    public ResponseEntity<Page<PostListDto>> helpPostList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(postService.getPostsByType(PostType.HELP, pageable));
    }

    @Operation(summary = "내 친구의 게시물 전체 조회")
    @GetMapping("/auth/post/friend")
    public ResponseEntity<Page<PostListDto>> myFriendPostList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(postService.getMyFriendPosts(pageable));
    }

    @Operation(summary = "멤버별 게시글 리스트")
    @GetMapping("/post/member/{memberId}")
    public ResponseEntity<Page<PostListDto>> MemberPostList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable,
            @PathVariable("memberId")Long memberId,
            @RequestParam PostCategory postCategory){
        return ResponseEntity.ok(postService.getPostsByMember(postCategory, memberId,pageable));
    }

    @Operation(summary = "게시글 좋아요")
    @PostMapping("/auth/post/{postId}/like")
    public ResponseEntity likePost(@PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.likePost(postId));
    }

    @Operation(summary = "게시글 좋아요 취소")
    @DeleteMapping("/auth/post/{postId}/like")
    public ResponseEntity unlikePost(@PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.unlikePost(postId));
    }
}
