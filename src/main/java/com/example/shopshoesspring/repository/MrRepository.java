package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.Mr;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MrRepository extends JpaRepository<Mr, Long> {
    List<Mr> findByMrTypeId(Long typeId);
}
