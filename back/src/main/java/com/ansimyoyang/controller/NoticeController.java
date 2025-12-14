package com.ansimyoyang.controller;

import com.ansimyoyang.domain.Notice;
import com.ansimyoyang.domain.dto.NoticeDto;
import com.ansimyoyang.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // front 연동을 위한 CORS 설정
@Slf4j
public class NoticeController {

    private final NoticeService noticeService;

    /**
     * 공지사항 작성
     */
    @PostMapping
    public java.util.Map<String, Object> createNotice(@RequestBody NoticeDto dto) {
        try {
            log.info("공지사항 작성 요청: title={}, institutionId={}, caregiverId={}", 
                dto.getTitle(), dto.getInstitutionId(), dto.getCaregiverId());
            
            Notice savedNotice = noticeService.createNotice(dto);
            
            log.info("공지사항 작성 성공: noticeId={}", savedNotice.getNoticeId());
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "공지사항이 성공적으로 작성되었습니다");
            response.put("noticeId", savedNotice.getNoticeId());
            response.put("data", savedNotice);
            
            return response;
        } catch (Exception e) {
            log.error("공지사항 작성 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "공지사항 작성 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 공지사항 상세 조회
     */
    @GetMapping("/{noticeId}")
    public java.util.Map<String, Object> getNoticeById(@PathVariable Long noticeId) {
        try {
            Notice notice = noticeService.getNoticeById(noticeId);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notice);
            
            return response;
        } catch (Exception e) {
            log.error("공지사항 조회 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 기관별 전체 공지사항 목록 조회 (페이지네이션)
     */
    @GetMapping("/institution/{institutionId}")
    public java.util.Map<String, Object> getNoticesByInstitution(
            @PathVariable Long institutionId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt") String sort,
            @RequestParam(name = "direction", defaultValue = "desc") String direction) {
        
        try {
            Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction) ? 
                Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
            
            Page<Notice> notices = noticeService.getNoticesByInstitution(institutionId, pageable);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notices.getContent());
            response.put("totalElements", notices.getTotalElements());
            response.put("totalPages", notices.getTotalPages());
            response.put("currentPage", notices.getNumber());
            response.put("size", notices.getSize());
            
            return response;
        } catch (Exception e) {
            log.error("기관별 공지사항 조회 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "공지사항 목록 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 기관별 개별 공지사항 목록 조회
     */
    @GetMapping("/institution/{institutionId}/personal/{patientId}")
    public java.util.Map<String, Object> getPersonalNoticesByInstitution(
            @PathVariable Long institutionId,
            @PathVariable Long patientId) {
        
        try {
            List<Notice> notices = noticeService.getPersonalNoticesByInstitution(institutionId, patientId);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notices);
            response.put("count", notices.size());
            
            return response;
        } catch (Exception e) {
            log.error("개별 공지사항 조회 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "개별 공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 환자별 개별 공지사항 목록 조회
     */
    @GetMapping("/patient/{patientId}")
    public java.util.Map<String, Object> getPersonalNoticesByPatient(@PathVariable Long patientId) {
        try {
            List<Notice> notices = noticeService.getPersonalNoticesByPatient(patientId);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notices);
            response.put("count", notices.size());
            
            return response;
        } catch (Exception e) {
            log.error("환자별 공지사항 조회 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "환자별 공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 요양보호사가 작성한 공지사항 목록 조회
     */
    @GetMapping("/caregiver/{caregiverId}")
    public java.util.Map<String, Object> getNoticesByCaregiver(@PathVariable Long caregiverId) {
        try {
            List<Notice> notices = noticeService.getNoticesByCaregiver(caregiverId);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notices);
            response.put("count", notices.size());
            
            return response;
        } catch (Exception e) {
            log.error("요양보호사별 공지사항 조회 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "요양보호사별 공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 공지사항 제목으로 검색
     */
    @GetMapping("/search")
    public java.util.Map<String, Object> searchNoticesByTitle(
            @RequestParam(name = "institutionId") Long institutionId,
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<Notice> notices = noticeService.searchNoticesByTitle(institutionId, keyword, pageable);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notices.getContent());
            response.put("totalElements", notices.getTotalElements());
            response.put("totalPages", notices.getTotalPages());
            response.put("currentPage", notices.getNumber());
            response.put("size", notices.getSize());
            response.put("keyword", keyword);
            
            return response;
        } catch (Exception e) {
            log.error("공지사항 검색 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "공지사항 검색 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 공지사항 수정
     */
    @PutMapping("/{noticeId}")
    public java.util.Map<String, Object> updateNotice(
            @PathVariable Long noticeId,
            @RequestBody NoticeDto dto) {
        
        try {
            log.info("공지사항 수정 요청: noticeId={}", noticeId);
            
            Notice updatedNotice = noticeService.updateNotice(noticeId, dto);
            
            log.info("공지사항 수정 성공: noticeId={}", noticeId);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "공지사항이 성공적으로 수정되었습니다");
            response.put("data", updatedNotice);
            
            return response;
        } catch (Exception e) {
            log.error("공지사항 수정 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "공지사항 수정 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 공지사항 삭제
     */
    @DeleteMapping("/{noticeId}")
    public java.util.Map<String, Object> deleteNotice(@PathVariable Long noticeId) {
        try {
            log.info("공지사항 삭제 요청: noticeId={}", noticeId);
            
            noticeService.deleteNotice(noticeId);
            
            log.info("공지사항 삭제 성공: noticeId={}", noticeId);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("message", "공지사항이 성공적으로 삭제되었습니다");
            
            return response;
        } catch (Exception e) {
            log.error("공지사항 삭제 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "공지사항 삭제 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 최근 공지사항 조회 (홈 화면용)
     */
    @GetMapping("/recent")
    public java.util.Map<String, Object> getRecentNotices(
            @RequestParam(name = "institutionId") Long institutionId,
            @RequestParam(name = "limit", defaultValue = "5") int limit) {
        
        try {
            List<Notice> notices = noticeService.getRecentNotices(institutionId, limit);
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notices);
            response.put("count", notices.size());
            
            return response;
        } catch (Exception e) {
            log.error("최근 공지사항 조회 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "최근 공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 전체 공지사항 목록 조회 (관리자용)
     */
    @GetMapping
    public java.util.Map<String, Object> getAllNotices() {
        try {
            List<Notice> notices = noticeService.getAllNotices();
            
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("success", true);
            response.put("data", notices);
            response.put("count", notices.size());
            
            return response;
        } catch (Exception e) {
            log.error("전체 공지사항 조회 실패: {}", e.getMessage(), e);
            
            java.util.Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "전체 공지사항 조회 중 오류가 발생했습니다: " + e.getMessage());
            
            return errorResponse;
        }
    }

    /**
     * 간단한 테스트용 엔드포인트
     */
    @GetMapping("/test")
    public String test() {
        return "Notice Controller is working!";
    }
}


