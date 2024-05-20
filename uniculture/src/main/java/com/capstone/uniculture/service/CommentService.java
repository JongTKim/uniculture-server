package com.capstone.uniculture.service;

import com.capstone.uniculture.config.SecurityUtil;
import com.capstone.uniculture.dto.Comment.CommentDto;
import com.capstone.uniculture.dto.Comment.CommentResponseDto;
import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Notification.Notification;
import com.capstone.uniculture.entity.Notification.NotificationType;
import com.capstone.uniculture.entity.Post.Comment;
import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.repository.CommentRepository;
import com.capstone.uniculture.repository.MemberRepository;
import com.capstone.uniculture.repository.NotificationRepository;
import com.capstone.uniculture.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.*;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public String createComment(Long postId, CommentDto commentDto) {
        // 1. 게시물 검색(프록시)
        Post post = postRepository.findById(postId).get();
        post.addComment();

        // 2. 작성자 검색(프록시)
        Member member = memberRepository.findById(SecurityUtil.getCurrentMemberId()).get();

        // 3. Dto -> Entity, 연관관계 매핑
        Comment comment = CommentDto.toComment(commentDto);

        // 4. 대댓글 이라면 부모를 설정해줘야함. 그 후 그외 정보들을 완성시킴
        if (commentDto.getParentId() != null) {
            // ** 4-1. 부모는 부모를 가지고있으면 안된다 (대댓글은 한 층까지만 허용, 대대댓글은 불가)
            // -> 보류, 사유 : 부모를 가지고 오려면 쿼리가 한번더 날아가야하는데 클라이언트에서 임의로 조작하지 않을경우 이 경우가 될수없음
            Comment parentComment = commentRepository.findById(commentDto.getParentId()).get();
            comment.setting(parentComment, post, member);
            if(!parentComment.getMember().getId().equals(member.getId())) {
                Notification noti1 = Notification.builder()
                        .notificationType(NotificationType.COMMENT)
                        .member(parentComment.getMember())
                        .isCheck(Boolean.FALSE)
                        .content(member.getNickname() + "님이 나의 댓글에 답글을 달았습니다")
                        .relatedNum(post.getId())
                        .build();
                notificationRepository.save(noti1);
            }
        }
        else {
            comment.setting(null, post, member);
        }

        // 내 게시물이면 달리면 안된다.
        if(!post.getMember().getId().equals(member.getId())){
            Notification noti2 = Notification.builder()
                    .notificationType(NotificationType.COMMENT)
                    .member(post.getMember())
                    .isCheck(Boolean.FALSE)
                    .content(member.getNickname() + "님이 나의 게시글에 댓글을 달았습니다")
                    .relatedNum(post.getId())
                    .build();
            notificationRepository.save(noti2);
        }


        // 6. 댓글 저장
        commentRepository.save(comment);

        return "댓글 작성에 성공하였습니다";
    }

    @Transactional
    public String updateComment(Long commentId, CommentDto commentDto) {
        // 1. 댓글 가져오기
        Comment comment = findComment(commentId);

        // 2. 자기 댓글인지 확인, 삭제된 댓글인지 확인(대댓글이 달린경우 댓글이 삭제되도 남아있기 때문)
        Long memberId = SecurityUtil.getCurrentMemberId();
        if(comment.getMember().getId() != memberId){
            throw new RuntimeException("자신이 작성한 댓글이 아닙니다");
        }
        if(comment.getIsDeleted() == Boolean.TRUE){
            throw new RuntimeException("삭제된 댓글은 수정할 수 없습니다");
        }

        // 3. 변경 감지에 의해 자동으로 Update
        comment.changeContent(commentDto.getContent());

        return "댓글 수정에 성공하였습니다";
    }

    @Transactional
    public String deleteComment(Long commentId) {
        // 1. 댓글 가져오기
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new NotFoundException("댓글이 존재하지 않습니다"));

        // 2. 자기 댓글인지 확인
        Long memberId = SecurityUtil.getCurrentMemberId();

        if(comment.getMember().getId() != memberId){
            throw new RuntimeException("자신이 작성한 댓글이 아닙니다");
        }

        // 2. 만약 자식(대댓글)이 있으면 바로 삭제하지 않고, (삭제된 게시물입니다) 처리만 해줌
        if(comment.getChildren().size() != 0){
            comment.changeStatus(true);
        }

        // 3. 자식이 없다면 바로 삭제. 근데 만약 이게 대댓글이고, 부모가 삭제된 상태고, 이거 지우면 더이상 대댓글이 없으면 부모까지 삭제해줘야함
        else{
            Comment parent = comment.getParent();
            if(parent != null && parent.getChildren().size() == 1 && parent.getIsDeleted()){
                commentRepository.deleteById(parent.getId());
            }
            else { // 셋중에 한조건이라도 만족하지 못하면 대댓글만 삭제
                commentRepository.deleteById(commentId);
            }
        }
        return "댓글 삭제에 성공하였습니다";
    }


    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("해당 댓글이 존재하지 않습니다"));
    }

    public Page<CommentResponseDto> viewCommentLogin(Long postId, Pageable pageable) {
        // 0. 사용자의 아이디 받아오기 (댓글이 자신의 댓글인지 판단을 위함)
        Long memberId = SecurityUtil.getCurrentMemberId();

        // 1. 게시물 존재하는지 확인
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다"));

        // 2. 댓글중에서만 Paging 실행
        List<Comment> comments = commentRepository.findCommentsByOnlyParent(postId, pageable);

        List<CommentResponseDto> commentResponseDtos = comments.stream().map(comment -> {
            CommentResponseDto dto = CommentResponseDto.fromEntity(comment);
            if (post.getMember().getId().equals(comment.getMember().getId())) dto.setPostMine(true);
            if(comment.getMember().getId().equals(memberId)) dto.setIsMine(true);
            List<CommentResponseDto> childDtos = comment.getChildren().stream().map(comment1 -> {
                CommentResponseDto childDto = CommentResponseDto.fromEntity(comment1);
                if (post.getMember().getId().equals(comment1.getMember().getId())) childDto.setPostMine(true);
                if (comment1.getMember().getId() == memberId) childDto.setIsMine(true);
                return childDto;
            }).toList();
            dto.setChildren(childDtos);
            return dto;
        }).toList();

        return new PageImpl<>(commentResponseDtos, pageable, comments.size());
    }

    public Page<CommentResponseDto> viewCommentLogout(Long postId, Pageable pageable) {

        Post post = postRepository.findPostByIdFetch(postId).orElseThrow(
                () -> new IllegalArgumentException("해당 게시물이 존재하지 않습니다"));

        List<Comment> comments = commentRepository.findCommentsByOnlyParent(postId, pageable);

        List<CommentResponseDto> commentResponseDtos = comments.stream().map(comment -> {
            CommentResponseDto dto = CommentResponseDto.fromEntity(comment);
            if (post.getMember().getId().equals(comment.getMember().getId())) dto.setPostMine(true);
            List<CommentResponseDto> childDtos = comment.getChildren().stream().map(comment1 -> {
                CommentResponseDto childDto = CommentResponseDto.fromEntity(comment1);
                if (post.getMember().getId().equals(comment1.getMember().getId())) childDto.setPostMine(true);
                return childDto;
            }).toList();
            dto.setChildren(childDtos);
            return dto;
        }).toList();

        return new PageImpl<>(commentResponseDtos, pageable, comments.size());
    }

    public List<Long> countComment(Long postId) {
        List<Long> lists = new ArrayList<>();
        lists.add(commentRepository.countCommentByPost_IdOnlyParent(postId));
        lists.add(commentRepository.countCommentByPost_Id(postId));
        return lists;
    }
}
