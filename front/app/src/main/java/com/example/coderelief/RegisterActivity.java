package com.example.coderelief;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.AuthApiService;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    
    private RadioGroup rgUserType;
    private RadioButton rbGuardian, rbCaregiver;
    private TextInputLayout tilUsername, tilPassword, tilConfirmPassword, tilName, tilPhone, tilEmail;
    private TextInputLayout tilInstitutionCode, tilAddress, tilRelationship;
    private TextInputEditText etUsername, etPassword, etConfirmPassword, etName, etPhone, etEmail;
    private TextInputEditText etInstitutionCode, etAddress, etRelationship;
    private Button btnRegister, btnBackToLogin;
    private TextView tvTitle, tvSubtitle;
    
    private String selectedUserType = "guardian";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        setupClickListeners();
        updateUIForUserType();
    }
    
    private void initViews() {
        rgUserType = findViewById(R.id.rg_user_type);
        rbGuardian = findViewById(R.id.rb_guardian);
        rbCaregiver = findViewById(R.id.rb_caregiver);
        
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        tilName = findViewById(R.id.til_name);
        tilPhone = findViewById(R.id.til_phone);
        tilEmail = findViewById(R.id.til_email);
        tilInstitutionCode = findViewById(R.id.til_institution_code);
        tilAddress = findViewById(R.id.til_address);
        tilRelationship = findViewById(R.id.til_relationship);
        
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etEmail = findViewById(R.id.et_email);
        etInstitutionCode = findViewById(R.id.et_institution_code);
        etAddress = findViewById(R.id.et_address);
        etRelationship = findViewById(R.id.et_relationship);
        
        btnRegister = findViewById(R.id.btn_register);
        btnBackToLogin = findViewById(R.id.btn_back_to_login);
        
        tvTitle = findViewById(R.id.tv_title);
        tvSubtitle = findViewById(R.id.tv_subtitle);
    }
    
    private void setupClickListeners() {
        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_guardian) {
                selectedUserType = "guardian";
            } else if (checkedId == R.id.rb_caregiver) {
                selectedUserType = "caregiver";
            }
            updateUIForUserType();
        });
        
        btnRegister.setOnClickListener(v -> handleRegister());
        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    private void updateUIForUserType() {
        if ("guardian".equals(selectedUserType)) {
            tvTitle.setText("보호자 회원가입");
            tvSubtitle.setText("환자를 돌보는 보호자님을 위한 계정을 만들어주세요");
            
            // 보호자 관련 필드 표시
            tilAddress.setVisibility(View.VISIBLE);
            tilRelationship.setVisibility(View.VISIBLE);
            
            // 요양원 직원 관련 필드 숨김
            tilInstitutionCode.setVisibility(View.GONE);
            
        } else {
            tvTitle.setText("요양원 직원 회원가입");
            tvSubtitle.setText("요양원에서 근무하는 직원님을 위한 계정을 만들어주세요");
            
            // 요양원 직원 관련 필드 표시
            tilInstitutionCode.setVisibility(View.VISIBLE);
            
            // 보호자 관련 필드 숨김
            tilAddress.setVisibility(View.GONE);
            tilRelationship.setVisibility(View.GONE);
        }
    }
    
    private void handleRegister() {
        if (!validateInputs()) {
            return;
        }
        
        // 입력값 수집
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String institutionCode = etInstitutionCode.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String relationship = etRelationship.getText().toString().trim();
        
        // API 호출
        AuthApiService authApiService = ApiClient.getAuthApiService();
        AuthApiService.SignupRequest signupRequest = new AuthApiService.SignupRequest(
            username, password, name, phone, email, institutionCode, address, relationship
        );
        
        Call<AuthApiService.SignupResponse> call;
        if ("guardian".equals(selectedUserType)) {
            call = authApiService.guardianSignup(signupRequest);
        } else {
            call = authApiService.caregiverSignup(signupRequest);
        }
        
        call.enqueue(new Callback<AuthApiService.SignupResponse>() {
            @Override
            public void onResponse(Call<AuthApiService.SignupResponse> call, Response<AuthApiService.SignupResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthApiService.SignupResponse signupResponse = response.body();
                    
                    if ("SUCCESS".equals(signupResponse.getStatus())) {
                        // 회원가입 성공
                        String message = signupResponse.getMessage() != null ? 
                            signupResponse.getMessage() : "회원가입이 완료되었습니다!";
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                        
                        // 로그인 화면으로 이동
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // 회원가입 실패
                        String errorMessage = signupResponse.getMessage() != null ? 
                            signupResponse.getMessage() : "회원가입에 실패했습니다.";
                        Toast.makeText(RegisterActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                } else {
                    // 서버 오류
                    Toast.makeText(RegisterActivity.this, "서버 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<AuthApiService.SignupResponse> call, Throwable t) {
                // 네트워크 오류
                Toast.makeText(RegisterActivity.this, "네트워크 오류가 발생했습니다: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    
    private boolean validateInputs() {
        boolean isValid = true;
        
        // 공통 필드 검증
        if (etUsername.getText().toString().trim().isEmpty()) {
            tilUsername.setError("아이디를 입력해주세요");
            isValid = false;
        } else {
            tilUsername.setError(null);
        }
        
        if (etPassword.getText().toString().trim().isEmpty()) {
            tilPassword.setError("비밀번호를 입력해주세요");
            isValid = false;
        } else if (etPassword.getText().toString().length() < 6) {
            tilPassword.setError("비밀번호는 6자 이상이어야 합니다");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }
        
        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            tilConfirmPassword.setError("비밀번호가 일치하지 않습니다");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }
        
        if (etName.getText().toString().trim().isEmpty()) {
            tilName.setError("이름을 입력해주세요");
            isValid = false;
        } else {
            tilName.setError(null);
        }
        
        if (etPhone.getText().toString().trim().isEmpty()) {
            tilPhone.setError("전화번호를 입력해주세요");
            isValid = false;
        } else {
            tilPhone.setError(null);
        }
        
        if (etEmail.getText().toString().trim().isEmpty()) {
            tilEmail.setError("이메일을 입력해주세요");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }
        
        // 사용자 타입별 필드 검증
        if ("guardian".equals(selectedUserType)) {
            if (etAddress.getText().toString().trim().isEmpty()) {
                tilAddress.setError("주소를 입력해주세요");
                isValid = false;
            } else {
                tilAddress.setError(null);
            }
            
            if (etRelationship.getText().toString().trim().isEmpty()) {
                tilRelationship.setError("환자와의 관계를 입력해주세요");
                isValid = false;
            } else {
                tilRelationship.setError(null);
            }
        } else if ("caregiver".equals(selectedUserType)) {
            if (etInstitutionCode.getText().toString().trim().isEmpty()) {
                tilInstitutionCode.setError("기관코드를 입력해주세요");
                isValid = false;
            } else {
                tilInstitutionCode.setError(null);
            }
        }
        
        return isValid;
    }
}
