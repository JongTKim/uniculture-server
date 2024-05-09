package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.MyLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MyLanguageRepository extends JpaRepository<MyLanguage,Long> {

    @Modifying
    @Query("DELETE FROM MyLanguage m WHERE m.member.id = :memberId")
    void deleteAllByMemberId(Long memberId);
}
