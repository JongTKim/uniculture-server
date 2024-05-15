package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurposeRepository extends JpaRepository<Purpose, Long> {

    void deleteAllByMemberId(Long memberId);
}
