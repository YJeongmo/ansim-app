package com.ansimyoyang.repository;

import com.ansimyoyang.domain.ConsultationRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ConsultationRequestRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public ConsultationRequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // RowMapper 정의
    private final RowMapper<ConsultationRequest> rowMapper = (rs, rowNum) -> {
        
        return ConsultationRequest.builder()
                .requestId(rs.getLong("request_id"))
                .institutionId(rs.getLong("institution_id"))
                .institutionName(rs.getString("institution_name"))
                .applicantName(rs.getString("applicant_name"))
                .applicantPhone(rs.getString("applicant_phone"))
                .consultationPurpose(rs.getString("consultation_purpose"))
                .consultationContent(rs.getString("consultation_content"))
                .createdAt(rs.getObject("created_at", java.time.LocalDateTime.class))
                .updatedAt(rs.getObject("updated_at", java.time.LocalDateTime.class))
                .build();
    };
    
    // 모든 상담 신청 조회
    public List<ConsultationRequest> findAll() {
        System.out.println("Repository: 상담신청 목록 조회 시작");
        String sql = "SELECT * FROM consultation_request ORDER BY created_at DESC";
        List<ConsultationRequest> results = jdbcTemplate.query(sql, rowMapper);
        System.out.println("Repository: 조회된 상담신청 개수: " + results.size());
        return results;
    }
    
    // ID로 상담 신청 조회
    public Optional<ConsultationRequest> findById(Long requestId) {
        String sql = "SELECT * FROM consultation_request WHERE request_id = ?";
        List<ConsultationRequest> results = jdbcTemplate.query(sql, rowMapper, requestId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    // 요양원 ID로 상담 신청 조회
    public List<ConsultationRequest> findByInstitutionId(Long institutionId) {
        System.out.println("Repository: 요양원 ID " + institutionId + "로 상담신청 조회 시작");
        String sql = "SELECT * FROM consultation_request WHERE institution_id = ? ORDER BY created_at DESC";
        List<ConsultationRequest> results = jdbcTemplate.query(sql, rowMapper, institutionId);
        System.out.println("Repository: 조회된 상담신청 개수: " + results.size());
        return results;
    }
    
    // 상담 신청 저장
    public ConsultationRequest save(ConsultationRequest request) {
        if (request.getRequestId() == null) {
            // 새로 생성
            String sql = """
                INSERT INTO consultation_request 
                (institution_id, institution_name, applicant_name, applicant_phone, consultation_purpose, 
                 consultation_content, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                """;
            
            jdbcTemplate.update(sql,
                    request.getInstitutionId(),
                    request.getInstitutionName(),
                    request.getApplicantName(),
                    request.getApplicantPhone(),
                    request.getConsultationPurpose(),
                    request.getConsultationContent()
            );
            
            // 생성된 ID 조회
            String idSql = "SELECT LAST_INSERT_ID()";
            Long generatedId = jdbcTemplate.queryForObject(idSql, Long.class);
            request.setRequestId(generatedId);
            
            // createdAt과 updatedAt을 현재 시간으로 설정
            request.setCreatedAt(java.time.LocalDateTime.now());
            request.setUpdatedAt(java.time.LocalDateTime.now());
            
        } else {
            // 기존 데이터 업데이트
            String sql = """
                UPDATE consultation_request SET 
                institution_name = ?, applicant_name = ?, applicant_phone = ?, 
                consultation_purpose = ?, consultation_content = ?, updated_at = CURRENT_TIMESTAMP
                WHERE request_id = ?
                """;
            
            jdbcTemplate.update(sql,
                    request.getInstitutionName(),
                    request.getApplicantName(),
                    request.getApplicantPhone(),
                    request.getConsultationPurpose(),
                    request.getConsultationContent(),
                    request.getRequestId()
            );
        }
        
        return request;
    }
    
    // 상담 신청 삭제
    public boolean deleteById(Long requestId) {
        String sql = "DELETE FROM consultation_request WHERE request_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, requestId);
        return rowsAffected > 0;
    }
    
    // 신청자명으로 검색
    public List<ConsultationRequest> findByApplicantName(String applicantName) {
        String sql = "SELECT * FROM consultation_request WHERE applicant_name LIKE ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, rowMapper, "%" + applicantName + "%");
    }
}
