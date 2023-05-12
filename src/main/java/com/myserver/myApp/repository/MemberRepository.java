package com.myserver.myApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myserver.myApp.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);
}
