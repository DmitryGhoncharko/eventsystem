package com.example.shopshoesspring.repository;

import com.example.shopshoesspring.entity.MrUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MrUserRepository extends JpaRepository<MrUser, Long> {
    List<MrUser> findByRequestAcceptedFalse();
    List<MrUser> findByRequestAcceptedTrue();
    List<MrUser> findByUserId(Long userId);
}
