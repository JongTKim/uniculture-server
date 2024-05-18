package com.capstone.uniculture.controller;

import com.capstone.uniculture.config.S3UploadUtil;
import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Post.Request.PostAddDto;
import com.capstone.uniculture.dto.Post.Request.PostListRequestDto;
import com.capstone.uniculture.dto.Post.Request.PostStatusDto;
import com.capstone.uniculture.dto.Post.Request.PostUpdateDto;
import com.capstone.uniculture.dto.Post.Response.PostDetailDto;
import com.capstone.uniculture.dto.Post.Response.PostListDto;
import com.capstone.uniculture.dto.Post.Response.PostSearchDto;
import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Tag(name="게시물", description = "게시물(Post) 관련 API 입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final S3UploadUtil s3UploadUtil;

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

    @Operation(summary = "주간 인기 태그 5개")
    @GetMapping("/post/tag")
    public ResponseEntity<List<String>> hotTag(){
        return ResponseEntity.ok(postService.hotTag());
    }

    @Operation(summary = "주간 인기 게시물 5개")
    @GetMapping("/post/hot")
    public ResponseEntity<List<PostListDto>> hotPost(){
        return ResponseEntity.ok(postService.hotPost());
    }

    // Post 조회하는 컨트롤러. (+Paging) postType, postCategory, postStatus 에 따른 조회가능
    // RequestParam 을 DTO 로 받도록 Refactoring
    @Operation(summary = "게시글 전체 조회(눌러서 설명확인)",
            description = "postType(일반이면 DAILY, HELP 스터디면 HOBBY,LANGUAGE)," +
                    "postCategory(NORMAL,STUDY), postStatus(모집중이면 START, 모집완료면 FINISH)에 따른 조회가 가능합니다" +
                    "sort(commentCount, likeCount, viewCount) 로 정렬조건도 줄 수 있습니다")
    @GetMapping("/post")
    public ResponseEntity<Page<PostListDto>> postList(
            @ModelAttribute PostListRequestDto postListRequestDto,
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable){
        return ResponseEntity.ok(postService.getAllPosts(pageable, postListRequestDto));
    }

    @Operation(summary = "멤버별 게시글 리스트")
    @GetMapping("/post/member/{memberId}")
    public ResponseEntity<Page<PostListDto>> MemberPostList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable,
            @PathVariable("memberId")Long memberId,
            @RequestParam PostCategory category){
        return ResponseEntity.ok(postService.getPostsByMember(category ,memberId, pageable));
    }

    // ------------------------------↓ 로그인 필수 ↓------------------------------------- //

    /**
     * 내 게시물 조회 API
     * (게시물, 스터디) 선택에 따라 내용이 달라져야 하므로, Parameter 로 category 를 받는다
     * Service 단에서 타인 게시물 조회와 같은 메소드를 사용하므로 memberId를 미리 받아서 넘겨야한다
     */
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
            @PathVariable("postId") Long postId,
            @RequestBody PostUpdateDto postUpdateDto){
        return ResponseEntity.ok(postService.updatePost(postId,postUpdateDto));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/auth/post/{postId}")
    public ResponseEntity deleteBoard(@PathVariable("postId")Long postId){
        return ResponseEntity.ok(postService.deletePost(postId));
    }



    @Operation(summary = "내 친구의 게시물 전체 조회")
    @GetMapping("/auth/post/friend")
    public ResponseEntity<Page<PostListDto>> myFriendPostList(
            @PageableDefault(size=10, sort = "id", direction = Sort.Direction.DESC)Pageable pageable){
        return ResponseEntity.ok(postService.getMyFriendPosts(pageable));
    }

    @Operation(summary = "게시글 상태변경")
    @PostMapping("/auth/post/{postId}/status")
    public ResponseEntity changeStatus(@PathVariable("postId") Long postId,
                                       @RequestBody PostStatusDto postStatusDto){
        return ResponseEntity.ok(postService.changeStatus(postId, postStatusDto));
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

    @Operation(summary = "게시글 이미지 작성")
    @PostMapping(path = {"/file"}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public String fileUpload(@RequestPart("files") MultipartFile multipartFile) throws IOException {
        return s3UploadUtil.upload2(multipartFile, "test");// test 폴더에 파일 생성
    }

    /*
    @DeleteMapping(name = "S3 파일 삭제", value = "/file")
    public String fileDelete(@RequestParam("path") String path) {
        s3UploadUtil.delete(path);
        return "success";
    }
     */
}
