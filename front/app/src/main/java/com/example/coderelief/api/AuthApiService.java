package com.example.coderelief.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApiService {
    
    // 로그인 요청 DTO
    class LoginRequest {
        private String username;
        private String password;
        
        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
    
    // 로그인 응답 DTO
    class LoginResponse {
        private String status;
        private String role;
        private String type;
        private String token;
        private String username;
        private String message;
        private Long guardian_id;
        private Long institution_id;
        private Long caregiver_id;
        private Boolean has_guardian_data;
        
        // 요양원 직원 권한 관련 필드들
        private String caregiver_role;
        private Boolean can_access_consultations;
        private String caregiver_name;
        private String guardian_name;
        private String guardian_phone;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getGuardianId() { return guardian_id; }
        public void setGuardianId(Long guardian_id) { this.guardian_id = guardian_id; }
        public Long getInstitutionId() { return institution_id; }
        public void setInstitutionId(Long institution_id) { this.institution_id = institution_id; }
        public Long getCaregiverId() { return caregiver_id; }
        public void setCaregiverId(Long caregiver_id) { this.caregiver_id = caregiver_id; }
        public Boolean getHasGuardianData() { return has_guardian_data; }
        public void setHasGuardianData(Boolean has_guardian_data) { this.has_guardian_data = has_guardian_data; }
        
        // 요양원 직원 권한 관련 getter/setter
        public String getCaregiverRole() { return caregiver_role; }
        public void setCaregiverRole(String caregiver_role) { this.caregiver_role = caregiver_role; }
        public Boolean getCanAccessConsultations() { return can_access_consultations; }
        public void setCanAccessConsultations(Boolean can_access_consultations) { this.can_access_consultations = can_access_consultations; }
        public String getCaregiverName() { return caregiver_name; }
        public void setCaregiverName(String caregiver_name) { this.caregiver_name = caregiver_name; }
        public String getGuardianName() { return guardian_name; }
        public void setGuardianName(String guardian_name) { this.guardian_name = guardian_name; }
        public String getGuardianPhone() { return guardian_phone; }
        public void setGuardianPhone(String guardian_phone) { this.guardian_phone = guardian_phone; }
    }
    
    // 회원가입 요청 DTO
    class SignupRequest {
        private String username;
        private String password;
        private String name;
        private String phone;
        private String email;
        private String institution_code;
        private String address;
        private String relationship;
        
        public SignupRequest(String username, String password, String name, String phone, String email, String institutionCode, String address, String relationship) {
            this.username = username;
            this.password = password;
            this.name = name;
            this.phone = phone;
            this.email = email;
            this.institution_code = institutionCode;
            this.address = address;
            this.relationship = relationship;
        }
        
        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getInstitutionCode() { return institution_code; }
        public void setInstitutionCode(String institution_code) { this.institution_code = institution_code; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getRelationship() { return relationship; }
        public void setRelationship(String relationship) { this.relationship = relationship; }
    }
    
    // 회원가입 응답 DTO
    class SignupResponse {
        private String status;
        private String message;
        private Long account_id;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Long getAccountId() { return account_id; }
        public void setAccountId(Long account_id) { this.account_id = account_id; }
    }
    
    // 보호자 로그인
    @POST("api/auth/guardian/login")
    Call<LoginResponse> guardianLogin(@Body LoginRequest request);
    
    // 요양보호사 로그인
    @POST("api/auth/caregiver/login")
    Call<LoginResponse> caregiverLogin(@Body LoginRequest request);
    
    // 보호자 회원가입
    @POST("api/auth/guardian/signup")
    Call<SignupResponse> guardianSignup(@Body SignupRequest request);
    
    // 요양보호사 회원가입
    @POST("api/auth/caregiver/signup")
    Call<SignupResponse> caregiverSignup(@Body SignupRequest request);
}
