package com.ansimyoyang.service;

import com.ansimyoyang.domain.Notice;
import com.ansimyoyang.domain.Caregiver;
import com.ansimyoyang.domain.Institution;
import com.ansimyoyang.domain.Patient;
import com.ansimyoyang.domain.dto.NoticeDto;
import com.ansimyoyang.repository.NoticeRepository;
import com.ansimyoyang.repository.CaregiverRepository;
import com.ansimyoyang.repository.InstitutionRepository;
import com.ansimyoyang.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final CaregiverRepository caregiverRepository;
    private final InstitutionRepository institutionRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;

    /**
     * 공지사항 작성
     */
    public Notice createNotice(NoticeDto dto) {
        log.info("공지사항 작성 요청: title={}, institutionId={}, caregiverId={}", 
                dto.getTitle(), dto.getInstitutionId(), dto.getCaregiverId());

        // 기관 검증
        Institution institution = institutionRepository.findById(dto.getInstitutionId())
                .orElseThrow(() -> new IllegalArgumentException("기관을 찾을 수 없습니다: " + dto.getInstitutionId()));

        // 요양보호사 검증
        Caregiver caregiver = caregiverRepository.findById(dto.getCaregiverId())
                .orElseThrow(() -> new IllegalArgumentException("요양보호사를 찾을 수 없습니다: " + dto.getCaregiverId()));

        // 개별 공지인 경우 환자 검증
        Patient patient = null;
        if (dto.isPersonal() && dto.getPatientId() != null) {
            patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new IllegalArgumentException("환자를 찾을 수 없습니다: " + dto.getPatientId()));
        }

        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .institution(institution)
                .caregiver(caregiver)
                .patient(patient)
                .isPersonal(dto.isPersonal())
                .priority(dto.getPriority())
                .photoUrl(dto.getPhotoUrl())
                .createdAt(LocalDateTime.now())
                .build();

        Notice savedNotice = noticeRepository.save(notice);
        log.info("공지사항 작성 완료: noticeId={}", savedNotice.getNoticeId());
        
        // 공지사항 알림 생성
        try {
            if (dto.isPersonal() && patient != null && patient.getGuardian() != null) {
                // 개별 공지 - 해당 환자의 보호자에게만 알림
                Long guardianId = patient.getGuardian().getGuardianId();
                notificationService.createNoticeNotification(guardianId, 
                    com.ansimyoyang.domain.Notification.UserType.GUARDIAN, 
                    dto.getTitle(), savedNotice.getNoticeId());
                log.info("개별 공지 알림 생성 완료: guardianId={}", guardianId);
            } else {
                // 전체 공지 - 해당 기관의 모든 보호자와 요양보호사에게 알림
                List<Patient> patients = patientRepository.findByInstitution_InstitutionId(dto.getInstitutionId());
                List<Caregiver> caregivers = caregiverRepository.findByInstitution_InstitutionId(dto.getInstitutionId());
                
                // 보호자들에게 알림
                for (Patient p : patients) {
                    if (p.getGuardian() != null) {
                        Long guardianId = p.getGuardian().getGuardianId();
                        notificationService.createNoticeNotification(guardianId, 
                            com.ansimyoyang.domain.Notification.UserType.GUARDIAN, 
                            dto.getTitle(), savedNotice.getNoticeId());
                    }
                }
                
                // 요양보호사들에게 알림
                for (Caregiver c : caregivers) {
                    Long caregiverId = c.getCaregiverId();
                    notificationService.createNoticeNotification(caregiverId, 
                        com.ansimyoyang.domain.Notification.UserType.CAREGIVER, 
                        dto.getTitle(), savedNotice.getNoticeId());
                }
                
                log.info("전체 공지 알림 생성 완료: 보호자 {}명, 요양보호사 {}명", patients.size(), caregivers.size());
            }
        } catch (Exception e) {
            log.warn("공지사항 알림 생성 실패: {}", e.getMessage());
            // 알림 생성 실패해도 공지사항 작성은 성공으로 처리
        }
        
        return savedNotice;
    }

    /**
     * 공지사항 조회 (ID로)
     */
    @Transactional(readOnly = true)
    public Notice getNoticeById(Long noticeId) {
        return noticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("공지사항을 찾을 수 없습니다: " + noticeId));
    }

    /**
     * 기관별 전체 공지사항 목록 조회 (페이지네이션)
     */
    @Transactional(readOnly = true)
    public Page<Notice> getNoticesByInstitution(Long institutionId, Pageable pageable) {
        return noticeRepository.findByInstitution_InstitutionIdAndIsPersonalFalseOrderByCreatedAtDesc(
                institutionId, pageable);
    }

    /**
     * 기관별 개별 공지사항 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Notice> getPersonalNoticesByInstitution(Long institutionId, Long patientId) {
        return noticeRepository.findByInstitution_InstitutionIdAndIsPersonalTrueAndPatient_PatientIdOrderByCreatedAtDesc(
                institutionId, patientId);
    }

    /**
     * 환자별 개별 공지사항 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Notice> getPersonalNoticesByPatient(Long patientId) {
        return noticeRepository.findByPatient_PatientIdOrderByCreatedAtDesc(patientId);
    }

    /**
     * 요양보호사가 작성한 공지사항 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Notice> getNoticesByCaregiver(Long caregiverId) {
        return noticeRepository.findByCaregiver_CaregiverIdOrderByCreatedAtDesc(caregiverId);
    }

    /**
     * 공지사항 제목으로 검색
     */
    @Transactional(readOnly = true)
    public Page<Notice> searchNoticesByTitle(Long institutionId, String keyword, Pageable pageable) {
        return noticeRepository.searchByTitle(institutionId, keyword, pageable);
    }

    /**
     * 공지사항 수정
     */
    public Notice updateNotice(Long noticeId, NoticeDto dto) {
        log.info("공지사항 수정 요청: noticeId={}", noticeId);

        Notice notice = getNoticeById(noticeId);
        
        // 제목과 내용만 수정 가능 (기관, 환자 등은 변경 불가)
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setPriority(dto.getPriority());
        notice.setPhotoUrl(dto.getPhotoUrl());
        notice.setUpdatedAt(LocalDateTime.now());

        Notice updatedNotice = noticeRepository.save(notice);
        log.info("공지사항 수정 완료: noticeId={}", noticeId);
        
        return updatedNotice;
    }

    /**
     * 공지사항 삭제
     */
    public void deleteNotice(Long noticeId) {
        log.info("공지사항 삭제 요청: noticeId={}", noticeId);
        
        Notice notice = getNoticeById(noticeId);
        noticeRepository.delete(notice);
        
        log.info("공지사항 삭제 완료: noticeId={}", noticeId);
    }

    /**
     * 전체 공지사항 목록 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    /**
     * 최근 공지사항 조회 (홈 화면용)
     */
    @Transactional(readOnly = true)
    public List<Notice> getRecentNotices(Long institutionId, int limit) {
        return noticeRepository.findByInstitution_InstitutionIdAndIsPersonalFalseOrderByCreatedAtDesc(
                institutionId, Pageable.ofSize(limit)).getContent();
    }
}


