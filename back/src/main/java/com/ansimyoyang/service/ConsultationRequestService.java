package com.ansimyoyang.service;

import com.ansimyoyang.domain.ConsultationRequest;
import com.ansimyoyang.repository.ConsultationRequestRepository;
import com.ansimyoyang.repository.CaregiverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultationRequestService {
    
    private final ConsultationRequestRepository consultationRequestRepository;
    private final CaregiverRepository caregiverRepository;
    private final NotificationService notificationService;
    
    // 특정 요양원의 상담 신청 조회
    public List<ConsultationRequest> getConsultationRequestsByInstitution(Long institutionId) {
        System.out.println("Service: 요양원 ID " + institutionId + "의 상담신청 목록 조회 시작");
        List<ConsultationRequest> requests = consultationRequestRepository.findByInstitutionId(institutionId);
        System.out.println("Service: Repository에서 조회된 개수: " + requests.size());
        
        // 상담신청 정보 로깅
        requests.forEach(request -> {
            System.out.println("Service: 상담신청 ID=" + request.getRequestId() + 
                             ", 요양원ID=" + request.getInstitutionId() +
                             ", 요양원=" + request.getInstitutionName() +
                             ", 신청자=" + request.getApplicantName());
        });
        
        return requests;
    }
    
    // ID로 상담 신청 조회
    public Optional<ConsultationRequest> getConsultationRequestById(Long requestId) {
        return consultationRequestRepository.findById(requestId);
    }
    
    // 상담 신청 저장 (새로 생성 또는 업데이트)
    public ConsultationRequest saveConsultationRequest(ConsultationRequest request) {
        // 새로 생성하는 경우 기본 상태 설정
        boolean isNewRequest = request.getRequestId() == null;
        
        // 새로운 상담신청 저장
        ConsultationRequest savedRequest = consultationRequestRepository.save(request);
        
        // 새로운 상담 신청인 경우 기관 요양보호사들에게 알림 생성
        if (isNewRequest) {
            try {
                // 기관의 요양보호사들에게 알림
                List<com.ansimyoyang.domain.Caregiver> caregivers = caregiverRepository.findByInstitution_InstitutionId(request.getInstitutionId());
                String title = "새로운 상담 신청";
                String message = request.getApplicantName() + "님이 " + request.getInstitutionName() + " 기관에 상담을 신청했습니다.";
                
                for (com.ansimyoyang.domain.Caregiver caregiver : caregivers) {
                    notificationService.createConsultationNotification(
                        caregiver.getCaregiverId(),
                        request.getApplicantName(),
                        savedRequest.getRequestId()
                    );
                }
            } catch (Exception e) {
                // 알림 생성 실패해도 상담 신청 저장은 성공으로 처리
                System.out.println("상담 신청 알림 생성 실패: " + e.getMessage());
            }
        }
        
        return savedRequest;
    }
    
    // 상담 신청 삭제
    public boolean deleteConsultationRequest(Long requestId) {
        return consultationRequestRepository.deleteById(requestId);
    }
    
    // 신청자명으로 검색
    public List<ConsultationRequest> searchByApplicantName(String applicantName) {
        if (applicantName == null || applicantName.trim().isEmpty()) {
            return consultationRequestRepository.findAll();
        }
        return consultationRequestRepository.findByApplicantName(applicantName.trim());
    }
    
    // 전체 상담 신청 개수 조회
    public long getTotalRequestCount() {
        return consultationRequestRepository.findAll().size();
    }
    
    // 오늘 신청된 상담 신청 개수 조회
    public long getTodayRequestCount() {
        List<ConsultationRequest> allRequests = consultationRequestRepository.findAll();
        return allRequests.stream()
                .filter(request -> request.getCreatedAt() != null && 
                        request.getCreatedAt().toLocalDate().equals(java.time.LocalDate.now()))
                .count();
    }
}



