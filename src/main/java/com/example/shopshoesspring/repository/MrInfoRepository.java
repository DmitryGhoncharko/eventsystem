package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.MrInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MrInfoRepository extends JpaRepository<MrInfo, Long> {
    List<MrInfo> findByMrId(Long mrId);
}
