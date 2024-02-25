package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Post.PostAddDto;
import com.capstone.uniculture.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/auth/board")
    public ResponseEntity addBoard(@RequestBody PostAddDto postAddDto){
        return ResponseEntity.ok(postService.createPost(postAddDto));
    }

    @PatchMapping("/auth/board")
    public ResponseEntity updateBoard(){
        return ResponseEntity.ok("성공");
    }
}
