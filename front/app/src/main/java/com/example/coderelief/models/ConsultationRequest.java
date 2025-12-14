package com.example.coderelief.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.time.LocalDateTime;

public class ConsultationRequest implements Serializable {
    
    private Long requestId;
    private Long institutionId;
    private String institutionName;
    private String applicantName;
    private String applicantPhone;
    private String consultationPurpose;
    private String consultationContent;
    
    @SerializedName("createdAt")
    private LocalDateTime createdAt;
    
    @SerializedName("updatedAt")
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    public ConsultationRequest() {}
    
    // 생성자
    public ConsultationRequest(Long requestId, Long institutionId, String institutionName, String applicantName, 
                             String applicantPhone, String consultationPurpose, String consultationContent,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.requestId = requestId;
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.applicantName = applicantName;
        this.applicantPhone = applicantPhone;
        this.consultationPurpose = consultationPurpose;
        this.consultationContent = consultationContent;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getter와 Setter
    public Long getRequestId() { return requestId; }
    public void setRequestId(Long requestId) { this.requestId = requestId; }
    
    public Long getInstitutionId() { return institutionId; }
    public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
    
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    
    public String getApplicantName() { return applicantName; }
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
    
    public String getApplicantPhone() { return applicantPhone; }
    public void setApplicantPhone(String applicantPhone) { this.applicantPhone = applicantPhone; }
    
    public String getConsultationPurpose() { return consultationPurpose; }
    public void setConsultationPurpose(String consultationPurpose) { this.consultationPurpose = consultationPurpose; }
    
    public String getConsultationContent() { return consultationContent; }
    public void setConsultationContent(String consultationContent) { this.consultationContent = consultationContent; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // 상담 신청 정보 요약 문자열 반환
    public String getConsultationSummary() {
        return String.format("[%s] %s - %s", 
            institutionName, 
            applicantName, 
            consultationPurpose);
    }
    
    // 생성일을 문자열로 반환
    public String getCreatedAtString() {
        if (createdAt != null) {
            return createdAt.toString();
        }
        return "";
    }
}



