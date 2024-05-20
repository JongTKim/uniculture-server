package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Comment.CommentDto;
import com.capstone.uniculture.dto.Comment.CommentResponseDto;
import com.capstone.uniculture.service.CommentService;
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

@Tag(name="댓글", description = "댓글(Comment) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 게시물에 달린 전체 댓글 조회 API
     * [로직 : 로그인 판단 -> 부모 댓글만 Page size 에 따라 가져오기 -> 가져온 댓글을 로그인 상태에 따른 DTO 로 변경]
     * 로그인 한 사용자와 로그인 안한 사용자의 댓글 조회가 다르게 되야하는 이유는 DTO 에 isMine 이 달려서 날아기 때문이다 (자신이 쓴 댓글여부)
     * 주의 : Page 의 Size 는 부모 댓글의 개수이다. 즉, 5개의 댓글을 요청했어도, 그 중 1개의 댓글에 대댓글이 100개가 달렸을경우
     */
    @Operation(summary = "게시물의 댓글 조회")
    @GetMapping("/comment")
    public ResponseEntity<Page<CommentResponseDto>> viewComment(
            @PageableDefault(size=10, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam("postId") Long postId )
    {
        try { // 로그인 한 사용자일때
            Long memberId = SecurityUtil.getCurrentMemberId();
            System.out.println("로그인 함");
            return ResponseEntity.ok(commentService.viewCommentLogin(postId, pageable));
        }
        catch (RuntimeException e){ // 로그인 안한 사용자일때
            System.out.println("로그인 안함");
            return ResponseEntity.ok(commentService.viewCommentLogout(postId, pageable));
        }
    }

    @Operation(summary = "게시물의 댓글수 조회")
    @GetMapping("/comment/count")
    public ResponseEntity<List<Long>> countComment(@RequestParam("postId") Long postId){
        return ResponseEntity.ok(commentService.countComment(postId));
    }

    // ------------------------------↓ 로그인 필수 ↓------------------------------------- //

    @Operation(summary = "댓글 작성")
    @PostMapping("/auth/comment")
    public ResponseEntity createComment(@RequestParam("postId") Long postId,
                                        @RequestBody CommentDto commentDto){
        return ResponseEntity.ok(commentService.createComment(postId, commentDto));
    }

    @Operation(summary = "댓글 수정")
    @PatchMapping("/auth/comment")
    public ResponseEntity updateComment(@RequestParam("commentId") Long commentId,
                                        @RequestBody CommentDto commentDto){
        return ResponseEntity.ok(commentService.updateComment(commentId,commentDto));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/auth/comment")
    public ResponseEntity deleteComment(@RequestParam("commentId") Long commentId){
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}