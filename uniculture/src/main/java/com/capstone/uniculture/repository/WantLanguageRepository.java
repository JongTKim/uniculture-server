package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.WantLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface WantLanguageRepository extends JpaRepository<WantLanguage,Long> {

    @Modifying
    @Query("DELETE FROM WantLanguage w WHERE w.member.id = :memberId")
    void deleteAllByMemberId(Long memberId);
}
