package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Guardian;
import com.ansimyoyang.domain.GuardianAccount;
import com.ansimyoyang.repository.GuardianAccountRepository;
import com.ansimyoyang.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/guardian")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class GuardianAuthController {

    private final GuardianAccountRepository guardianAccountRepository;
    private final GuardianRepository guardianRepository;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String username = request.get("username");
        String password = request.get("password");
        
        // 실제 데이터베이스에서 사용자 조회
        Optional<GuardianAccount> accountOpt = guardianAccountRepository.findByUsername(username);
        
        if (accountOpt.isPresent()) {
            GuardianAccount account = accountOpt.get();
            // 비밀번호 비교 (평문 비교 - 실제로는 BCrypt 등 사용 권장)
            if (password.equals(account.getPassword())) {
                log.info("보호자 로그인 성공: username={}", username);
                
                // account_id로 실제 guardian 정보 조회
                Optional<Guardian> guardianOpt = guardianRepository.findByAccountId(account.getId());
                
                Long guardianId = null;
                String guardianName = null;
                String guardianPhone = null;
                boolean hasGuardianData = false;
                
                if (guardianOpt.isPresent()) {
                    Guardian guardian = guardianOpt.get();
                    guardianId = guardian.getGuardianId();
                    guardianName = guardian.getName();
                    guardianPhone = guardian.getPhone();
                    hasGuardianData = true;
                    log.info("연결된 보호자 정보: guardianId={}, name={}", guardianId, guardianName);
                } else {
                    log.warn("계정은 존재하지만 연결된 보호자 정보가 없습니다: accountId={}", account.getId());
                }
                
                response.put("status", "SUCCESS");
                response.put("role", "guardian");
                response.put("type", "Bearer");
                response.put("token", "guardian_token_" + System.currentTimeMillis());
                response.put("username", username);
                response.put("account_id", account.getId());
                response.put("guardian_id", guardianId);
                response.put("guardian_name", guardianName);
                response.put("guardian_phone", guardianPhone);
                response.put("has_guardian_data", hasGuardianData);
                
                if (!hasGuardianData) {
                    response.put("message", "회원가입은 완료되었지만, 아직 연결된 환자 정보가 없습니다.");
                }
                
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
        String name = (String) request.get("name");
        String phone = (String) request.get("phone");
        String email = (String) request.get("email");
        
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
        
        // 중복 아이디 확인
        if (guardianAccountRepository.existsByUsername(username)) {
            response.put("status", "FAILED");
            response.put("message", "이미 사용 중인 아이디입니다");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            // 새 계정 생성
            GuardianAccount newAccount = GuardianAccount.builder()
                    .username(username)
                    .password(password) // 실제로는 암호화 필요
                    .build();
            
            GuardianAccount savedAccount = guardianAccountRepository.save(newAccount);
            
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
