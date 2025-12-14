package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Caregiver;
import com.ansimyoyang.domain.CaregiverAccount;
import com.ansimyoyang.repository.CaregiverAccountRepository;
import com.ansimyoyang.repository.CaregiverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/caregiver")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class CaregiverAuthController {

    private final CaregiverAccountRepository caregiverAccountRepository;
    private final CaregiverRepository caregiverRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String username = request.get("username");
        String password = request.get("password");
        
        // 실제 데이터베이스에서 사용자 조회
        Optional<CaregiverAccount> accountOpt = caregiverAccountRepository.findByUsername(username);
        
        if (accountOpt.isPresent()) {
            CaregiverAccount account = accountOpt.get();
            // 비밀번호 비교 (평문 비교 - 실제로는 BCrypt 등 사용 권장)
            if (password.equals(account.getPassword())) {
                log.info("로그인 성공: username={}, role={}", username, account.getRole());
                
                // account_id로 실제 caregiver 정보 조회
                Optional<Caregiver> caregiverOpt = caregiverRepository.findByAccountId(account.getId());
                
                Long caregiverId = null;
                Long institutionId = null;
                String caregiverName = null;
                
                if (caregiverOpt.isPresent()) {
                    Caregiver caregiver = caregiverOpt.get();
                    caregiverId = caregiver.getCaregiverId();
                    caregiverName = caregiver.getName();
                    if (caregiver.getInstitution() != null) {
                        institutionId = caregiver.getInstitution().getInstitutionId();
                    }
                    log.info("연결된 직원 정보: caregiverId={}, name={}, institutionId={}", 
                            caregiverId, caregiverName, institutionId);
                } else {
                    log.warn("계정은 존재하지만 연결된 직원 정보가 없습니다: accountId={}", account.getId());
                }
                
                response.put("status", "SUCCESS");
                response.put("role", "caregiver");
                response.put("caregiver_role", account.getRole().name()); // STAFF, MANAGER, ADMIN
                response.put("can_access_consultations", account.getRole().canAccessConsultations());
                response.put("type", "Bearer");
                response.put("token", "caregiver_token_" + System.currentTimeMillis());
                response.put("username", username);
                response.put("account_id", account.getId());
                response.put("caregiver_id", caregiverId);
                response.put("caregiver_name", caregiverName);
                response.put("institution_id", institutionId);
                response.put("institution_code", account.getInstitutionCode());
                
                return ResponseEntity.ok(response);
            }
        }
        
        response.put("status", "FAILED");
        response.put("message", "아이디 또는 비밀번호가 올바르지 않습니다");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        String username = (String) request.get("username");
        String password = (String) request.get("password");
        String institutionCode = (String) request.get("institution_code");
        
        // 필수 필드 검증
        if (username == null || username.trim().isEmpty()) {
            response.put("status", "FAILED");
            response.put("message", "아이디를 입력해주세요");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (password == null || password.trim().isEmpty()) {
            response.put("status", "FAILED");
            response.put("message", "비밀번호를 입력해주세요");
            return ResponseEntity.badRequest().body(response);
        }
        
        if (institutionCode == null || institutionCode.trim().isEmpty()) {
            response.put("status", "FAILED");
            response.put("message", "기관코드를 입력해주세요");
            return ResponseEntity.badRequest().body(response);
        }
        
        // 중복 아이디 확인
        if (caregiverAccountRepository.existsByUsername(username)) {
            response.put("status", "FAILED");
            response.put("message", "이미 사용 중인 아이디입니다");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 새 계정 생성
            CaregiverAccount newAccount = CaregiverAccount.builder()
                    .username(username)
                    .password(password) // 실제로는 암호화 필요
                    .institutionCode(institutionCode)
                    .build();
            
            CaregiverAccount savedAccount = caregiverAccountRepository.save(newAccount);
            
            response.put("status", "SUCCESS");
            response.put("message", "회원가입이 완료되었습니다");
            response.put("account_id", savedAccount.getId());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("status", "FAILED");
            response.put("message", "회원가입 중 오류가 발생했습니다: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
