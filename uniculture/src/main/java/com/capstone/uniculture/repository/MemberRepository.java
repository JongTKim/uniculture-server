package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {

    // Email 주소로 Member 찾는 로직
    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickname(String nickname);

    // Email 이 이미 존재하는지 판별 로직
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
