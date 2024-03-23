package com.capstone.uniculture.controller;

import com.capstone.uniculture.dto.Comment.CommentDto;
import com.capstone.uniculture.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name="댓글", description = "댓글(Comment) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/auth/comment")
    public ResponseEntity createComment(@RequestParam("postId") Long postId,
                                       @RequestBody CommentDto commentDto){
        return ResponseEntity.ok(commentService.createComment(postId, commentDto));
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/auth/comment")
    public ResponseEntity updateComment(@RequestParam("commentId") Long commentId,
                                        @RequestParam CommentDto commentDto){
        return ResponseEntity.ok(commentService.updateComment(commentId,commentDto));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/auth/comment")
    public ResponseEntity deleteComment(@RequestParam("commentId") Long commentId){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}