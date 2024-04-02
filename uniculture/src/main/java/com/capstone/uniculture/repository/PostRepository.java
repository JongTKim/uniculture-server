package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {

    @Query("SELECT COUNT(p) FROM Post p WHERE p.member = :member")
    Integer countByMember(@Param("member") Member member);

    /**
     * 삭제할때 본인이 작성한 글이 맞는지 확인하기위해 사용
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.id = :postId AND p.member.id = :memberId")
    Optional<Post> findPostByIdAndMemberId(@Param("postId") Long postId, @Param("memberId") Long memberId);
    /**
     * postId에 따라 게시물 하나만 조회하는 메소드 - 상세 게시물 페이지에 들어갈 내용
     * N+1 문제를 해결하고자 FETCH JOIN 사용, 추후 @FetchJoin 으로 변경할수도 있음
     */
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.member LEFT JOIN FETCH p.comments WHERE p.id = :postId")
    Optional<Post> findPostWithMemberAndCommentsById(@Param("postId") Long postId);

    /**
     * 전체 게시물을 조회하는 메소드 - 메인창에 들어갈 내용
     * 여기서는 Comment 까지 Join 필요X. CommentCount 만 쓸꺼기때문
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.member")
    Page<Post> findAllWithMemberAndComments(Pageable pageable);

    /**
     * Post 타입에 따른 게시물만 조회하는 메소드 - 메인창에 들어갈 내용
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.member WHERE p.posttype = :postType")
    Page<Post> findByPostTypeWithMember(@Param("postType") PostType postType, Pageable pageable);

    /**
     *  유저 ID에 따른 게시물만 조회하는 메소드 - 프로필창에 들어갈 내용
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.member WHERE p.member.id = :memberId")
    Page<Post> findByMemberIdWithMember(@Param("memberId") Long memberId, Pageable pageable);


}
