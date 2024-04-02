package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Post.PostAddDto;
import com.capstone.uniculture.dto.Post.PostDetailDto;
import com.capstone.uniculture.dto.Post.PostListDto;
import com.capstone.uniculture.dto.Post.PostUpdateDto;
import com.capstone.uniculture.entity.Post.PostType;
import com.capstone.uniculture.service.PostService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/auth/post")
    public ResponseEntity<Page<PostListDto>> MyPostList(@PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(postService.getPostsByMember(SecurityUtil.getCurrentMemberId(), pageable));
    }

    @PostMapping("/auth/post")
    public ResponseEntity addBoard(@RequestBody PostAddDto postAddDto){
        return ResponseEntity.ok(postService.createPost(postAddDto));
    }

    @PatchMapping("/auth/post/{postId}")
    public ResponseEntity updateBoard(@PathVariable("postId") Long postId, @RequestBody PostUpdateDto postUpdateDto){
        return ResponseEntity.ok(postService.updatePost(postId,postUpdateDto));
    }

    @DeleteMapping("/auth/post/{postId}")
    public ResponseEntity deleteBoard(@PathVariable("postId")Long postId){
        return ResponseEntity.ok(postService.deletePost(postId));
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<PostDetailDto> getPost(@PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/post")
    public ResponseEntity<Page<PostListDto>> postList(@PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @GetMapping("/post/daily")
    public ResponseEntity<Page<PostListDto>> dailyPostList(@PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(postService.getPostsByType(PostType.DAILY, pageable));
    }

    @GetMapping("/post/help")
    public ResponseEntity<Page<PostListDto>> helpPostList(@PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(postService.getPostsByType(PostType.HELP, pageable));
    }

    @GetMapping("/post/member/{memberId}")
    public ResponseEntity<Page<PostListDto>> MemberPostList(@PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable,@PathVariable("memberId")Long memberId){
        return ResponseEntity.ok(postService.getPostsByMember(memberId,pageable));
    }

    @PostMapping("/auth/post/{postId}/like")
    public ResponseEntity likePost(@PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.likePost(postId));
    }

    @DeleteMapping("/auth/post/{postId}/like")
    public ResponseEntity unlikePost(@PathVariable("postId") Long postId){
        return ResponseEntity.ok(postService.unlikePost(postId));
    }
}
