package com.example.coderelief.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;

public class CareCenterInfoFragment extends Fragment {
    
    private static final String TAG = "CareCenterInfoFragment";
    
    private TextView tvTitle, tvCareCenterName, tvAddress, tvPhone, tvRating, tvReviewCount;
    private TextView tvDescription;
    private Button btnConsultation, btnCall, btnShowOnMap;
    private ImageView ivCareCenterPhoto;
    private RatingBar ratingBar;
    private MaterialToolbar toolbar;
    
    private Long institutionId;
    private String name, address, phone, photoUrl, description;
    private Double rating, distance;
    private Integer reviewCount;
    private Double latitude, longitude;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_care_center_info, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        setupClickListeners();
        loadCareCenterData();
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        tvTitle = view.findViewById(R.id.tv_title);
        tvCareCenterName = view.findViewById(R.id.tv_care_center_name);
        tvAddress = view.findViewById(R.id.tv_address);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvRating = view.findViewById(R.id.tv_rating);
        tvReviewCount = view.findViewById(R.id.tv_review_count);
        tvDescription = view.findViewById(R.id.tv_description);
        btnConsultation = view.findViewById(R.id.btn_consultation);
        btnCall = view.findViewById(R.id.btn_call);
        btnShowOnMap = view.findViewById(R.id.btn_show_on_map);
        ivCareCenterPhoto = view.findViewById(R.id.iv_care_center_photo);
        ratingBar = view.findViewById(R.id.rating_bar);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            // 기본 정보
            institutionId = args.getLong("institution_id", -1);
            name = args.getString("care_center_name", args.getString("name", ""));
            address = args.getString("care_center_address", args.getString("address", ""));
            phone = args.getString("care_center_phone", args.getString("phone", ""));
            
            // 추가 정보 (FindCareCenterFragment에서 전달받은 데이터)
            photoUrl = args.getString("care_center_photo_url", "");
            description = args.getString("care_center_description", "");
            rating = args.getDouble("care_center_rating", 0.0);
            reviewCount = args.getInt("care_center_review_count", 0);
            distance = args.getDouble("care_center_distance", 0.0);
            latitude = args.getDouble("care_center_lat", 0.0);
            longitude = args.getDouble("care_center_lng", 0.0);
            
