package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Member.WantLanguage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WantLanguageRepository extends JpaRepository<WantLanguage,Long> {
}
