package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import com.capstone.uniculture.entity.Post.Post;
import com.capstone.uniculture.entity.Post.PostCategory;
import com.capstone.uniculture.entity.Post.PostStatus;
import com.capstone.uniculture.entity.Post.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long>, JpaSpecificationExecutor<Post> {

    /*
    @Query(value = "SELECT p.* FROM post p JOIN post_like pl ON p.id = pl.post_id " +
            "WHERE pl.created_date >= DATEADD(DAY, -7, CURRENT_DATE) " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(*) DESC LIMIT 5", nativeQuery = true)
    List<Post> findMostLikedPostLastWeek();*/

    @Query("SELECT p FROM Post p JOIN p.postLikes pl " +
            "WHERE pl.createdDate >= :oneWeekAgo " +
            "GROUP BY p.id " +
            "ORDER BY COUNT(pl) DESC")
    Page<Post> findMostLikedPostLastWeek(@Param("oneWeekAgo") LocalDateTime oneWeekAgo, Pageable pageable);


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
    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.member WHERE p.id = :postId")
    Optional<Post> findPostWithMemberById(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p JOIN FETCH p.member")
    List<Post> findAllWithMember();

    /**
     * 전체 게시물을 조회하는 메소드 - 메인창에 들어갈 내용
     * 여기서는 Comment 까지 Join 필요X. CommentCount 만 쓸꺼기때문
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.member " +
            "WHERE (:pt is null or p.posttype = :pt) " +
            "and (:ct is null or p.postCategory = :ct)" +
            "and (:ps is null or p.postStatus = :ps)")
    Page<Post> findAllWithMemberAndComments(Pageable pageable,
                                            @Param("pt") PostType postType,
                                            @Param("ct") PostCategory postCategory,
                                            @Param("ps") PostStatus postStatus);

    /**
     * Post 타입에 따른 게시물만 조회하는 메소드 - 메인창에 들어갈 내용
     * Member 와는 관계가 1:1이기때문에 Fetch Join 해서 Paging 하더라도 문제가 발생하지 않음
     */
    @Query("SELECT DISTINCT p FROM Post p JOIN FETCH p.member WHERE p.posttype = :postType")
    Page<Post> findByPostTypeWithMember(@Param("postType") PostType postType, Pageable pageable);

    /**
     *  유저 ID와 게시물 Category 따른 게시물만 조회하는 메소드 - 프로필창에 들어갈 내용
     *  Member 와는 관계가 1:1이기때문에 Fetch Join 해서 Paging 하더라도 문제가 발생하지 않음
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.postCategory = :postCategory and p.member.id = :memberId")
    Page<Post> findByMemberIdWithMember(@Param("postCategory") PostCategory postCategory, @Param("memberId") Long memberId, Pageable pageable);

    /**
     * 내 친구인 Member 의 게시물만 조회하는 메소드
     * Member 와는 관계가 1:1이기때문에 Fetch Join 해서 Paging 하더라도 문제가 발생하지 않음
     */
    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.member IN (SELECT f.toMember FROM Friendship f WHERE f.fromMember.id = :myId)")
    Page<Post> findPostsFromMyFriends(@Param("myId") Long myId, Pageable pageable);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.member WHERE p.title LIKE %:title%")
    Page<Post> findAllByTitleContaining(@Param("title") String title, Pageable pageable);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.member WHERE p.content LIKE %:content%")
    Page<Post> findAllByContentContaining(@Param("content") String content, Pageable pageable);

    @Query(value = "SELECT p FROM Post p JOIN FETCH p.member WHERE p.member.nickname LIKE %:nickname%")
    Page<Post> findAllByNicknameContaining(@Param("nickname") String nickname, Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.member WHERE p.id = :postId")
    Optional<Post> findPostByIdFetch(@Param("postId") Long postId);

    // 문제점 : Fetch Join + Paging, LEFT Outer Join + Fetch Join
    // 문제점 : 동적 쿼리로 바꿔줘야됨.
    // title만 들어왔는데 postTag fetch join은 왜합니까?
    // 또한 posttag는 1:N관계인데 @BatchSize로 처리하는게 이득이쥬?
    // 1:N에서 Paging 처리하면 오류안나나요???
    @Query("SELECT p FROM Post p LEFT OUTER JOIN FETCH p.postTags pt JOIN FETCH p.member m " +
            "WHERE p.postCategory = :category " +
            "and (:title is null or p.title like %:title%)" +
            "and (:tag is null or pt.hashtag in :tag)")
    Page<Post> findByTitleAndContentAndAuthorName(
            @Param("category") PostCategory category,
            @Param("title") String title,
            @Param("tag") List<String> tag,
            Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.postStatus= :postStatus WHERE p.id = :postId")
    void changeStatus(@Param("postId") Long postId, @Param("postStatus") PostStatus postStatus);

    /*
    @Query(value = "SELECT COUNT(*) FROM post p " +
            "WHERE p.content LIKE %:contentKeyword% " +
            "AND p.id IN (SELECT post_id FROM post_tag pt WHERE pt.hashtag IN :hashtags)", nativeQuery = true)
    Long countPostsByContentAndHashtags(@Param("contentKeyword") String contentKeyword, @Param("hashtags") List<String> hashtags);

     @Query(value = "SELECT COUNT(*) FROM post p " +
            "WHERE p.content LIKE %:contentKeyword% ", nativeQuery = true)
    Long countPostsByContent(@Param("contentKeyword") String contentKeyword);

     */

    @Query("SELECT COUNT(p) FROM Post p " +
            "JOIN p.postTags pt " +
            "WHERE p.postCategory = 'NORMAL' " +
            "AND (:keyword is null or p.title LIKE %:keyword%) " +
            "AND pt.hashtag IN :hashtags ")
    Long countPosts(@Param("keyword") String keyword, @Param("hashtags") List<String> hashtags);

    @Query("SELECT COUNT(p) FROM Post p " +
            "WHERE p.postCategory = 'NORMAL' AND p.title LIKE %:keyword% ")
    Long countPostsByContent(@Param("keyword") String keyword);




}
