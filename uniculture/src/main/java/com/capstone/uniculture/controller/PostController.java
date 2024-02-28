package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Post.PostAddDto;
import com.capstone.uniculture.dto.Post.PostOneDto;
import com.capstone.uniculture.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/auth/post")
    public ResponseEntity addBoard(@RequestBody PostAddDto postAddDto){
        return ResponseEntity.ok(postService.createPost(postAddDto));
    }

    @PatchMapping("/auth/post")
    public ResponseEntity updateBoard(){
        return ResponseEntity.ok("성공");
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostOneDto> getPost(@PathVariable("postId") Long postId){
        try{
            Long memberId = SecurityUtil.getCurrentMemberId();
            // 여기서부터는 로그인 된 사용자
            postService.getPost(postId);

        }catch (RuntimeException e){
            System.out.println("error message = " + e.getMessage());
            // 여기서부터는 로그인 안된 사용자

        }
    }


}
