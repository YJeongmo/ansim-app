package com.example.coderelief.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.coderelief.models.Patient;
import com.example.coderelief.models.Activity;
import com.example.coderelief.models.DailyRecord;
import com.example.coderelief.models.Notice;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 환자 정보를 CSV 파일로 내보내는 유틸리티 클래스
 * Excel 호환성 문제를 피하기 위해 CSV 사용
 */
public class CsvExporter {
    
    private static final String TAG = "CsvExporter";
    
    /**
     * 환자 정보를 CSV 파일로 내보내기
     */
    public static File exportPatientInfo(Patient patient, List<Activity> activities, 
                                       List<DailyRecord> dailyRecords, List<Notice> notices, 
                                       Context context) throws IOException {
        
        Log.d(TAG, "=== CSV 내보내기 시작 ===");
        Log.d(TAG, "환자명: " + patient.getName());
        Log.d(TAG, "활동 기록 수: " + activities.size());
        Log.d(TAG, "일일 기록 수: " + dailyRecords.size());
        Log.d(TAG, "공지사항 수: " + notices.size());
        
        // 파일명 생성
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = String.format("환자정보_%s_%s.csv", patient.getName(), timestamp);
        Log.d(TAG, "파일명: " + fileName);
        
        // 저장 경로 설정 (Files 앱에서 접근 가능한 Downloads 폴더 사용)
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.d(TAG, "저장 경로: " + downloadsDir.getAbsolutePath());
        
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs();
            Log.d(TAG, "Downloads 폴더 생성됨");
        }
        
        File csvFile = new File(downloadsDir, fileName);
        Log.d(TAG, "CSV 파일 경로: " + csvFile.getAbsolutePath());
        
