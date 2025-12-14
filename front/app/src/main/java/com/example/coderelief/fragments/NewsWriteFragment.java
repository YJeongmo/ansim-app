package com.example.coderelief.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderelief.DashboardActivity;
import com.example.coderelief.R;

/**
 * 환자 소식 작성 Fragment
 * coderelief1의 WriteNewsScreenPreview를 참고하여 구현
 */
public class NewsWriteFragment extends Fragment {
    
    private TextView tvTitle;
    private TextView tvPatientInfo;
    private EditText etNewsTitle;
    private EditText etNewsContent;
    private ImageView ivPhoto;
    private Button btnSelectPhoto;
    private Button btnSaveNews;
    private Button btnPublishNews;
    
    private String patientName;
    private Long patientId;
    private String userRole;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_write, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        getArgumentsData();
        setupPatientInfo();
        setupClickListeners();
    }
    
    private void initViews(View view) {
        tvTitle = view.findViewById(R.id.tv_title);
        tvPatientInfo = view.findViewById(R.id.tv_patient_info);
        etNewsTitle = view.findViewById(R.id.et_news_title);
        etNewsContent = view.findViewById(R.id.et_news_content);
        ivPhoto = view.findViewById(R.id.iv_photo);
        btnSelectPhoto = view.findViewById(R.id.btn_select_photo);
        btnSaveNews = view.findViewById(R.id.btn_save_news);
        btnPublishNews = view.findViewById(R.id.btn_publish_news);
    }
    
    private void getArgumentsData() {
        Bundle args = getArguments();
        if (args != null) {
            patientName = args.getString("patient_name", "환자");
            patientId = args.getLong("patient_id", 0L);
            userRole = args.getString("user_role", "caregiver");
        }
    }
    
    private void setupPatientInfo() {
        tvTitle.setText("소식 작성");
        if (patientName != null) {
            tvPatientInfo.setText(patientName + " 환자의 소식을 작성해주세요");
        }
    }
    
    private void setupClickListeners() {
        // 사진 선택 버튼
        btnSelectPhoto.setOnClickListener(v -> {
            // TODO: 갤러리에서 사진 선택 또는 카메라로 촬영 기능 구현
            Toast.makeText(getContext(), "사진 선택 기능 (추후 구현)", Toast.LENGTH_SHORT).show();
        });
        
        // 임시 저장 버튼
        btnSaveNews.setOnClickListener(v -> {
            if (validateInput()) {
                // TODO: 임시 저장 로직 구현
                Toast.makeText(getContext(), "소식이 임시 저장되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 게시 버튼
        btnPublishNews.setOnClickListener(v -> {
            if (validateInput()) {
                publishNews();
            }
        });
    }
    
    private boolean validateInput() {
        String title = etNewsTitle.getText().toString().trim();
        String content = etNewsContent.getText().toString().trim();
        
        if (title.isEmpty()) {
            etNewsTitle.setError("제목을 입력해주세요");
            etNewsTitle.requestFocus();
            return false;
        }
        
        if (content.isEmpty()) {
            etNewsContent.setError("내용을 입력해주세요");
            etNewsContent.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void publishNews() {
        String title = etNewsTitle.getText().toString().trim();
        String content = etNewsContent.getText().toString().trim();
        
        // TODO: 실제 게시 로직 구현
        // - 서버에 소식 데이터 전송
        // - 이미지 업로드 (있는 경우)
        // - activity 테이블에 저장 (type='News')
        
        if (getContext() != null) {
            Toast.makeText(getContext(), 
                patientName + " 환자의 소식이 게시되었습니다", 
                Toast.LENGTH_SHORT).show();
        }
        
        // 성공 후 이전 화면으로 돌아가기
        if (getActivity() instanceof DashboardActivity) {
            ((DashboardActivity) getActivity()).onBackPressed();
        }
    }
}