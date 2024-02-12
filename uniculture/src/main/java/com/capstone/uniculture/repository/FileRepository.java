package com.capstone.uniculture.repository;

import com.capstone.uniculture.entity.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Files,Long> {
}