            Log.d(TAG, "전달받은 데이터 - 이름: " + name + ", 평점: " + rating + ", 리뷰: " + reviewCount);
            Log.d(TAG, "전화번호: " + phone + ", 사진URL: " + photoUrl + ", 설명: " + description);
            Log.d(TAG, "위치: " + latitude + ", " + longitude + ", 거리: " + distance);
        } else {
            institutionId = -1L;
            name = "";
            address = "";
            phone = "";
            photoUrl = "";
            description = "";
            rating = 0.0;
            reviewCount = 0;
            distance = 0.0;
            latitude = 0.0;
            longitude = 0.0;
        }
    }
    
    private void setupClickListeners() {
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).onBackPressed();
            }
        });
        
        btnConsultation.setOnClickListener(v -> {
            navigateToConsultation();
        });
        
        btnCall.setOnClickListener(v -> {
            makePhoneCall();
        });
        
        btnShowOnMap.setOnClickListener(v -> {
            showOnMap();
        });
    }
    
    private void loadCareCenterData() {
        Log.d(TAG, "loadCareCenterData 시작 - institutionId: " + institutionId);
        
        if (institutionId == -1) {
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), 
                    "요양원 정보를 불러올 수 없습니다", 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
            return;
        }
        
        // 요양원 이름
        tvCareCenterName.setText(name);
        Log.d(TAG, "요양원 이름 설정: " + name);
        
        // 주소 정보
        if (address != null && !address.isEmpty()) {
            tvAddress.setText("주소: " + address);
        } else {
            tvAddress.setText("주소: 정보 없음");
        }
        
        // 전화번호 정보
        Log.d(TAG, "전화번호 설정 시도 - phone: '" + phone + "'");
        if (phone != null && !phone.isEmpty()) {
            tvPhone.setText("전화: " + phone);
            Log.d(TAG, "전화번호 설정 완료: " + phone);
        } else {
            tvPhone.setText("전화: 정보 없음");
            Log.d(TAG, "전화번호가 없어 '정보 없음' 표시");
        }
        
        // 평점 정보 (실제 API 데이터 사용)
        if (rating != null && rating > 0) {
            ratingBar.setRating(rating.floatValue());
            tvRating.setText(String.format("%.1f", rating));
        } else {
            ratingBar.setRating(0f);
            tvRating.setText("평점 없음");
        }
        
        // 리뷰 개수 (실제 API 데이터 사용)
        if (reviewCount != null && reviewCount > 0) {
            tvReviewCount.setText("(" + reviewCount + "개 리뷰)");
        } else {
            tvReviewCount.setText("(리뷰 없음)");
        }
        
        // 시설 소개 (실제 API 데이터 사용)
        if (description != null && !description.isEmpty()) {
            tvDescription.setText(description);
        } else {
            tvDescription.setText("시설 소개 정보가 없습니다.");
        }
        
        // 요양원 사진 로드 (Glide 사용)
        loadCareCenterPhoto();
    }
    
    private void loadCareCenterPhoto() {
        Log.d(TAG, "loadCareCenterPhoto 시작 - photoUrl: " + photoUrl);
        
        if (photoUrl != null && !photoUrl.isEmpty()) {
            // Glide를 사용하여 이미지 로드
            Log.d(TAG, "Glide로 이미지 로드 시도: " + photoUrl);
            Glide.with(this)
                .load(photoUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .placeholder(R.drawable.ic_menu_gallery)
                .error(R.drawable.ic_menu_gallery)
                .into(ivCareCenterPhoto);
        } else {
            // 기본 이미지 표시
            Log.d(TAG, "photoUrl이 없어 기본 이미지 표시");
            ivCareCenterPhoto.setImageResource(R.drawable.ic_menu_gallery);
        }
    }
    
    private void navigateToConsultation() {
        // 상담 화면으로 이동
        if (getActivity() instanceof DashboardActivity) {
            ConsultationFragment fragment = new ConsultationFragment();
            
            // 요양원 정보와 이전 Fragment 정보를 Bundle로 전달
            Bundle args = new Bundle();
            args.putString("institution_name", name);
            args.putString("care_center_name", name);
            args.putString("name", name);
            args.putString("previous_fragment_tag", "CareCenterInfoFragment");
            fragment.setArguments(args);
            
            ((DashboardActivity) getActivity()).navigateToFragment(fragment);
        }
    }
    
    private void makePhoneCall() {
        if (phone != null && !phone.isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
            } catch (Exception e) {
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), 
                        "전화 앱을 실행할 수 없습니다", 
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (getContext() != null) {
                android.widget.Toast.makeText(getContext(), 
                    "전화번호 정보가 없습니다", 
                    android.widget.Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void showOnMap() {
        // 위도/경도가 있으면 정확한 위치로, 없으면 주소로 검색
        if (latitude != null && longitude != null && latitude != 0.0 && longitude != 0.0) {
            // 정확한 좌표로 지도 표시
            Uri geoUri = Uri.parse("geo:" + latitude + "," + longitude + "?q=" + Uri.encode(name));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            
            try {
                startActivity(mapIntent);
            } catch (Exception e) {
                // Google Maps가 설치되지 않은 경우 일반 지도 앱 사용
                Intent genericMapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                try {
                    startActivity(genericMapIntent);
                } catch (Exception ex) {
                    if (getContext() != null) {
                        android.widget.Toast.makeText(getContext(), 
                            "지도 앱을 실행할 수 없습니다", 
                            android.widget.Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } else {
            // 주소로 검색
            String address = extractAddress(tvAddress.getText().toString());
            if (address != null && !address.isEmpty()) {
                Uri geoUri = Uri.parse("geo:0,0?q=" + Uri.encode(address));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                
                try {
                    startActivity(mapIntent);
                } catch (Exception e) {
                    // If Google Maps is not installed, use generic intent
                    Intent genericMapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                    try {
                        startActivity(genericMapIntent);
                    } catch (Exception ex) {
                        if (getContext() != null) {
                            android.widget.Toast.makeText(getContext(), 
                                "지도 앱을 실행할 수 없습니다", 
                                android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            } else {
                if (getContext() != null) {
                    android.widget.Toast.makeText(getContext(), 
                        "위치 정보가 없습니다", 
                        android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    private String extractAddress(String addressText) {
        // Extract address from text like "주소: 서울시 강남구 테헤란로 123"
        if (addressText != null && addressText.contains(":")) {
            String[] parts = addressText.split(":");
            if (parts.length > 1) {
                return parts[1].trim();
            }
        }
        return null;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when returning to this fragment
        loadCareCenterData();
    }
}