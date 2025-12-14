package com.example.coderelief.services;

import android.content.Context;
import android.util.Log;
import com.example.coderelief.R;
import com.example.coderelief.models.CareCenter;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CopyOnWriteArrayList;

public class PublicDataApiService {
    private static final String TAG = "PublicDataApiService";
    private final Context context;
    private final ExecutorService executor;

    public PublicDataApiService(Context context) {
        this.context = context;
        this.executor = Executors.newFixedThreadPool(5); // 키워드 그룹 수와 동일하게 설정
    }

    public interface NursingHomeSearchCallback {
        void onSuccess(List<CareCenter> nursingHomes);
        void onError(String errorMessage);
    }

    public void searchNursingHomes(String location, int radius, NursingHomeSearchCallback callback) {
        executor.execute(() -> {
            try {
                Log.d(TAG, "Google Places API로 요양기관 검색 시작");
                
                // 1단계: 현재 위치 파싱
                LatLng currentLocation = parseLocation(location);
                if (currentLocation == null) {
                    callback.onError("위치 정보를 파싱할 수 없습니다");
                    return;
                }
                
                Log.d(TAG, "현재 위치: " + currentLocation.latitude + ", " + currentLocation.longitude + ", 반경: " + radius + "m");
                
                // 2단계: Google Places API로 근처 요양기관 검색
                List<CareCenter> nursingHomes = searchNearbyNursingHomes(currentLocation, radius);
                
                // 3단계: 거리 필터링 (반경 내의 요양원만)
                List<CareCenter> filteredHomes = filterByDistance(nursingHomes, radius);
                Log.d(TAG, "거리 필터링 후: " + filteredHomes.size() + "개 (전체: " + nursingHomes.size() + "개)");
                
                // 4단계: 거리순으로 정렬
                List<CareCenter> sortedHomes = sortByDistance(filteredHomes, currentLocation);
                
                Log.d(TAG, "검색 완료 - 요양기관 수: " + sortedHomes.size());
                callback.onSuccess(sortedHomes);
                
            } catch (Exception e) {
                Log.e(TAG, "요양기관 검색 실패: " + e.getMessage());
                callback.onError("요양기관 검색에 실패했습니다: " + e.getMessage());
            }
        });
    }

    // 1단계: 위치 문자열을 LatLng로 파싱
    private LatLng parseLocation(String location) {
        try {
            String[] coords = location.split(",");
            if (coords.length == 2) {
                double lat = Double.parseDouble(coords[0].trim());
                double lng = Double.parseDouble(coords[1].trim());
                return new LatLng(lat, lng);
            }
        } catch (Exception e) {
            Log.e(TAG, "위치 파싱 실패: " + e.getMessage());
        }
        return null;
    }

