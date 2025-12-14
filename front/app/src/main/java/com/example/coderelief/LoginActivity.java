package com.example.coderelief;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.AuthApiService;
import com.example.coderelief.utils.PermissionUtils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    
    private Button btnGuardian, btnStaff, btnLoginSubmit;
    private TextInputEditText etUsername, etPassword;
    private MaterialToolbar toolbar;
    
    private boolean isGuardianSelected = false;
    private boolean isStaffSelected = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        btnGuardian = findViewById(R.id.btn_guardian);
        btnStaff = findViewById(R.id.btn_staff);
        btnLoginSubmit = findViewById(R.id.btn_login_submit);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
    
    private void setupClickListeners() {
        btnGuardian.setOnClickListener(v -> selectRole(true));
        btnStaff.setOnClickListener(v -> selectRole(false));
        
        btnLoginSubmit.setOnClickListener(v -> performLogin());
        
        findViewById(R.id.tv_register).setOnClickListener(v -> {
            // 회원가입 화면으로 이동
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        
        toolbar.setNavigationOnClickListener(v -> finish());
    }
    
    private void selectRole(boolean isGuardian) {
        isGuardianSelected = isGuardian;
        isStaffSelected = !isGuardian;
        
        // Update button appearances
        if (isGuardian) {
            btnGuardian.setBackgroundColor(getColor(R.color.app_logo_color));
            btnGuardian.setTextColor(getColor(android.R.color.white));
            btnStaff.setBackgroundColor(getColor(android.R.color.transparent));
            btnStaff.setTextColor(getColor(R.color.app_logo_color));
        } else {
            btnStaff.setBackgroundColor(getColor(R.color.app_logo_color));
            btnStaff.setTextColor(getColor(android.R.color.white));
            btnGuardian.setBackgroundColor(getColor(android.R.color.transparent));
            btnGuardian.setTextColor(getColor(R.color.app_logo_color));
        }
    }
    
    private void performLogin() {
        // 입력값 검증
        if (!isGuardianSelected && !isStaffSelected) {
            Toast.makeText(this, "사용자 구분을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        if (username.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // 로그인 API 호출
        AuthApiService authApiService = ApiClient.getAuthApiService();
        AuthApiService.LoginRequest loginRequest = new AuthApiService.LoginRequest(username, password);
        
        Call<AuthApiService.LoginResponse> call;
        if (isGuardianSelected) {
            call = authApiService.guardianLogin(loginRequest);
        } else {
            call = authApiService.caregiverLogin(loginRequest);
        }
        
        call.enqueue(new Callback<AuthApiService.LoginResponse>() {
            @Override
            public void onResponse(Call<AuthApiService.LoginResponse> call, Response<AuthApiService.LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApiService.LoginResponse loginResponse = response.body();
                    
                    if ("SUCCESS".equals(loginResponse.getStatus())) {
                        // 로그인 성공 - 권한 정보 저장
                        saveUserPermissions(loginResponse);
                        
                        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                        intent.putExtra("user_role", loginResponse.getRole());
                        intent.putExtra("username", loginResponse.getUsername());
                        intent.putExtra("token", loginResponse.getToken());
                        if (loginResponse.getInstitutionId() != null) {
                            intent.putExtra("institution_id", loginResponse.getInstitutionId());
                        }
                        if (loginResponse.getGuardianId() != null) {
                            intent.putExtra("guardian_id", loginResponse.getGuardianId());
                        }
                        if (loginResponse.getCaregiverId() != null) {
                            intent.putExtra("caregiver_id", loginResponse.getCaregiverId());
                        }
                        if (loginResponse.getHasGuardianData() != null) {
                            intent.putExtra("has_guardian_data", loginResponse.getHasGuardianData());
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        // 로그인 실패
                        String errorMessage = loginResponse.getMessage() != null ? 
                            loginResponse.getMessage() : "로그인에 실패했습니다.";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 서버 오류
                    Toast.makeText(LoginActivity.this, "서버 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<AuthApiService.LoginResponse> call, Throwable t) {
                // 네트워크 오류
                Toast.makeText(LoginActivity.this, "네트워크 오류가 발생했습니다: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * 로그인 응답에서 권한 정보를 추출하여 저장
     */
    private void saveUserPermissions(AuthApiService.LoginResponse loginResponse) {
        String userRole = loginResponse.getRole();
        String caregiverRole = loginResponse.getCaregiverRole();
        Boolean canAccessConsultations = loginResponse.getCanAccessConsultations();
        String caregiverName = loginResponse.getCaregiverName();
        Long institutionId = loginResponse.getInstitutionId();
        
        // 기본값 설정
        if (caregiverRole == null) {
            caregiverRole = "STAFF"; // 기본값
        }
        if (canAccessConsultations == null) {
            canAccessConsultations = false; // 기본값
        }
        if (caregiverName == null) {
            caregiverName = ""; // 기본값
        }
        
        // PermissionUtils를 사용하여 권한 정보 저장
        PermissionUtils.saveUserPermissions(
            this,
            userRole,
            caregiverRole,
            canAccessConsultations,
            caregiverName,
            institutionId
        );
        
        android.util.Log.d("LoginActivity", "권한 정보 저장 완료: " +
                "userRole=" + userRole + 
                ", caregiverRole=" + caregiverRole + 
                ", canAccessConsultations=" + canAccessConsultations +
                ", caregiverName=" + caregiverName +
                ", institutionId=" + institutionId);
    }
}