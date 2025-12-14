package com.ansimyoyang.controller;

import com.ansimyoyang.domain.ConsultationRequest;
import com.ansimyoyang.service.ConsultationRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultation-requests")
@CrossOrigin(origins = "*")
@Slf4j
public class ConsultationRequestController {
    
    private final ConsultationRequestService consultationRequestService;
    
    public ConsultationRequestController(ConsultationRequestService consultationRequestService) {
        this.consultationRequestService = consultationRequestService;
    }
    
    // 특정 요양원의 상담 신청 조회 (MANAGER, ADMIN만 접근 가능)
    // 실제 프론트엔드에서 로그인 시 받은 caregiver_role과 can_access_consultations 값을 확인하여
    // 메뉴 표시 여부를 결정해야 합니다.
    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<ConsultationRequest>> getConsultationRequestsByInstitution(
            @PathVariable Long institutionId,
            @RequestHeader(value = "X-Caregiver-Role", required = false) String caregiverRole) {
        try {
            log.info("GET /api/consultation-requests/institution/{} - 요청 받음, role={}", institutionId, caregiverRole);
            
            // 권한 체크 (선택적 - 프론트엔드에서 메뉴 제어로 대체 가능)
            if (caregiverRole != null && !"MANAGER".equals(caregiverRole) && !"ADMIN".equals(caregiverRole)) {
                log.warn("권한 없음: role={}", caregiverRole);
                return ResponseEntity.status(403).build(); // Forbidden
            }
            
            List<ConsultationRequest> requests = consultationRequestService.getConsultationRequestsByInstitution(institutionId);
            log.info("조회된 상담 신청 개수: {}", requests.size());
            
            // 각 상담신청의 상세 로깅
            for (int i = 0; i < requests.size(); i++) {
                ConsultationRequest req = requests.get(i);
                System.out.println("상담신청 " + (i+1) + ": ID=" + req.getRequestId() + 
                                 ", 요양원ID=" + req.getInstitutionId() +
                                 ", 요양원=" + req.getInstitutionName() +
                                 ", 신청자=" + req.getApplicantName());
            }
            
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            System.err.println("상담 신청 조회 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ID로 상담 신청 조회
    @GetMapping("/{requestId}")
    public ResponseEntity<ConsultationRequest> getConsultationRequestById(@PathVariable Long requestId) {
        try {
            return consultationRequestService.getConsultationRequestById(requestId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 새로운 상담 신청 생성 (비회원용)
    @PostMapping
    public ResponseEntity<ConsultationRequest> createConsultationRequest(@RequestBody ConsultationRequest request) {
        try {
            System.out.println("=== 상담신청 생성 요청 받음 ===");
            System.out.println("POST /api/consultation-requests");
            System.out.println("요청 데이터 전체: " + request.toString());
            
            // 각 필드별 상세 로깅
            System.out.println("요양원명: " + request.getInstitutionName());
            System.out.println("신청자명: " + request.getApplicantName());
            System.out.println("연락처: " + request.getApplicantPhone());
            System.out.println("상담 목적: " + request.getConsultationPurpose());
            System.out.println("상담 내용: " + request.getConsultationContent());
            
            // 필수 필드 검증
            if (request.getInstitutionName() == null || request.getInstitutionName().trim().isEmpty()) {
                System.out.println("❌ 요양원명 누락");
                return ResponseEntity.badRequest().build();
            }
            if (request.getApplicantName() == null || request.getApplicantName().trim().isEmpty()) {
                System.out.println("❌ 신청자명 누락");
                return ResponseEntity.badRequest().build();
            }
            if (request.getApplicantPhone() == null || request.getApplicantPhone().trim().isEmpty()) {
                System.out.println("❌ 연락처 누락");
                return ResponseEntity.badRequest().build();
            }
            if (request.getConsultationPurpose() == null || request.getConsultationPurpose().trim().isEmpty()) {
                System.out.println("❌ 상담 목적 누락");
                return ResponseEntity.badRequest().build();
            }
            if (request.getConsultationContent() == null || request.getConsultationContent().trim().isEmpty()) {
                System.out.println("❌ 상담 내용 누락");
                return ResponseEntity.badRequest().build();
            }
            System.out.println("✅ 모든 필드 검증 통과");
            
            System.out.println("Service 호출 시작...");
            
            ConsultationRequest savedRequest = consultationRequestService.saveConsultationRequest(request);
            System.out.println("✅ 상담 신청 저장 완료: ID=" + savedRequest.getRequestId());
            return ResponseEntity.ok(savedRequest);
        } catch (Exception e) {
            System.err.println("❌ 상담 신청 생성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 상담 신청 삭제
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Map<String, Object>> deleteConsultationRequest(@PathVariable Long requestId) {
        try {
            boolean deleted = consultationRequestService.deleteConsultationRequest(requestId);
            if (deleted) {
                return ResponseEntity.ok(Map.of("success", true, "message", "상담 신청이 삭제되었습니다."));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 신청자명으로 검색
    @GetMapping("/search/applicant")
    public ResponseEntity<List<ConsultationRequest>> searchByApplicantName(
            @RequestParam String applicantName) {
        try {
            List<ConsultationRequest> requests = consultationRequestService.searchByApplicantName(applicantName);
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // 통계 정보 조회
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getConsultationRequestStats() {
        try {
            long totalCount = consultationRequestService.getTotalRequestCount();
            long todayCount = consultationRequestService.getTodayRequestCount();
            
            return ResponseEntity.ok(Map.of(
                "totalCount", totalCount,
                "todayCount", todayCount
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}



