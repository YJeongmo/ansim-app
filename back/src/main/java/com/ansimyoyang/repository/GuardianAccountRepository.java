package com.ansimyoyang.repository;

import com.ansimyoyang.domain.GuardianAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuardianAccountRepository extends JpaRepository<GuardianAccount, Long> {
    
    Optional<GuardianAccount> findByUsername(String username);
    
    boolean existsByUsername(String username);
}