    // 2단계: Google Places API로 근처 요양기관 검색 (병렬 처리)
    private List<CareCenter> searchNearbyNursingHomes(LatLng location, int radius) {
        // 스레드 안전한 리스트 사용
        CopyOnWriteArrayList<CareCenter> allNursingHomes = new CopyOnWriteArrayList<>();
        
        try {
            // 최적화된 키워드 그룹으로 검색 (중복 제거 및 영문 키워드 추가)
            String[][] keywordGroups = {
                // 핵심 요양시설 키워드
                {"요양원", "요양병원", "요양시설"},
                // 노인복지 통합 (중복 제거)
                {"노인복지", "실버타운", "양로원"},
                // 케어센터 관련
                {"주간보호센터", "데이케어센터"},
                // 전문 시설
                {"요양전문", "장기요양", "노인전문병원"},
                // 영문 표기 시설
                {"너싱", "실버홈", "시니어"}
            };
            
            // CountDownLatch로 모든 검색이 완료될 때까지 대기
            CountDownLatch latch = new CountDownLatch(keywordGroups.length);
            
            Log.d(TAG, "=== 병렬 검색 시작 (총 " + keywordGroups.length + "개 그룹) ===");
            long startTime = System.currentTimeMillis();
            
            // 각 키워드 그룹을 병렬로 검색
            for (String[] keywordGroup : keywordGroups) {
                String primaryKeyword = keywordGroup[0];
                
                executor.execute(() -> {
                    try {
                        long keywordStartTime = System.currentTimeMillis();
                        List<CareCenter> keywordHomes = searchNursingHomesByKeyword(location, radius, primaryKeyword);
                        allNursingHomes.addAll(keywordHomes);
                        
                        long keywordDuration = System.currentTimeMillis() - keywordStartTime;
                        Log.d(TAG, "✓ 키워드 '" + primaryKeyword + "' 검색 완료: " + keywordHomes.size() + "개 (" + keywordDuration + "ms)");
                    } catch (Exception e) {
                        Log.e(TAG, "✗ 키워드 '" + primaryKeyword + "' 검색 실패: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            // 모든 검색이 완료될 때까지 대기 (최대 30초)
            latch.await(30, java.util.concurrent.TimeUnit.SECONDS);
            
            long totalDuration = System.currentTimeMillis() - startTime;
            Log.d(TAG, "=== 병렬 검색 완료 (총 소요시간: " + totalDuration + "ms) ===");
            Log.d(TAG, "총 검색 결과: " + allNursingHomes.size() + "개 (중복 포함)");
            
            // 중복 제거
            List<CareCenter> uniqueHomes = removeDuplicateNursingHomes(new ArrayList<>(allNursingHomes));
            Log.d(TAG, "중복 제거 후 요양기관 수: " + uniqueHomes.size());
            
            return uniqueHomes;
            
        } catch (Exception e) {
            Log.e(TAG, "요양기관 검색 중 오류: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // 키워드별 요양기관 검색
    private List<CareCenter> searchNursingHomesByKeyword(LatLng location, int radius, String keyword) {
        List<CareCenter> nursingHomes = new ArrayList<>();
        
        try {
            String apiKey = context.getString(R.string.google_maps_key);
            String urlString = String.format(
                "https://maps.googleapis.com/maps/api/place/textsearch/json" +
                "?query=%s" +
                "&location=%f,%f" +
                "&radius=%d" +
                "&language=ko" +
                "&key=%s",
                keyword, location.latitude, location.longitude, radius, apiKey
            );
            
            Log.d(TAG, "API 호출 URL: " + urlString);
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                // JSON 파싱
                JSONObject jsonResponse = new JSONObject(response.toString());
                String status = jsonResponse.getString("status");
                
                if ("OK".equals(status)) {
                    JSONArray results = jsonResponse.getJSONArray("results");
                    
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject place = results.getJSONObject(i);
                        
                        // 요양기관 필터링 (키워드 기반)
                        if (isNursingHome(place, keyword)) {
                            // Place Details API로 상세 정보 가져오기
                            String placeId = place.optString("place_id", "");
                            if (!placeId.isEmpty()) {
                                JSONObject placeDetails = getPlaceDetails(placeId);
                                if (placeDetails != null) {
                                    CareCenter nursingHome = parseNursingHomeFromPlace(placeDetails, location);
                                    if (nursingHome != null) {
                                        nursingHomes.add(nursingHome);
                                    }
                                }
                            } else {
                                // Place ID가 없으면 기본 정보만 사용
                                CareCenter nursingHome = parseNursingHomeFromPlace(place, location);
                                if (nursingHome != null) {
                                    nursingHomes.add(nursingHome);
                                }
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "Google Places API 응답 상태: " + status);
                }
            } else {
                Log.e(TAG, "HTTP 응답 코드: " + responseCode);
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e(TAG, "키워드 '" + keyword + "' 검색 실패: " + e.getMessage());
        }
        
        return nursingHomes;
    }

    // 요양기관 여부 확인
    private boolean isNursingHome(JSONObject place, String keyword) {
        try {
            String name = place.getString("name").toLowerCase();
            String[] nursingHomeKeywords = {
                "요양", "양로", "노인", "복지", "너싱", "케어", "시설", "병원", "원"
            };
            
            for (String nursingKeyword : nursingHomeKeywords) {
                if (name.contains(nursingKeyword)) {
                    return true;
                }
            }
            
            // 타입 확인
            if (place.has("types")) {
                JSONArray types = place.getJSONArray("types");
                for (int i = 0; i < types.length(); i++) {
                    String type = types.getString(i);
                    if ("health".equals(type) || "hospital".equals(type)) {
                        return true;
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "요양기관 필터링 중 오류: " + e.getMessage());
        }
        
        return false;
    }

    // Place Details API로 상세 정보 가져오기
    private JSONObject getPlaceDetails(String placeId) {
        try {
            String apiKey = context.getString(R.string.google_maps_key);
            String urlString = String.format(
                "https://maps.googleapis.com/maps/api/place/details/json" +
                "?place_id=%s" +
                "&fields=name,formatted_address,vicinity,formatted_phone_number,geometry,photos,rating,user_ratings_total,types" +
                "&language=ko" +
                "&key=%s",
                placeId, apiKey
            );
            
            Log.d(TAG, "Place Details API 호출: " + placeId);
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                
                JSONObject jsonResponse = new JSONObject(response.toString());
                String status = jsonResponse.getString("status");
                
                if ("OK".equals(status)) {
                    JSONObject result = jsonResponse.getJSONObject("result");
                    Log.d(TAG, "Place Details 가져오기 성공: " + result.optString("name", ""));
                    return result;
                } else {
                    Log.w(TAG, "Place Details API 응답 상태: " + status);
                }
            } else {
                Log.e(TAG, "Place Details API HTTP 응답 코드: " + responseCode);
            }
            
            connection.disconnect();
            
        } catch (Exception e) {
            Log.e(TAG, "Place Details API 호출 실패: " + e.getMessage());
        }
        
        return null;
    }

    // Place JSON에서 CareCenter 객체 생성
    private CareCenter parseNursingHomeFromPlace(JSONObject place, LatLng currentLocation) {
        try {
            // 기본 정보 추출
            String name = place.getString("name");
            String address = place.optString("formatted_address", "");
            
            // 주소가 비어있으면 vicinty 사용
            if (address.isEmpty()) {
                address = place.optString("vicinity", "");
            }
            
            Log.d(TAG, "요양원 정보 파싱: " + name + ", 주소: " + address);
            
            // 위치 정보
            JSONObject geometry = place.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            LatLng placeLocation = new LatLng(lat, lng);
            
            // 거리 계산
            double distanceKm = calculateDistance(currentLocation, placeLocation);
            
            // 전화번호 - Google Places API에서 가져오기
            String phone = place.optString("formatted_phone_number", "");
            Log.d(TAG, "전화번호: " + phone);
            
            // 사진 URL
            String photoUrl = "";
            if (place.has("photos") && place.getJSONArray("photos").length() > 0) {
                JSONObject photo = place.getJSONArray("photos").getJSONObject(0);
                String photoReference = photo.getString("photo_reference");
                String apiKey = context.getString(R.string.google_maps_key);
                photoUrl = String.format(
                    "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=%s&key=%s",
                    photoReference, apiKey
                );
            }
            
            // 평점 정보
            double rating = place.optDouble("rating", 0.0);
            int userRatingsTotal = place.optInt("user_ratings_total", 0);
            
            // 임시 ID 생성 (실제로는 DB에서 관리)
            Long institutionId = (long) (name.hashCode() + address.hashCode());
            
            return new CareCenter(institutionId, name, address, phone, distanceKm, placeLocation, photoUrl, rating, userRatingsTotal);
            
        } catch (Exception e) {
            Log.e(TAG, "Place 파싱 실패: " + e.getMessage());
            return null;
        }
    }

    // 거리 계산 (Haversine 공식)
    private double calculateDistance(LatLng point1, LatLng point2) {
        double lat1 = Math.toRadians(point1.latitude);
        double lat2 = Math.toRadians(point2.latitude);
        double deltaLat = Math.toRadians(point2.latitude - point1.latitude);
        double deltaLng = Math.toRadians(point2.longitude - point1.longitude);
        
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                   Math.cos(lat1) * Math.cos(lat2) *
                   Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return 6371 * c; // 지구 반지름 (km)
    }

    // 중복 제거
    private List<CareCenter> removeDuplicateNursingHomes(List<CareCenter> nursingHomes) {
        List<CareCenter> uniqueHomes = new ArrayList<>();
        List<String> seenNames = new ArrayList<>();
        
        for (CareCenter home : nursingHomes) {
            String key = home.getName() + "|" + home.getAddress();
            if (!seenNames.contains(key)) {
                seenNames.add(key);
                uniqueHomes.add(home);
            }
        }
        
        return uniqueHomes;
    }
    
    // 거리 필터링 (반경 내의 요양원만)
    private List<CareCenter> filterByDistance(List<CareCenter> nursingHomes, int radiusMeters) {
        List<CareCenter> filteredHomes = new ArrayList<>();
        double radiusKm = radiusMeters / 1000.0; // 미터를 킬로미터로 변환
        
        for (CareCenter home : nursingHomes) {
            if (home.getDistanceKm() != null && home.getDistanceKm() <= radiusKm) {
                filteredHomes.add(home);
                Log.d(TAG, "포함: " + home.getName() + " (" + String.format("%.2f", home.getDistanceKm()) + "km)");
            } else {
                Log.d(TAG, "제외 (거리 초과): " + home.getName() + 
                          " (" + String.format("%.2f", home.getDistanceKm() != null ? home.getDistanceKm() : 0) + "km > " + radiusKm + "km)");
            }
        }
        
        return filteredHomes;
    }

    // 거리순 정렬
    private List<CareCenter> sortByDistance(List<CareCenter> nursingHomes, LatLng currentLocation) {
        List<CareCenter> sortedHomes = new ArrayList<>(nursingHomes);
        sortedHomes.sort((h1, h2) -> {
            double distance1 = h1.getDistanceKm() != null ? h1.getDistanceKm() : Double.MAX_VALUE;
            double distance2 = h2.getDistanceKm() != null ? h2.getDistanceKm() : Double.MAX_VALUE;
            return Double.compare(distance1, distance2);
        });
        
        return sortedHomes;
    }

    public void shutdown() {
        executor.shutdown();
    }
}
