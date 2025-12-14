package com.example.coderelief.models;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

public class Institution {
    
    @SerializedName("institutionId")
    private Long institutionId;
    
    @SerializedName("institutionName")
    private String institutionName;
    
    private String address;
    
    @SerializedName("phoneNumber")
    private String phoneNumber;
    
    private BigDecimal rating;
    
    // 기본 생성자
    public Institution() {}
    
    // 생성자
    public Institution(Long institutionId, String institutionName, String address, 
                      String phoneNumber, BigDecimal rating) {
        this.institutionId = institutionId;
        this.institutionName = institutionName;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }
    
    // Getter와 Setter
    public Long getInstitutionId() { return institutionId; }
    public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
    
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public BigDecimal getRating() { return rating; }
    public void setRating(BigDecimal rating) { this.rating = rating; }
    
    @Override
    public String toString() {
        return "Institution{" +
                "institutionId=" + institutionId +
                ", institutionName='" + institutionName + '\'' +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rating=" + rating +
                '}';
    }
}