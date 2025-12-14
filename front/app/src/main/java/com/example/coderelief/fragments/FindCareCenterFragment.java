package com.example.coderelief.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.graphics.Rect;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;
import com.example.coderelief.adapters.CareCenterAdapter;
import com.example.coderelief.adapters.RegionAdapter;
import com.example.coderelief.fragments.CareCenterInfoFragment;
import com.example.coderelief.models.CareCenter;
import com.example.coderelief.models.RegionData;
import com.example.coderelief.services.PublicDataApiService;
import com.example.coderelief.utils.MarkerLabelGenerator;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FindCareCenterFragment extends Fragment implements CareCenterAdapter.OnCareCenterActionListener, OnMapReadyCallback {

    private static final String TAG = "FindCareCenterFragment";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int DEFAULT_RADIUS = 5000; // 기본 검색 반경 5km
    
    // 검색 반경 옵션
    private static final int RADIUS_3KM = 3000;
    private static final int RADIUS_5KM = 5000;
    private static final int RADIUS_10KM = 10000;
    
    private RecyclerView recyclerViewCareCenters;
    private CareCenterAdapter careCenterAdapter;
    private List<CareCenter> careCenters;
    
    // 지도 관련 변수들
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private final List<Marker> markers = new ArrayList<>();
    private PublicDataApiService publicDataApiService;
    
    // 검색 설정
    private int searchRadius = DEFAULT_RADIUS; // final 제거하여 변경 가능하게
    
    // 검색 결과 캐싱을 위한 변수들 (정적 변수로 앱 전체에서 유지)
    private static boolean hasSearched = false;
    private static long lastSearchTime = 0;
    private static final long SEARCH_CACHE_DURATION = 5 * 60 * 1000; // 5분 캐시
    private static final List<CareCenter> cachedCareCenters = new ArrayList<>();
    private static Location cachedCurrentLocation = null;
    
    // 지도 초기화 상태 확인을 위한 플래그
    private boolean isMapInitialized = false;
    
    // 마커 라벨 생성기
    private MarkerLabelGenerator markerLabelGenerator;
    
    // UI 요소
    private MaterialToolbar toolbar;
    private FloatingActionButton btnRefresh;
    private FloatingActionButton btnRegionSearch;
    private ImageButton btnSettings;
    private Button btnSearchHere;
    private LinearLayout layoutLoading;
    private ProgressBar progressBar;
    private TextView tvLoading;
    private TextView tvResultCount;
    private LinearLayout bottomSheet;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_find_care_center, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // 공공데이터 API 서비스 초기화
        publicDataApiService = new PublicDataApiService(requireContext());
        
        // 위치 서비스 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        
        // 마커 라벨 생성기 초기화
        markerLabelGenerator = new MarkerLabelGenerator(requireContext());
        
        initViews(view);
        setupRecyclerView();
        setupMap();
        setupClickListeners();
        
        // 캐시된 데이터 복원
        restoreCachedData();
        
        // 캐시된 데이터가 있으면 복원, 없으면 새로 검색
        if (hasSearched && !cachedCareCenters.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSearchTime < SEARCH_CACHE_DURATION) {
                // 캐시가 유효한 경우 지도 준비 후 결과 복원
                Log.d(TAG, "캐시된 데이터가 있어 검색을 건너뜁니다");
            } else {
                // 캐시가 만료된 경우 새로 검색
                Log.d(TAG, "캐시가 만료되어 새로 검색합니다");
                hasSearched = false;
                startAutoLocationAndSearch();
            }
        } else {
            // 처음 진입하는 경우 새로 검색
            Log.d(TAG, "처음 진입하여 새로 검색합니다");
            startAutoLocationAndSearch();
        }
    }
    
    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        recyclerViewCareCenters = view.findViewById(R.id.recycler_view_care_centers);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnRegionSearch = view.findViewById(R.id.btn_region_search);
        btnSettings = view.findViewById(R.id.btn_settings);
        btnSearchHere = view.findViewById(R.id.btn_search_here);
        layoutLoading = view.findViewById(R.id.layout_loading);
        progressBar = view.findViewById(R.id.progress_bar);
        tvLoading = view.findViewById(R.id.tv_loading);
        tvResultCount = view.findViewById(R.id.tv_result_count);
        bottomSheet = view.findViewById(R.id.bottom_sheet);
        
        // Bottom Sheet Behavior 설정
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        setupBottomSheetBehavior();
    }
    
    private void setupRecyclerView() {
        careCenters = new ArrayList<>();
        
        careCenterAdapter = new CareCenterAdapter(careCenters, this);
        recyclerViewCareCenters.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewCareCenters.setAdapter(careCenterAdapter);
    }
    
    private void setupBottomSheetBehavior() {
        // Bottom Sheet Behavior 속성 설정
        bottomSheetBehavior.setHideable(false); // 완전히 숨김 방지
        bottomSheetBehavior.setSkipCollapsed(false);
        
        // 화면 크기 계산
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int statusBarHeight = getStatusBarHeight();
        
        // 고정값 사용 (dp를 픽셀로 변환)
        float density = getResources().getDisplayMetrics().density;
        int toolbarHeight = (int) (56 * density); // 56dp
        int fabHeight = (int) (56 * density); // 56dp
        int fabMargin = (int) (16 * density); // 16dp
        int extraMargin = (int) (20 * density); // 20dp 여유 공간
        
        // 새로고침 버튼 아래까지의 최대 높이 계산
        int maxHeight = screenHeight - statusBarHeight - toolbarHeight - fabHeight - (fabMargin * 2) - extraMargin;
        
        // 최소 높이 (현재 peek height)
        int minHeight = getResources().getDimensionPixelSize(R.dimen.bottom_sheet_peek_height);
        
        // Bottom Sheet 높이 설정
        bottomSheetBehavior.setPeekHeight(minHeight);
        
        // 최대 높이 설정 (새로고침 버튼 아래까지)
        ViewGroup.LayoutParams params = bottomSheet.getLayoutParams();
        if (params != null) {
            params.height = maxHeight;
            bottomSheet.setLayoutParams(params);
        }
        
        // Bottom Sheet 초기 상태 설정
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        
        // Bottom Sheet 상태 변경 리스너
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.d(TAG, "Bottom Sheet: Collapsed");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.d(TAG, "Bottom Sheet: Expanded");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.d(TAG, "Bottom Sheet: Hidden");
                        break;
                }
            }
            
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // 슬라이드 중일 때의 처리 (필요시)
            }
        });
    }
    
    // 더미 요양원 추가 메서드
    private void addDummyCareCenter() {
        // 이미 더미 요양원이 있는지 확인 (중복 추가 방지)
        boolean hasDummy = false;
        for (CareCenter center : careCenters) {
            if (center.getInstitutionId() != null && center.getInstitutionId().equals(1L) && 
                "요양원 A".equals(center.getName())) {
                hasDummy = true;
                break;
            }
        }
        
        if (hasDummy) {
            Log.d(TAG, "더미 요양원이 이미 존재함 - 추가하지 않음");
            return;
        }
        
        LatLng dummyLatLng = new LatLng(37.5665, 126.9780);
        CareCenter dummyCareCenter = new CareCenter(
            1L, // institutionId
            "요양원 A", // name
            "서울시 강남구 테헤란로 123", // address
            "02-1234-5678", // phone
            0.0, // distanceKm
            dummyLatLng, // latLng
            null, // photoUrl
            4.5, // rating
            100 // userRatingsTotal
        );
        
        // 더미 요양원을 리스트의 맨 앞에 추가
        careCenters.add(0, dummyCareCenter);
        careCenterAdapter.notifyItemInserted(0);
        
        // 지도에 더미 요양원 마커 추가 (Google Maps 기본 빨간색 마커)
        if (googleMap != null) {
            Marker dummyMarker = googleMap.addMarker(new MarkerOptions()
                .position(dummyLatLng)
                .title("요양원 A")
                .snippet("테스트용 더미 요양원")
                .icon(markerLabelGenerator.createMarkerWithLabel("요양원 A")));
            dummyMarker.setTag(dummyCareCenter);
            markers.add(dummyMarker);
        }
        
        Log.d(TAG, "더미 요양원 추가 완료");
    }
    
    // 캐시된 데이터 복원
    private void restoreCachedData() {
        try {
            // 캐시된 위치 정보 복원
            if (cachedCurrentLocation != null) {
                currentLocation = cachedCurrentLocation;
                Log.d(TAG, "캐시된 위치 정보 복원: " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
            }
            
            // 캐시된 요양원 목록 복원
            if (!cachedCareCenters.isEmpty()) {
                careCenters.clear();
                careCenters.addAll(cachedCareCenters);
                Log.d(TAG, "캐시된 요양원 목록 복원: " + careCenters.size() + "개");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "캐시된 데이터 복원 실패: " + e.getMessage());
        }
    }
    
    private void setupMap() {
        try {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
            } else {
                Log.e(TAG, "지도 프래그먼트를 찾을 수 없습니다");
                Toast.makeText(getContext(), "지도를 초기화할 수 없습니다", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "지도 초기화 실패: " + e.getMessage());
            Toast.makeText(getContext(), "지도 초기화에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void setupClickListeners() {
        // 뒤로가기 버튼 클릭 이벤트
        toolbar.setNavigationOnClickListener(v -> {
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).onBackPressed();
            }
        });
        
        btnRefresh.setOnClickListener(v -> {
            if (checkLocationPermission()) {
                // 새로고침 시작
                
                // 리스트뷰 초기화
                careCenters.clear();
                careCenterAdapter.updateCareCenters(careCenters);
                
                // 기존 마커들 제거
                clearMarkers();
                
                // 캐시 무효화
                hasSearched = false;
                isMapInitialized = false; // 새로고침 시 지도 초기화 플래그 리셋
                cachedCareCenters.clear();
                cachedCurrentLocation = null;
                
                // 새로 검색 시작
                getCurrentLocationAndSearch();
            } else {
            requestLocationPermission();
            }
        });
        
        btnRegionSearch.setOnClickListener(v -> {
            // 지역 선택 다이얼로그 표시
            showRegionSelectionDialog();
        });
        
        btnSearchHere.setOnClickListener(v -> {
            // 현재 화면 중심으로 검색
            searchAtMapCenter();
        });
        
        btnSettings.setOnClickListener(v -> {
            // 검색 설정 다이얼로그 표시
            showSearchSettingsDialog();
        });
    }
    
    private void startAutoLocationAndSearch() {
        // 위치 권한 확인
        if (checkLocationPermission()) {
            // 자동으로 현재 위치 확인 및 요양원 검색
            getCurrentLocationAndSearch();
        } else {
            // 권한 요청
            requestLocationPermission();
        }
    }
    
    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        try {
        googleMap = map;
        
        // 지도 설정
            if (checkLocationPermission()) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        
        // 마커 클릭 리스너
        googleMap.setOnMarkerClickListener(marker -> {
                try {
                    Object tag = marker.getTag();
                    
                    // 현재 위치 마커인 경우 아무 동작도 하지 않음
                    if (tag == null || "current_location".equals(tag)) {
                        return true;
                    }
                    
                    // 요양원 마커인 경우에만 상세 정보 표시
                    if (tag instanceof CareCenter) {
                        CareCenter careCenter = (CareCenter) tag;
                        // 마커를 선택된 상태로 변경
                        updateMarkerSelection(marker, true);
                        showCareCenterInfo(careCenter);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "마커 클릭 처리 실패: " + e.getMessage());
                }
            return true;
        });
        
        // 지도 카메라 이동 리스너 (사용자가 지도를 드래그할 때)
        googleMap.setOnCameraIdleListener(() -> {
            // 지도가 이동했을 때 "현재 화면에서 검색" 버튼 표시
            if (btnSearchHere != null && hasSearched) {
                btnSearchHere.setVisibility(View.VISIBLE);
            }
        });
        
        
            // 캐시된 데이터가 있으면 복원, 없으면 초기 위치 설정
            if (hasSearched && !cachedCareCenters.isEmpty()) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSearchTime < SEARCH_CACHE_DURATION) {
                    // 캐시된 결과 복원
                    restoreCachedResults();
                    Log.d(TAG, "지도 초기화 후 캐시된 결과 복원 완료");
                } else {
                    // 캐시 만료 - 초기 위치 설정
                    LatLng seoulCityHall = new LatLng(37.5665, 126.9780);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulCityHall, 12f));
                    Log.d(TAG, "캐시 만료로 초기 위치 설정");
                }
            } else {
                // 초기 위치 설정 (서울 시청)
        LatLng seoulCityHall = new LatLng(37.5665, 126.9780);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoulCityHall, 12f));
                Log.d(TAG, "캐시된 데이터 없음 - 초기 위치 설정");
            }
            
            Log.d(TAG, "지도가 성공적으로 초기화되었습니다");
        } catch (Exception e) {
            Log.e(TAG, "지도 초기화 실패: " + e.getMessage());
            Toast.makeText(getContext(), "지도 초기화에 실패했습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    private boolean checkLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            
            new AlertDialog.Builder(requireContext())
                    .setTitle("위치 권한 필요")
                    .setMessage("주변 요양원을 찾기 위해 위치 권한이 필요합니다.")
                    .setPositiveButton("권한 허용", (dialog, which) -> {
                        ActivityCompat.requestPermissions(requireActivity(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                    })
                    .setNegativeButton("취소", (dialog, which) -> {
                        // 기본 위치(서울시청)로 검색
                        searchNursingHomesWithDefaultLocation();
                    })
                    .show();
        } else {
            getCurrentLocation();
        }
    }
    
    @SuppressLint("MissingPermission")
    private void getCurrentLocationAndSearch() {
        try {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "위치 권한이 필요합니다", Toast.LENGTH_SHORT).show();
                return;
            }

            if (fusedLocationClient == null) {
                Log.e(TAG, "위치 서비스가 초기화되지 않았습니다");
                Toast.makeText(getContext(), "위치 서비스를 초기화할 수 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            // 로딩 메시지 표시
            Toast.makeText(getContext(), "현재 위치를 확인하고 주변 요양원을 검색합니다...", Toast.LENGTH_SHORT).show();

            fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(requireActivity(), location -> {
                        try {
                            if (location != null) {
                                currentLocation = location;
                                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                
                                if (googleMap != null) {
                                    // 지도 이동
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                                    
                                    // 현재 위치 마커 추가 (커스텀 파란색 마커)
                                    Marker currentMarker = googleMap.addMarker(new MarkerOptions()
                                            .position(currentLatLng)
                                            .title("현재 위치")
                                            .icon(markerLabelGenerator.createCurrentLocationMarker()));
                                    if (currentMarker != null) {
                                        currentMarker.setTag("current_location");
                                        markers.add(currentMarker);
                                    }
                                }
                                
                                // 위치 확인 후 자동으로 주변 요양원 검색
                                searchNursingHomesAfterLocation();
                                
                            } else {
                                Toast.makeText(getContext(), "위치를 가져올 수 없습니다", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "위치 처리 실패: " + e.getMessage());
                            Toast.makeText(getContext(), "위치 정보 처리에 실패했습니다", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "위치 가져오기 실패: " + e.getMessage());
                        Toast.makeText(getContext(), "위치를 가져올 수 없습니다", Toast.LENGTH_SHORT).show();
                    });
        } catch (Exception e) {
            Log.e(TAG, "getCurrentLocationAndSearch 실패: " + e.getMessage());
            Toast.makeText(getContext(), "위치 서비스에 접근할 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        // 현재 위치만 가져오고 로딩은 표시하지 않음 (위치 마커 표시 후 로딩 시작)
        
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        currentLocation = location;
                        Log.d(TAG, "현재 위치: " + location.getLatitude() + ", " + location.getLongitude());
                        
                        // 현재 위치 마커를 지도에 표시
                        if (googleMap != null) {
                            LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                            
                            // 현재 위치 마커 추가
                            Marker currentMarker = googleMap.addMarker(new MarkerOptions()
                                    .position(currentLatLng)
                                    .title("현재 위치")
                                    .icon(markerLabelGenerator.createCurrentLocationMarker()));
                            if (currentMarker != null) {
                                currentMarker.setTag("current_location");
                                markers.add(currentMarker);
                            }
                        }
                        
                        // 현재 위치 마커 표시 후 요양원 검색 시작
                        searchNursingHomesAfterLocation();
                    } else {
                        Log.w(TAG, "위치를 가져올 수 없습니다. 기본 위치 사용");
                        searchNursingHomesWithDefaultLocation();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "위치 가져오기 실패: " + e.getMessage());
                    searchNursingHomesWithDefaultLocation();
                });
    }
    
    private void searchNursingHomesWithDefaultLocation() {
        // 기본 위치: 서울시청
        String defaultLocation = "37.5665,126.9780";
        
        // 기본 위치로 현재 위치 설정
        currentLocation = new android.location.Location("default");
        currentLocation.setLatitude(37.5665);
        currentLocation.setLongitude(126.9780);
        
        // 기본 위치 마커를 지도에 표시
        if (googleMap != null) {
            LatLng defaultLatLng = new LatLng(37.5665, 126.9780);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, 12f));
            
            // 기본 위치 마커 추가 (커스텀 파란색 마커)
            Marker defaultMarker = googleMap.addMarker(new MarkerOptions()
                    .position(defaultLatLng)
                    .title("기본 위치")
                    .icon(markerLabelGenerator.createCurrentLocationMarker()));
            if (defaultMarker != null) {
                defaultMarker.setTag("current_location");
                markers.add(defaultMarker);
            }
        }
        
        // 기본 위치 마커 표시 후 요양원 검색 시작
        searchNursingHomesAfterLocation();
    }
    
    private void searchNursingHomes() {
        if (currentLocation != null) {
            String locationString = currentLocation.getLatitude() + "," + currentLocation.getLongitude();
            searchNursingHomes(locationString, searchRadius);
        } else {
            searchNursingHomesWithDefaultLocation();
        }
    }
    
    /**
     * 현재 위치 마커가 표시된 후 요양원 검색을 시작
     */
    private void searchNursingHomesAfterLocation() {
        // 현재 위치 마커가 표시된 후 잠시 대기 후 요양원 검색 시작
        requireActivity().runOnUiThread(() -> {
            // 로딩 상태 표시
            showLoading(true);
            
            // 500ms 후에 요양원 검색 시작 (현재 위치 마커가 완전히 표시된 후)
            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                searchNursingHomes();
            }, 500);
        });
    }
    
    private void searchNursingHomes(String location, int radius) {
        showLoading(true);
        
        // 검색 시작 시 카운터 초기화
        updateResultCount(0);
        
        publicDataApiService.searchNursingHomes(location, radius, new PublicDataApiService.NursingHomeSearchCallback() {
            @Override
            public void onSuccess(List<CareCenter> nursingHomes) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    
                    if (nursingHomes.isEmpty()) {
                        Toast.makeText(requireContext(), "근처에 요양기관을 찾을 수 없습니다", Toast.LENGTH_LONG).show();
                        return;
                    }
                    
                    // 결과 저장 및 캐시 업데이트
                    careCenters = nursingHomes;
                    cachedCareCenters.clear();
                    cachedCareCenters.addAll(nursingHomes);
                    cachedCurrentLocation = currentLocation;
                    hasSearched = true;
                    lastSearchTime = System.currentTimeMillis();
                    
                    // UI 업데이트
                    careCenterAdapter.updateCareCenters(careCenters);
                    updateMapMarkers();
                    
                    // 결과 개수 업데이트
                    updateResultCount(careCenters.size());
                    
                    // Bottom Sheet 확장
                    if (bottomSheetBehavior != null) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    }
                    
                    // 더미 요양원 추가 (검색 완료 후)
                    addDummyCareCenter();
                    
                    // 캐시에 더미 요양원도 포함 (다른 화면에서 돌아올 때 유지되도록)
                    if (!careCenters.isEmpty() && careCenters.get(0).getInstitutionId() != null && 
                        careCenters.get(0).getInstitutionId().equals(1L) && 
                        "요양원 A".equals(careCenters.get(0).getName())) {
                        // 더미 요양원이 추가되었으면 캐시에도 추가
                        cachedCareCenters.add(0, careCenters.get(0));
                    }
                    
                    Log.d(TAG, "요양기관 검색 완료: " + nursingHomes.size() + "개");
                });
            }
            
            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(requireContext(), "요양기관 검색에 실패했습니다", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "검색 실패: " + errorMessage);
                });
            }
        });
    }
    

    private void updateMapMarkers() {
        if (googleMap == null) return;
        
        // 기존 마커 제거
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
        
        // 현재 위치 마커 추가 (커스텀 파란색 마커)
        if (currentLocation != null) {
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            Marker currentMarker = googleMap.addMarker(new MarkerOptions()
                    .position(currentLatLng)
                    .title("현재 위치")
                    .icon(markerLabelGenerator.createCurrentLocationMarker()));
            if (currentMarker != null) {
                currentMarker.setTag("current_location");
                markers.add(currentMarker);
            }
        }
        
        // 새로운 요양원 마커 추가 (Google Maps 기본 빨간색 마커)
        for (CareCenter careCenter : careCenters) {
            if (careCenter.getLatLng() != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(careCenter.getLatLng())
                        .title(careCenter.getName())
                        .snippet(careCenter.getFormattedDistance())
                        .icon(markerLabelGenerator.createMarkerWithLabel(careCenter.getName())));
                
                if (marker != null) {
                    marker.setTag(careCenter);
                    markers.add(marker);
                }
            }
        }
        
        // 모든 마커가 보이도록 카메라 조정
        if (!markers.isEmpty() || currentLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            
            // 현재 위치 추가
            if (currentLocation != null) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                builder.include(currentLatLng);
            }
            
            // 모든 요양원 마커 추가
            for (Marker marker : markers) {
                builder.include(marker.getPosition());
            }
            
            LatLngBounds bounds = builder.build();
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }
    
    // 캐시된 검색 결과 복원
    private void restoreCachedResults() {
        try {
            // 기존 마커들 제거
            clearMarkers();
            
            // 현재 위치 마커 추가 (캐시된 위치 정보 사용, Google Maps 기본 파란색 마커)
            if (cachedCurrentLocation != null) {
                LatLng currentLatLng = new LatLng(cachedCurrentLocation.getLatitude(), cachedCurrentLocation.getLongitude());
                
                // 현재 위치 마커 추가 (커스텀 파란색 마커)
                Marker currentMarker = googleMap.addMarker(new MarkerOptions()
                        .position(currentLatLng)
                        .title("현재 위치")
                        .icon(markerLabelGenerator.createCurrentLocationMarker()));
                if (currentMarker != null) {
                    currentMarker.setTag("current_location");
                    markers.add(currentMarker);
                }
                
                // 지도가 이미 초기화된 경우에는 위치 변경하지 않음 (UX 개선)
                if (!isMapInitialized) {
                    // 지도를 현재 위치로 중앙 정렬 (화면 정 중앙에 위치하도록)
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f));
                    Log.d(TAG, "캐시된 현재 위치로 지도 중앙 정렬: " + currentLatLng.latitude + ", " + currentLatLng.longitude);
                } else {
                    Log.d(TAG, "지도가 이미 초기화됨 - 위치 변경하지 않음");
                }
            }
            
            // 캐시된 요양원들을 지도에 다시 표시
            for (CareCenter careCenter : cachedCareCenters) {
                addCareCenterMarker(careCenter);
            }
            
            // RecyclerView 업데이트
            careCenterAdapter.updateCareCenters(cachedCareCenters);
            
            // 결과 개수 업데이트
            updateResultCount(cachedCareCenters.size());
            
            // 더미 요양원 추가 (캐시 복원 후)
            addDummyCareCenter();
            
            // 지도가 이미 초기화된 경우에는 줌 조정도 하지 않음
            if (!isMapInitialized) {
                // 모든 마커를 포함하도록 지도 줌 조정 (현재 위치 중앙 정렬 후 약간의 지연)
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    adjustMapZoomToFitMarkers(cachedCareCenters);
                }, 1000); // 1초 후 줌아웃 실행
            }
            
            // 지도 초기화 완료 표시
            isMapInitialized = true;
            
            Log.d(TAG, "캐시된 검색 결과 복원 완료 - 요양원 수: " + cachedCareCenters.size());
            
        } catch (Exception e) {
            Log.e(TAG, "캐시된 결과 복원 실패: " + e.getMessage());
        }
    }
    
    private void addCareCenterMarker(CareCenter careCenter) {
        try {
            if (googleMap != null && careCenter.getLatLng() != null) {
                Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(careCenter.getLatLng())
                        .title(careCenter.getName())
                        .snippet(careCenter.getAddress())
                        .icon(markerLabelGenerator.createMarkerWithLabel(careCenter.getName())));
                
                if (marker != null) {
                    marker.setTag(careCenter);
                    markers.add(marker);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "요양기관 마커 추가 실패: " + e.getMessage());
        }
    }
    
    // 마커들의 위치에 따라 지도 줌 레벨 자동 조정
    private void adjustMapZoomToFitMarkers(List<CareCenter> careCenters) {
        if (googleMap == null || careCenters == null || careCenters.size() == 0) {
            return;
        }
        
        try {
            // 모든 마커와 현재 위치를 포함하는 영역 계산
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            
            // 현재 위치 추가
            if (currentLocation != null) {
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                boundsBuilder.include(currentLatLng);
            }
            
            // 모든 요양원 마커 추가
            for (CareCenter careCenter : careCenters) {
                if (careCenter.getLatLng() != null) {
                    boundsBuilder.include(careCenter.getLatLng());
                }
            }
            
            // 경계 영역 생성
            LatLngBounds bounds = boundsBuilder.build();
            
            // 패딩을 추가하여 모든 마커가 잘 보이도록 조정
            int padding = 100; // 픽셀 단위 패딩
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            
            // 지도 애니메이션으로 이동
            googleMap.animateCamera(cameraUpdate);
            
            Log.d(TAG, "지도 줌 자동 조정 완료 - 요양원 수: " + careCenters.size() + ", 검색 반경: " + searchRadius + "m");
            
        } catch (Exception e) {
            Log.e(TAG, "지도 줌 조정 실패: " + e.getMessage());
            // 실패 시 기본 줌 레벨로 설정
            if (currentLocation != null) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 13f));
            }
        }
    }
    
    private void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }
    
    /**
     * 마커의 선택 상태를 업데이트 (단순한 마커에서는 선택 상태 변경 없음)
     */
    private void updateMarkerSelection(Marker selectedMarker, boolean isSelected) {
        // 단순한 마커에서는 선택 상태 변경을 하지 않음
        // 마커 클릭 시 상세 정보만 표시
    }
    
    private void showCareCenterInfo(CareCenter careCenter) {
        // 마커 클릭 시에도 동일한 상세 정보 다이얼로그 표시
        showCareCenterDetailDialog(careCenter);
    }
    
    // 요양원 상세 정보 다이얼로그 표시
    private void showCareCenterDetailDialog(CareCenter careCenter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(careCenter.getName())
                .setMessage(String.format("주소: %s\n전화번호: %s\n거리: %s", 
                        careCenter.getAddress(), careCenter.getPhone(), careCenter.getFormattedDistance()))
                .setPositiveButton("상세정보 보기", (dialog, which) -> {
                    // CareCenterInfoFragment로 이동
                    navigateToCareCenterInfo(careCenter);
                })
                .setNegativeButton("닫기", null)
                .show();
    }
    
    // CareCenterInfoFragment로 이동
    private void navigateToCareCenterInfo(CareCenter careCenter) {
        try {
            // Fragment 생성 및 데이터 전달
            CareCenterInfoFragment fragment = new CareCenterInfoFragment();
            Bundle args = new Bundle();
            args.putLong("institution_id", careCenter.getInstitutionId());
            args.putString("care_center_name", careCenter.getName());
            args.putString("care_center_address", careCenter.getAddress());
            args.putString("care_center_phone", careCenter.getPhone());
            Log.d(TAG, "전화번호 전달: " + careCenter.getPhone());
            args.putDouble("care_center_distance", careCenter.getDistanceKm() != null ? careCenter.getDistanceKm() : 0.0);
            args.putDouble("care_center_lat", careCenter.getLatLng() != null ? careCenter.getLatLng().latitude : 0.0);
            args.putDouble("care_center_lng", careCenter.getLatLng() != null ? careCenter.getLatLng().longitude : 0.0);
            args.putString("care_center_photo_url", careCenter.getPhotoUrl());
            args.putDouble("care_center_rating", careCenter.getRating() != null ? careCenter.getRating() : 0.0);
            args.putInt("care_center_review_count", careCenter.getUserRatingsTotal() != null ? careCenter.getUserRatingsTotal() : 0);
            args.putString("care_center_description", careCenter.getDescription());
            fragment.setArguments(args);
            
            // DashboardActivity를 통해 Fragment 전환
            if (getActivity() instanceof DashboardActivity) {
                ((DashboardActivity) getActivity()).navigateToFragment(fragment);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "CareCenterInfoFragment 이동 실패: " + e.getMessage());
            Toast.makeText(getContext(), "상세 정보를 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
        }
    }
    
    @Override
    public void onCareCenterClicked(CareCenter careCenter) {
        // 요양원 클릭 시 지도에서 해당 위치로 이동
        if (googleMap != null && careCenter.getLatLng() != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(careCenter.getLatLng(), 15f));
        }
        
        // 상세 정보 화면으로 이동 - navigateToCareCenterInfo와 동일한 데이터 전달
        navigateToCareCenterInfo(careCenter);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationAndSearch();
            } else {
                searchNursingHomesWithDefaultLocation();
            }
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Fragment가 다시 활성화될 때만 캐시 복원 (지도가 이미 준비된 경우)
        if (googleMap != null && hasSearched && !cachedCareCenters.isEmpty()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastSearchTime < SEARCH_CACHE_DURATION) {
                // 지도가 이미 초기화된 경우에는 캐시 복원하지 않음 (UX 개선)
                if (!isMapInitialized) {
                    // 캐시가 유효한 경우 기존 결과 복원
                    restoreCachedResults();
                    Log.d(TAG, "onResume에서 캐시된 결과 복원 완료");
                } else {
                    Log.d(TAG, "지도가 이미 초기화됨 - onResume에서 캐시 복원 생략");
                }
            } else {
                // 캐시가 만료된 경우 다시 검색
                Log.d(TAG, "캐시 만료로 onResume에서 재검색");
                hasSearched = false;
                isMapInitialized = false; // 재검색 시 지도 초기화 플래그 리셋
                if (currentLocation != null) {
                    searchNursingHomes();
                }
            }
        } else {
            // 캐시된 데이터가 없거나 지도가 준비되지 않은 경우 더미 요양원만 추가
            Log.d(TAG, "캐시된 데이터 없음 또는 지도 미준비 - 더미 요양원만 추가");
            if (googleMap != null) {
                addDummyCareCenter();
            }
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (publicDataApiService != null) {
            publicDataApiService.shutdown();
        }
    }
    
    // 상태바 높이 가져오기
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
    
    private void showLoading(boolean show) {
        if (layoutLoading != null) {
            layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        
        // 로딩 중일 때는 RecyclerView 숨기기
        if (recyclerViewCareCenters != null) {
            recyclerViewCareCenters.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    
    private void updateResultCount(int count) {
        if (tvResultCount != null) {
            tvResultCount.setText(count + "개");
        }
    }
    
    /**
     * 검색 설정 다이얼로그 표시
     */
    private void showSearchSettingsDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_search_settings, null);
        RadioGroup radioGroup = dialogView.findViewById(R.id.radio_group_radius);
        RadioButton radio3km = dialogView.findViewById(R.id.radio_3km);
        RadioButton radio5km = dialogView.findViewById(R.id.radio_5km);
        RadioButton radio10km = dialogView.findViewById(R.id.radio_10km);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnApply = dialogView.findViewById(R.id.btn_apply);
        
        // 현재 설정된 반경에 따라 라디오 버튼 체크
        if (searchRadius == RADIUS_3KM) {
            radio3km.setChecked(true);
        } else if (searchRadius == RADIUS_5KM) {
            radio5km.setChecked(true);
        } else if (searchRadius == RADIUS_10KM) {
            radio10km.setChecked(true);
        }
        
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        
        // 적용 버튼
        btnApply.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            
            int oldRadius = searchRadius;
            if (selectedId == R.id.radio_3km) {
                searchRadius = RADIUS_3KM;
            } else if (selectedId == R.id.radio_5km) {
                searchRadius = RADIUS_5KM;
            } else if (selectedId == R.id.radio_10km) {
                searchRadius = RADIUS_10KM;
            }
            
            // 반경이 변경된 경우 재검색
            if (oldRadius != searchRadius) {
                dialog.dismiss();
                Toast.makeText(requireContext(), 
                    "검색 반경이 " + (searchRadius / 1000) + "km로 변경되었습니다", 
                    Toast.LENGTH_SHORT).show();
                
                // 현재 위치 기준으로 재검색
                if (currentLocation != null) {
                    // 리스트뷰 초기화
                    careCenters.clear();
                    careCenterAdapter.updateCareCenters(careCenters);
                    
                    // 기존 요양원 마커들만 제거 (현재 위치 마커는 유지)
                    clearCareCenterMarkers();
                    
                    // 재검색
                    searchNursingHomes();
                }
            } else {
                dialog.dismiss();
            }
        });
        
        // 취소 버튼
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * 요양원 마커만 제거 (현재 위치 마커는 유지)
     */
    private void clearCareCenterMarkers() {
        // markers 리스트를 순회하면서 요양원 마커만 제거
        List<Marker> markersToRemove = new ArrayList<>();
        for (Marker marker : markers) {
            Object tag = marker.getTag();
            if (tag instanceof CareCenter) {
                marker.remove();
                markersToRemove.add(marker);
            }
        }
        markers.removeAll(markersToRemove);
    }
    
    /**
     * 지역 선택 다이얼로그 표시 (1단계: 시/도 선택) - 2xn 그리드 형식
     */
    private void showRegionSelectionDialog() {
        final String[] cities = RegionData.getCities();
        List<String> cityList = new ArrayList<>();
        for (String city : cities) {
            cityList.add(city);
        }
        
        // 커스텀 다이얼로그 뷰 생성
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_region_selection, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_regions);
        Button btnBack = dialogView.findViewById(R.id.btn_back);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        
        tvTitle.setText("시/도를 선택하세요");
        btnBack.setVisibility(View.GONE); // 1단계에서는 "이전" 버튼 숨김
        
        // 다이얼로그 생성 (먼저 선언)
        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        
        // 2열 그리드 레이아웃 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        
        // 어댑터 설정
        RegionAdapter adapter = new RegionAdapter(cityList, (region, position) -> {
            // 2단계: 군/구 선택 다이얼로그 표시
            dialog.dismiss();
            showDistrictSelectionDialog(region);
        });
        recyclerView.setAdapter(adapter);
        
        // 취소 버튼 클릭 리스너
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * 군/구 선택 다이얼로그 표시 (2단계) - 2xn 그리드 형식
     */
    private void showDistrictSelectionDialog(String city) {
        Map<String, List<RegionData.Region>> districtsMap = RegionData.getDistrictsMap();
        List<RegionData.Region> districts = districtsMap.get(city);
        
        if (districts == null || districts.isEmpty()) {
            Toast.makeText(requireContext(), "해당 지역의 데이터가 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 지역명만 추출
        List<String> districtNames = new ArrayList<>();
        for (RegionData.Region district : districts) {
            districtNames.add(district.name);
        }
        
        // 커스텀 다이얼로그 뷰 생성
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_region_selection, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_regions);
        Button btnBack = dialogView.findViewById(R.id.btn_back);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        
        tvTitle.setText(city + " - 군/구를 선택하세요");
        btnBack.setVisibility(View.VISIBLE); // 2단계에서는 "이전" 버튼 표시
        
        // 다이얼로그 생성 (먼저 선언)
        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        
        // 2열 그리드 레이아웃 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        
        // 어댑터 설정
        RegionAdapter adapter = new RegionAdapter(districtNames, (districtName, position) -> {
            RegionData.Region selectedDistrict = districts.get(position);
            
            dialog.dismiss();
            
            // 하위 읍/면/동이 있는 경우 3단계로 이동
            if (selectedDistrict.hasSubRegions) {
                showSubDistrictSelectionDialog(city, selectedDistrict.name, selectedDistrict.location);
            } else {
                // 하위 지역이 없으면 바로 검색
                String fullRegionName = city + " " + selectedDistrict.name;
                searchByRegion(fullRegionName, 
                              selectedDistrict.location.latitude, 
                              selectedDistrict.location.longitude);
            }
        });
        recyclerView.setAdapter(adapter);
        
        // "이전" 버튼 클릭 리스너
        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
            // 다시 시/도 선택으로 돌아가기
            showRegionSelectionDialog();
        });
        
        // "취소" 버튼 클릭 리스너
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * 읍/면/동 선택 다이얼로그 표시 (3단계) - 2xn 그리드 형식
     */
    private void showSubDistrictSelectionDialog(String city, String district, LatLng districtLocation) {
        Map<String, List<RegionData.Region>> subDistrictsMap = RegionData.getSubDistrictsMap();
        String key = city + " " + district;
        List<RegionData.Region> subDistricts = subDistrictsMap.get(key);
        
        if (subDistricts == null || subDistricts.isEmpty()) {
            // 읍/면/동 데이터가 없으면 군/구 위치로 바로 검색
            Toast.makeText(requireContext(), "상세 지역 데이터가 없어 " + district + " 전체를 검색합니다", Toast.LENGTH_SHORT).show();
            searchByRegion(key, districtLocation.latitude, districtLocation.longitude);
            return;
        }
        
        // 지역명만 추출
        List<String> subDistrictNames = new ArrayList<>();
        for (RegionData.Region subDistrict : subDistricts) {
            subDistrictNames.add(subDistrict.name);
        }
        
        // 커스텀 다이얼로그 뷰 생성
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_region_selection, null);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recycler_view_regions);
        Button btnBack = dialogView.findViewById(R.id.btn_back);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        
        tvTitle.setText(city + " " + district + " - 읍/면/동을 선택하세요");
        btnBack.setVisibility(View.VISIBLE); // 3단계에서도 "이전" 버튼 표시
        
        // 다이얼로그 생성 (먼저 선언)
        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();
        
        // 2열 그리드 레이아웃 설정
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        
        // 어댑터 설정
        RegionAdapter adapter = new RegionAdapter(subDistrictNames, (subDistrictName, position) -> {
            RegionData.Region selectedSubDistrict = subDistricts.get(position);
            String fullRegionName = city + " " + district + " " + selectedSubDistrict.name;
            
            // 선택한 읍/면/동으로 검색
            dialog.dismiss();
            searchByRegion(fullRegionName, 
                          selectedSubDistrict.location.latitude, 
                          selectedSubDistrict.location.longitude);
        });
        recyclerView.setAdapter(adapter);
        
        // "이전" 버튼 클릭 리스너 (다시 군/구 선택으로 돌아가기)
        btnBack.setOnClickListener(v -> {
            dialog.dismiss();
            showDistrictSelectionDialog(city);
        });
        
        // "취소" 버튼 클릭 리스너
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        dialog.show();
    }
    
    /**
     * 현재 화면 중심으로 요양원 검색
     */
    private void searchAtMapCenter() {
        if (googleMap == null) {
            Toast.makeText(requireContext(), "지도가 초기화되지 않았습니다", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 현재 화면 중심 좌표 가져오기
        LatLng mapCenter = googleMap.getCameraPosition().target;
        
        // 리스트뷰 초기화
        careCenters.clear();
        careCenterAdapter.updateCareCenters(careCenters);
        
        // 기존 마커들 제거
        clearMarkers();
        
        // 화면 중심을 현재 위치로 설정
        currentLocation = new Location("map_center");
        currentLocation.setLatitude(mapCenter.latitude);
        currentLocation.setLongitude(mapCenter.longitude);
        
        // 화면 중심에 마커 추가
        Marker centerMarker = googleMap.addMarker(new MarkerOptions()
                .position(mapCenter)
                .title("검색 중심")
                .icon(markerLabelGenerator.createCurrentLocationMarker()));
        if (centerMarker != null) {
            centerMarker.setTag("current_location");
            markers.add(centerMarker);
        }
        
        // "현재 화면에서 검색" 버튼 숨기기
        btnSearchHere.setVisibility(View.GONE);
        
        // 화면 중심 기준으로 요양원 검색
        Toast.makeText(requireContext(), "이 위치에서 요양원을 검색합니다...", Toast.LENGTH_SHORT).show();
        searchNursingHomesAfterLocation();
        
        // 지도 초기화 플래그 리셋
        isMapInitialized = false;
        
        Log.d(TAG, "화면 중심 검색: " + mapCenter.latitude + ", " + mapCenter.longitude);
    }
    
    /**
     * 선택한 지역 기준으로 요양원 검색
     */
    private void searchByRegion(String regionName, double latitude, double longitude) {
        // 리스트뷰 초기화
        careCenters.clear();
        careCenterAdapter.updateCareCenters(careCenters);
        
        // 기존 마커들 제거
        clearMarkers();
        
        // 선택한 지역을 현재 위치로 설정
        currentLocation = new Location("selected_region");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
        
        // 지도를 선택한 지역으로 이동 및 마커 표시
        if (googleMap != null) {
            LatLng selectedLatLng = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 12f));
            
            // 선택한 지역 마커 추가
            Marker regionMarker = googleMap.addMarker(new MarkerOptions()
                    .position(selectedLatLng)
                    .title(regionName)
                    .icon(markerLabelGenerator.createCurrentLocationMarker()));
            if (regionMarker != null) {
                regionMarker.setTag("current_location");
                markers.add(regionMarker);
            }
        }
        
        // 선택한 지역 기준으로 요양원 검색
        Toast.makeText(requireContext(), regionName + " 지역의 요양원을 검색합니다...", Toast.LENGTH_SHORT).show();
        searchNursingHomesAfterLocation();
        
        // 지도 초기화 플래그 리셋 (새로운 지역 검색)
        isMapInitialized = false;
        
        Log.d(TAG, "지역 선택 검색: " + regionName + " (" + latitude + ", " + longitude + ")");
    }
}