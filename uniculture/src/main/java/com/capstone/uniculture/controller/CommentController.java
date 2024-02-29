package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Comment.CommentDto;
import com.capstone.uniculture.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/auth/comment")
    public ResponseEntity createComment(@RequestParam("postId") Long postId,
                                       @RequestBody CommentDto commentDto){
        return ResponseEntity.ok(commentService.createComment(postId, commentDto));
    }

    @PatchMapping("/auth/comment")
    public ResponseEntity updateComment(@RequestParam("commentId") Long commentId,
                                        @RequestParam CommentDto commentDto){
        return ResponseEntity.ok(commentService.updateComment(commentId,commentDto));
    }

    @DeleteMapping("/auth/comment")
    public ResponseEntity deleteComment(@RequestParam("commentId") Long commentId){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}