package com.example.coderelief;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.coderelief.fragments.ConsultationFragment;

public class MainActivity extends AppCompatActivity {

    private Button btnFindCareCenter, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        btnFindCareCenter = findViewById(R.id.btn_find_care_center);
        btnLogin = findViewById(R.id.btn_login);
    }
    
    private void setupClickListeners() {
        btnFindCareCenter.setOnClickListener(v -> {
            // Navigate to Find Care Center Fragment
            Intent intent = new Intent(this, DashboardActivity.class);
            intent.putExtra("initial_fragment", "find_care_center");
            startActivity(intent);
        });
        
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }
    
    public void navigateToFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}