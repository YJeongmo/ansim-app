package com.example.coderelief.api;

import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import com.example.coderelief.api.ConsultationRequestApiService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Retrofit을 사용한 API 클라이언트
 */
public class ApiClient {
    
    private static final String TAG = "ApiClient";
    
    // 환경별 BASE_URL 설정
    private static final String BASE_URL_EMULATOR = "http://10.0.2.2:8080/"; // Android 에뮬레이터 (HTTP)
    private static final String BASE_URL_LOCAL = "http://localhost:8080/";   // 로컬 테스트 (HTTP)
    private static final String BASE_URL_DEVICE = "http://192.168.1.100:8080/"; // 실제 기기 (HTTP)
    
    // 현재 사용할 BASE_URL 선택
    // Android 에뮬레이터용 HTTP 사용 (10.0.2.2:8080)
    private static final String BASE_URL = BASE_URL_EMULATOR;
    
    private static Retrofit retrofit = null;
    private static Gson gson = null;
    
    public static Retrofit getClient() {
        if (retrofit == null) {
            Log.d(TAG, "Creating Retrofit client with BASE_URL: " + BASE_URL);
            
            // Gson TypeAdapter 설정
            if (gson == null) {
                gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>() {
                    @Override
                    public void write(JsonWriter out, LocalDate value) throws IOException {
                        if (value == null) {
                            out.nullValue();
                        } else {
                            out.value(value.toString());
                        }
                    }
                    
                    @Override
                    public LocalDate read(JsonReader in) throws IOException {
                        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        
                        String dateStr = in.nextString();
                        if (dateStr == null || dateStr.isEmpty()) {
                            return null;
                        }
                        
                        try {
                            // ISO 형식 (2025-09-03) 먼저 시도
                            return LocalDate.parse(dateStr);
                        } catch (Exception e) {
                            try {
                                // 다른 형식 시도 (예: 2025/09/03)
                                return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                            } catch (Exception e2) {
                                Log.e("ApiClient", "LocalDate 파싱 실패: " + dateStr, e2);
                                return null;
                            }
                        }
                    }
                })
                .registerTypeAdapter(LocalTime.class, new TypeAdapter<LocalTime>() {
                    @Override
                    public void write(JsonWriter out, LocalTime value) throws IOException {
                        if (value == null) {
                            out.nullValue();
                        } else {
                            out.value(value.toString());
                        }
                    }
                    
                    @Override
                    public LocalTime read(JsonReader in) throws IOException {
                        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        
                        String timeStr = in.nextString();
                        if (timeStr == null || timeStr.isEmpty()) {
                            return null;
                        }
                        
                        try {
                            // ISO 형식 (13:04:41) 먼저 시도
                            return LocalTime.parse(timeStr);
                        } catch (Exception e) {
                            try {
                                // 다른 형식 시도 (예: 13:04)
                                return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
                            } catch (Exception e2) {
                                Log.e("ApiClient", "LocalTime 파싱 실패: " + timeStr, e2);
                                return null;
                            }
                        }
                    }
                })
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter out, LocalDateTime value) throws IOException {
                        if (value == null) {
                            out.nullValue();
                        } else {
                            out.value(value.toString());
                        }
                    }
                    
                    @Override
                    public LocalDateTime read(JsonReader in) throws IOException {
                        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
                            in.nextNull();
                            return null;
                        }
                        
                        String dateTimeStr = in.nextString();
                        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
                            return null;
                        }
                        
                        try {
                            // ISO 형식 (2025-09-03T13:04:41) 먼저 시도
                            return LocalDateTime.parse(dateTimeStr);
                        } catch (Exception e1) {
                            try {
                                // ISO 형식 with timezone (2025-09-14T11:53:46.000+00:00) 시도
                                // timezone 부분을 제거하고 LocalDateTime으로 파싱
                                String withoutTimezone = dateTimeStr.replaceAll("\\.[0-9]{3}\\+[0-9]{2}:[0-9]{2}$", "");
                                return LocalDateTime.parse(withoutTimezone);
                            } catch (Exception e2) {
                                try {
                                    // 공백 포함 형식 (2025-09-03 13:04:41) 시도
                                    return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                                } catch (Exception e3) {
                                    try {
                                        // 날짜만 있는 경우 (2025-09-03) 시도
                                        LocalDate date = LocalDate.parse(dateTimeStr);
                                        return date.atStartOfDay();
                                    } catch (Exception e4) {
                                        Log.e("ApiClient", "LocalDateTime 파싱 실패: " + dateTimeStr, e4);
                                        return null;
                                    }
                                }
                            }
                        }
                    }
                })
                .create();
            }
            
            // OkHttpClient 설정 (HTTP 사용으로 SSL 설정 불필요)
            OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)  // 연결 타임아웃 30초
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)     // 읽기 타임아웃 60초
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)    // 쓰기 타임아웃 30초
                .build();
            
            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
        }
        return retrofit;
    }
    
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
    
    public static AuthApiService getAuthApiService() {
        return getClient().create(AuthApiService.class);
    }
    
    public static ConsultationRequestApiService getConsultationRequestApiService() {
        return getClient().create(ConsultationRequestApiService.class);
    }
    
    public static ReservationApiService getReservationApiService() {
        return getClient().create(ReservationApiService.class);
    }
    
    public static InstitutionApiService getInstitutionApiService() {
        return getClient().create(InstitutionApiService.class);
    }
    
    public static ChatApiService getChatApiService() {
        return getClient().create(ChatApiService.class);
    }
    
    public static NotificationApiService getNotificationApiService() {
        return getClient().create(NotificationApiService.class);
    }
    
    public static HealthAnalysisApiService getHealthAnalysisApiService() {
        return getClient().create(HealthAnalysisApiService.class);
    }
    
    // 현재 BASE_URL 반환 (디버깅용)
    public static String getCurrentBaseUrl() {
        return BASE_URL;
    }
    
    // 현재 구성된 Gson 반환 (수동 파싱 시 재사용)
    public static Gson getGson() {
        if (gson == null) {
            // Ensure initialization path constructs gson
            getClient();
        }
        return gson;
    }
}
