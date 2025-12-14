package com.ansimyoyang.repository;

import com.ansimyoyang.domain.CaregiverAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaregiverAccountRepository extends JpaRepository<CaregiverAccount, Long> {
    
    Optional<CaregiverAccount> findByUsername(String username);
    
    boolean existsByUsername(String username);
}
