package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.R;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.models.Activity;
import com.example.coderelief.models.DailyRecord;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailFragment extends Fragment {
    
    private TextView tvTitle, tvPatientName, tvDetailContent;
    
    private String patientName;
    private String detailType;
    private String userRole;
    private Long patientId;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_patient_detail, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        loadDetailContent();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvPatientName = view.findViewById(R.id.tv_patient_name);
        tvDetailContent = view.findViewById(R.id.tv_detail_content);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "환자");
            detailType = args.getString("detail_type", "meal");
            userRole = args.getString("user_role", "guardian");
            patientId = args.getLong("patient_id", 0L);
        } else {
            patientName = "환자";
            detailType = "meal";
            userRole = "guardian";
            patientId = 0L;
        }
    }
    
    private void loadDetailContent() {
        tvPatientName.setText(patientName);
        
        if ("meal".equals(detailType)) {
            tvTitle.setText("오늘의 급여");
            if (patientId != null && patientId > 0) {
                loadMealContentFromApi();
            } else {
                loadMealContent();
            }
        } else {
            tvTitle.setText("오늘의 활동");
            if (patientId != null && patientId > 0) {
                loadActivityContentFromApi();
            } else {
                loadActivityContent();
            }
        }
    }
    
    private void loadMealContent() {
        String mealContent = "2024-01-15 급여 현황\n\n" +
                "식사 현황\n" +
                "아침: 완식 (100%)\n" +
                "점심: 반식 (50%)\n" +
                "저녁: 완식 (100%)\n\n" +
                "투약 현황\n" +
                "혈압약: 복용 완료 (09:00)\n" +
                "당뇨약: 복용 완료 (12:00)\n" +
                "심장약: 복용 완료 (18:00)\n\n" +
                "건강상태\n" +
                "체온: 36.5°C (정상)\n" +
                "혈압: 120/80 mmHg (정상)\n" +
                "혈당: 110 mg/dL (정상)\n" +
                "특이사항: 없음";
        tvDetailContent.setText(mealContent);
    }
    
    private void loadActivityContent() {
        String activityContent = "2024-01-15 활동 현황\n\n" +
                "오전 활동\n" +
                "09:00 - 10:30: 미술 치료\n" +
                "참여도: 적극적\n" +
                "작품: 봄 꽃 그리기\n\n" +
                "오후 활동\n" +
                "14:00 - 15:30: 음악 치료\n" +
                "참여도: 보통\n" +
                "활동: 노래 부르기, 악기 연주\n\n" +
                "저녁 활동\n" +
                "19:00 - 20:00: 산책\n" +
                "참여도: 적극적\n" +
                "코스: 요양원 정원 산책\n\n" +
                "※ 참여 사진은 요양원에서 별도로 제공됩니다.";
        tvDetailContent.setText(activityContent);
    }
    
    // API에서 활동 기록 가져오기
    private void loadActivityContentFromApi() {
        if (patientId == null || patientId == 0L) {
            loadActivityContent(); // 폴백 데이터 사용
            return;
        }
        
        ApiClient.getApiService().getActivitiesByPatient(patientId)
                .enqueue(new Callback<List<Activity>>() {
                    @Override
                    public void onResponse(Call<List<Activity>> call, Response<List<Activity>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<Activity> activities = response.body();
                            displayActivityContent(activities);
                        } else {
                            loadActivityContent(); // 폴백 데이터 사용
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<Activity>> call, Throwable t) {
                        Toast.makeText(getContext(), "활동 기록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                        loadActivityContent(); // 폴백 데이터 사용
                    }
                });
    }
    
    // API에서 급여 기록 가져오기
    private void loadMealContentFromApi() {
        if (patientId == null || patientId == 0L) {
            Log.d("PatientDetail", "patientId가 null이므로 폴백 데이터 사용");
            loadMealContent(); // 폴백 데이터 사용
            return;
        }
        
        Log.d("PatientDetail", "API 호출 시작: patientId = " + patientId);
        ApiClient.getApiService().getDailyRecordsByPatient(patientId)
                .enqueue(new Callback<List<DailyRecord>>() {
                    @Override
                    public void onResponse(Call<List<DailyRecord>> call, Response<List<DailyRecord>> response) {
                        Log.d("PatientDetail", "API 응답: success=" + response.isSuccessful() + ", body=" + response.body());
                        if (response.isSuccessful() && response.body() != null) {
                            List<DailyRecord> dailyRecords = response.body();
                            Log.d("PatientDetail", "급여 기록 수: " + dailyRecords.size());
                            if (!dailyRecords.isEmpty()) {
                                displayMealContent(dailyRecords);
                            } else {
                                Log.d("PatientDetail", "급여 기록이 비어있으므로 폴백 데이터 사용");
                                loadMealContent(); // 폴백 데이터 사용
                            }
                        } else {
                            Log.d("PatientDetail", "API 응답 실패 또는 body가 null이므로 폴백 데이터 사용");
                            loadMealContent(); // 폴백 데이터 사용
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<DailyRecord>> call, Throwable t) {
                        Log.e("PatientDetail", "API 호출 실패", t);
                        Toast.makeText(getContext(), "급여 기록을 불러올 수 없습니다", Toast.LENGTH_SHORT).show();
                        loadMealContent(); // 폴백 데이터 사용
                    }
                });
    }
    
    // 활동 기록 표시
    private void displayActivityContent(List<Activity> activities) {
        if (activities.isEmpty()) {
            String noActivityContent = "오늘의 활동 기록이 없습니다.\n\n" +
                    "※ 새로운 활동 기록이 입력되면 여기에 표시됩니다.";
            tvDetailContent.setText(noActivityContent);
            return;
        }
        
        StringBuilder activityContent = new StringBuilder();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        
        // 가장 최근 활동의 날짜로 제목 설정
        Date latestDate = activities.get(0).getActivityTime();
        String dateStr = dateFormat.format(latestDate);
        activityContent.append(dateStr).append(" 활동 현황\n\n");
        
        for (Activity activity : activities) {
            String timeStr = timeFormat.format(activity.getActivityTime());
            String type = activity.getType() != null ? activity.getType() : "활동";
            String description = activity.getDescription() != null ? activity.getDescription() : "내용 없음";
            
            activityContent.append("📸 ").append(timeStr).append(": ").append(type).append("\n");
            activityContent.append("   ").append(description).append("\n\n");
        }
        
        activityContent.append("※ 참여 사진은 요양원에서 별도로 제공됩니다.");
        tvDetailContent.setText(activityContent.toString());
    }
    
    // 급여 기록 표시
    private void displayMealContent(List<DailyRecord> dailyRecords) {
        if (dailyRecords.isEmpty()) {
            String noMealContent = "오늘의 급여 기록이 없습니다.\n\n" +
                    "※ 새로운 급여 기록이 입력되면 여기에 표시됩니다.";
            tvDetailContent.setText(noMealContent);
            return;
        }
        
        StringBuilder mealContent = new StringBuilder();
        
        // 가장 최근 기록의 날짜로 제목 설정 (이제 String이므로 직접 사용)
        String dateStr = dailyRecords.get(0).getRecordDate();
        mealContent.append(dateStr).append(" 급여 현황\n\n");
        
        // 가장 최근 기록만 사용 (하루 기록이므로)
        DailyRecord record = dailyRecords.get(0);
        String meal = record.getMeal() != null ? record.getMeal() : "상태 미정";
        String healthCondition = record.getHealthCondition() != null ? record.getHealthCondition() : "상태 미정";
        String notes = record.getNotes() != null ? record.getNotes() : "";
        
        // notes 파싱하여 각 항목별로 표시
        MealDetails mealDetails = parseMealNotes(notes);
        
        mealContent.append("🍽️ 아침 식사\n");
        mealContent.append("   상태: ").append(mealDetails.breakfast).append("\n\n");
        
        mealContent.append("🍽️ 점심 식사\n");
        mealContent.append("   상태: ").append(mealDetails.lunch).append("\n\n");
        
        mealContent.append("🍽️ 저녁 식사\n");
        mealContent.append("   상태: ").append(mealDetails.dinner).append("\n\n");
        
        if (!mealDetails.medication.isEmpty()) {
            mealContent.append("💊 투약 내역\n");
            mealContent.append("   ").append(mealDetails.medication).append("\n\n");
        }
        
        if (!mealDetails.healthStatus.isEmpty()) {
            mealContent.append("🏥 건강상태\n");
            mealContent.append("   ").append(mealDetails.healthStatus).append("\n\n");
        }
        
        if (!mealDetails.specialNotes.isEmpty()) {
            mealContent.append("📝 특이사항\n");
            mealContent.append("   ").append(mealDetails.specialNotes).append("\n\n");
        }
        
        mealContent.append("※ 상세한 건강 정보는 요양원에서 별도로 제공됩니다.");
        tvDetailContent.setText(mealContent.toString());
    }
    
    // notes 파싱을 위한 내부 클래스
    private static class MealDetails {
        String breakfast = "기록 없음";
        String lunch = "기록 없음";
        String dinner = "기록 없음";
        String medication = "";
        String healthStatus = "";
        String specialNotes = "";
    }
    
    // notes 문자열을 파싱하여 각 항목별로 분리
    private MealDetails parseMealNotes(String notes) {
        MealDetails details = new MealDetails();
        
        if (notes == null || notes.isEmpty()) {
            return details;
        }
        
        String[] lines = notes.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("아침:")) {
                details.breakfast = line.substring(3).trim();
            } else if (line.startsWith("점심:")) {
                details.lunch = line.substring(3).trim();
            } else if (line.startsWith("저녁:")) {
                details.dinner = line.substring(3).trim();
            } else if (line.startsWith("투약:")) {
                details.medication = line.substring(3).trim();
            } else if (line.startsWith("건강상태:")) {
                details.healthStatus = line.substring(5).trim();
            } else if (line.startsWith("특이사항:")) {
                details.specialNotes = line.substring(5).trim();
            }
        }
        
        return details;
    }
}