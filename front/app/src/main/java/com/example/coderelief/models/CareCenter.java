package com.example.coderelief.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * 요양원 검색 결과 모델 클래스
 * DB 스키마 institution 테이블과 매핑 (검색 결과 표시용)
 */
public class CareCenter {
    private Long institutionId;
    private String name;
    private String address;
    private String phone;
    private String description;
    private Double distanceKm; // 사용자 위치로부터의 거리
    private LatLng latLng; // 위도, 경도 정보
    private String photoUrl; // 요양원 사진 URL
    private Double rating; // 평점 (0.0 ~ 5.0)
    private Integer userRatingsTotal; // 총 리뷰 수

    // 완전한 생성자 (모든 정보 포함)
    public CareCenter(Long institutionId, String name, String address, String phone, Double distanceKm, LatLng latLng, String photoUrl, Double rating, Integer userRatingsTotal) {
        this.institutionId = institutionId;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.distanceKm = distanceKm;
        this.latLng = latLng;
        this.photoUrl = photoUrl;
        this.rating = rating;
        this.userRatingsTotal = userRatingsTotal;
    }

    // Getters and Setters
    public Long getInstitutionId() {
        return institutionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }
    
    public LatLng getLatLng() {
        return latLng;
    }
    
    public String getPhotoUrl() {
        return photoUrl;
    }
    
    public Double getRating() {
        return rating;
    }
    
    public Integer getUserRatingsTotal() {
        return userRatingsTotal;
    }

    /**
     * 거리 표시용 포맷팅
     */
    public String getFormattedDistance() {
        if (distanceKm == null) return "거리 정보 없음";
        if (distanceKm < 1.0) {
            return String.format("%.0fm", distanceKm * 1000);
        } else {
            return String.format("%.1fkm", distanceKm);
        }
    }

    @Override
    public String toString() {
        return "CareCenter{" +
                "institutionId=" + institutionId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", distanceKm=" + distanceKm +
                '}';
    }
}