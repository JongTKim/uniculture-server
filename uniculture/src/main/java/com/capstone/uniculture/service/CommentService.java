package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Comment.CommentDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Post.Comment;
import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.repository.CommentRepository;
import com.capstone.uniculture.repository.MemberRepository;
import com.capstone.uniculture.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public String createComment(Long postId, CommentDto commentDto) {
        // 1. 게시물 검색
        Post post = findPost(postId);

        // 2. 작성자 검색
        Member member = findMember(SecurityUtil.getCurrentMemberId());

        // 3. Dto -> Entity, 연관관계 매핑
        Comment comment = CommentDto.toComment(commentDto);
        comment.setPost(post);
        comment.setMember(member);

        commentRepository.save(comment);
        return "댓글 작성에 성공하였습니다";

    }

    public String updateComment(Long commentId, CommentDto commentDto){
        Comment comment = findComment(commentId);
        comment.setContent(commentDto.getContent());
        return "댓글 수정에 성공하였습니다";
    }

    public String deleteComment(Long commentId){
        commentRepository.deleteById(commentId);
        return "댓글 삭제에 성공하였습니다";
    }

    private Member findMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(
                () -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다"));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다"));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다"));
    }
}