        try (FileWriter writer = new FileWriter(csvFile, false)) {
            // UTF-8 BOM 추가 (Excel에서 한글 깨짐 방지)
            writer.write('\ufeff');
            
            // 1. 기본 정보 섹션
            writeBasicInfo(writer, patient);
            
            // 2. 일일 기록 섹션
            writeDailyRecords(writer, dailyRecords);
            
            // 3. 급여 기록 섹션
            writeMealRecords(writer, activities);
            
            // 4. 약물 복용 섹션
            writeMedicationRecords(writer, activities);
            
            // 5. 활동 프로그램 섹션
            writeActivityRecords(writer, activities);
            
            // 6. 공지사항 섹션
            writeNotices(writer, notices);
            
            // 버퍼 플러시 (파일 크기 정확히 반영)
            writer.flush();
            
            Log.d(TAG, "CSV 파일 생성 완료: " + csvFile.getAbsolutePath());
            Log.d(TAG, "파일 크기: " + csvFile.length() + " bytes");
            Log.d(TAG, "파일 존재 여부: " + csvFile.exists());
            
            // 파일 내용 미리보기 (처음 500자)
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(csvFile));
                StringBuilder preview = new StringBuilder();
                String line;
                int lineCount = 0;
                while ((line = reader.readLine()) != null && lineCount < 10) {
                    preview.append(line).append("\n");
                    lineCount++;
                }
                reader.close();
                Log.d(TAG, "파일 내용 미리보기:\n" + preview.toString());
            } catch (Exception e) {
                Log.e(TAG, "파일 내용 읽기 실패: " + e.getMessage());
            }
            
            Log.d(TAG, "=== CSV 내보내기 성공 ===");
            return csvFile;
            
        } catch (IOException e) {
            Log.e(TAG, "CSV 파일 생성 실패: " + e.getMessage());
            Log.e(TAG, "오류 상세: " + e.toString());
            throw e;
        }
    }
    
    /**
     * 기본 정보 작성
     */
    private static void writeBasicInfo(FileWriter writer, Patient patient) throws IOException {
        writer.write("=== 환자 기본 정보 ===\n");
        writer.write("항목,내용\n");
        writer.write("환자명," + (patient.getName() != null ? patient.getName() : "") + "\n");
        writer.write("나이," + (patient.getAge() != null ? patient.getAge() + "세" : "") + "\n");
        writer.write("생년월일," + (patient.getBirthdate() != null ? patient.getBirthdate() : "") + "\n");
        writer.write("입소일," + (patient.getAdmissionDate() != null ? patient.getAdmissionDate() : "") + "\n");
        writer.write("방호실," + (patient.getRoomNumber() != null ? patient.getRoomNumber() : "") + "\n");
        writer.write("요양등급," + (patient.getCareLevel() != null ? patient.getCareLevel() : "") + "\n");
        writer.write("\n");
    }
    
    /**
     * 일일 기록 작성
     */
    private static void writeDailyRecords(FileWriter writer, List<DailyRecord> dailyRecords) throws IOException {
        writer.write("=== 일일 기록 ===\n");
        writer.write("날짜,시간대,식사상태,건강상태,약물복용,특이사항,작성일시\n");
        
        for (DailyRecord record : dailyRecords) {
            writer.write((record.getRecordDate() != null ? record.getRecordDate() : "") + ",");
            writer.write((record.getTimeSlot() != null ? record.getTimeSlot() : "") + ",");
            writer.write((record.getMeal() != null ? record.getMeal() : "") + ",");
            writer.write((record.getHealthCondition() != null ? record.getHealthCondition() : "") + ",");
            writer.write((record.isMedicationTaken() ? "복용완료" : "미복용") + ",");
            writer.write((record.getNotes() != null ? record.getNotes() : "") + ",");
            writer.write((record.getCreatedAt() != null ? record.getCreatedAt() : "") + "\n");
        }
        writer.write("\n");
    }
    
    /**
     * 급여 기록 작성
     */
    private static void writeMealRecords(FileWriter writer, List<Activity> activities) throws IOException {
        writer.write("=== 급여 기록 ===\n");
        writer.write("날짜,시간대,식사상태,특이사항,작성자\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Activity activity : activities) {
            if ("Meal".equals(activity.getType())) {
                writer.write((activity.getActivityTime() != null ? sdf.format(activity.getActivityTime()) : "") + ",");
                writer.write("식사시간,");
                writer.write((activity.getDescription() != null ? activity.getDescription() : "") + ",");
                writer.write((activity.getNotes() != null ? activity.getNotes() : "") + ",");
                writer.write("요양보호사\n");
            }
        }
        writer.write("\n");
    }
    
    /**
     * 약물 복용 기록 작성
     */
    private static void writeMedicationRecords(FileWriter writer, List<Activity> activities) throws IOException {
        writer.write("=== 약물 복용 기록 ===\n");
        writer.write("날짜,약물명,복용시간,복용상태,특이사항\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Activity activity : activities) {
            if ("Medication".equals(activity.getType())) {
                writer.write((activity.getActivityTime() != null ? sdf.format(activity.getActivityTime()) : "") + ",");
                writer.write((activity.getDescription() != null ? activity.getDescription() : "") + ",");
                writer.write("복용시간,");
                writer.write("복용완료,");
                writer.write((activity.getNotes() != null ? activity.getNotes() : "") + "\n");
            }
        }
        writer.write("\n");
    }
    
    /**
     * 활동 프로그램 기록 작성
     */
    private static void writeActivityRecords(FileWriter writer, List<Activity> activities) throws IOException {
        writer.write("=== 활동 프로그램 기록 ===\n");
        writer.write("날짜,프로그램명,참여도,사진,특이사항\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Activity activity : activities) {
            if ("Program".equals(activity.getType())) {
                writer.write((activity.getActivityTime() != null ? sdf.format(activity.getActivityTime()) : "") + ",");
                writer.write((activity.getDescription() != null ? activity.getDescription() : "") + ",");
                writer.write("참여,");
                writer.write((activity.getPhotoUrl() != null ? "있음" : "없음") + ",");
                writer.write((activity.getNotes() != null ? activity.getNotes() : "") + "\n");
            }
        }
        writer.write("\n");
    }
    
    /**
     * 공지사항 작성
     */
    private static void writeNotices(FileWriter writer, List<Notice> notices) throws IOException {
        writer.write("=== 공지사항 ===\n");
        writer.write("날짜,제목,내용,우선순위,작성자\n");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Notice notice : notices) {
            // API 24 호환을 위해 현재 시간 사용
            String currentTime = sdf.format(new Date());
            writer.write((notice.getCreatedAt() != null ? notice.getCreatedAtString() : currentTime) + ",");
            writer.write((notice.getTitle() != null ? notice.getTitle() : "") + ",");
            writer.write((notice.getContent() != null ? notice.getContent() : "") + ",");
            writer.write((notice.getPriority() != null ? notice.getPriority() : "") + ",");
            writer.write("요양보호사\n");
        }
        writer.write("\n");
    }
}
