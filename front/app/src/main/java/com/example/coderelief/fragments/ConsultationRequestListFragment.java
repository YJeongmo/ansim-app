package com.example.coderelief.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderelief.R;
import com.example.coderelief.adapters.ConsultationRequestAdapter;
import com.example.coderelief.api.ApiClient;
import com.example.coderelief.api.ConsultationRequestApiService;
import com.example.coderelief.models.ConsultationRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultationRequestListFragment extends Fragment {
    
    private static final String ARG_INSTITUTION_ID = "institution_id";
    
    private RecyclerView recyclerView;
    private ConsultationRequestAdapter adapter;
    private List<ConsultationRequest> consultationRequests;
    
    private EditText etSearchApplicant;
    private Button btnSearchApplicant, btnRefresh;
    private LinearLayout layoutSearch, layoutLoading;
    private TextView tvStats;
    
    private ConsultationRequestApiService apiService;
    private Long institutionId;
    
    public static ConsultationRequestListFragment newInstance(Long institutionId) {
        ConsultationRequestListFragment fragment = new ConsultationRequestListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_INSTITUTION_ID, institutionId);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            institutionId = getArguments().getLong(ARG_INSTITUTION_ID);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, 
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_consultation_request_list, container, false);
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initViews(view);
        setupRecyclerView();
        setupClickListeners();
        
        apiService = ApiClient.getConsultationRequestApiService();
        loadConsultationRequests();
    }
    
    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewConsultationRequests);
        etSearchApplicant = view.findViewById(R.id.etSearchApplicant);
        btnSearchApplicant = view.findViewById(R.id.btnSearchApplicant);
        btnRefresh = view.findViewById(R.id.btnRefresh);
        layoutSearch = view.findViewById(R.id.layoutSearch);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        tvStats = view.findViewById(R.id.tvStats);
    }
    
    private void setupRecyclerView() {
        consultationRequests = new ArrayList<>();
        adapter = new ConsultationRequestAdapter(consultationRequests, this::onConsultationRequestClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
    
    private void setupClickListeners() {
        btnSearchApplicant.setOnClickListener(v -> searchByApplicant());
        btnRefresh.setOnClickListener(v -> loadConsultationRequests());
    }
    
    private void loadConsultationRequests() {
        showLoading(true);
        
        Log.d("ConsultationList", "Loading consultation requests for institution: " + institutionId);
        
        // 서버에서 해당 institution_id의 데이터만 가져오기
        Call<List<ConsultationRequest>> call = apiService.getConsultationRequestsByInstitution(institutionId);
        call.enqueue(new Callback<List<ConsultationRequest>>() {
            @Override
            public void onResponse(Call<List<ConsultationRequest>> call, Response<List<ConsultationRequest>> response) {
                Log.d("ConsultationList", "Response code: " + response.code());
                Log.d("ConsultationList", "Response body: " + response.body());
                Log.d("ConsultationList", "Response error body: " + response.errorBody());
                
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ConsultationRequest> requests = response.body();
                    
                    Log.d("ConsultationList", "서버에서 받은 상담 신청 수: " + requests.size());
                    Log.d("ConsultationList", "현재 요양원 ID: " + institutionId);
                    
                    // 각 상담 신청 로깅
                    for (ConsultationRequest request : requests) {
                        Log.d("ConsultationList", "상담 신청 - 요양원 ID: " + request.getInstitutionId() + ", 요양원명: " + request.getInstitutionName());
                    }
                    
                    consultationRequests.clear();
                    consultationRequests.addAll(requests);
                    adapter.notifyDataSetChanged();
                    updateStats();
                } else {
                    String errorMessage = "상담 신청 목록을 불러오는데 실패했습니다.";
                    if (response.code() == 404) {
                        errorMessage = "API 엔드포인트를 찾을 수 없습니다.";
                    } else if (response.code() == 500) {
                        errorMessage = "서버 내부 오류가 발생했습니다.";
                    }
                    Toast.makeText(getContext(), errorMessage + " (코드: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<ConsultationRequest>> call, Throwable t) {
                showLoading(false);
                String errorMessage = "네트워크 오류가 발생했습니다: " + t.getMessage();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                t.printStackTrace(); // 로그캣에 상세 오류 출력
            }
        });
    }
    
    
    private void searchByApplicant() {
        String applicantName = etSearchApplicant.getText().toString().trim();
        if (applicantName.isEmpty()) {
            Toast.makeText(getContext(), "신청자명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        
        showLoading(true);
        // 서버에서 해당 institution_id의 데이터만 가져온 후 클라이언트에서 신청자명으로 필터링
        Call<List<ConsultationRequest>> call = apiService.getConsultationRequestsByInstitution(institutionId);
        call.enqueue(new Callback<List<ConsultationRequest>>() {
            @Override
            public void onResponse(Call<List<ConsultationRequest>> call, Response<List<ConsultationRequest>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ConsultationRequest> allRequests = response.body();
                    List<ConsultationRequest> filteredRequests = new ArrayList<>();
                    
                    // 클라이언트에서 신청자명으로 필터링
                    for (ConsultationRequest request : allRequests) {
                        if (request.getApplicantName().toLowerCase().contains(applicantName.toLowerCase())) {
                            filteredRequests.add(request);
                        }
                    }
                    
                    consultationRequests.clear();
                    consultationRequests.addAll(filteredRequests);
                    adapter.notifyDataSetChanged();
                    updateStats();
                } else {
                    Toast.makeText(getContext(), "검색에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<List<ConsultationRequest>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(getContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    
    private void onConsultationRequestClick(ConsultationRequest request) {
        // 상담 신청 상세 페이지로 이동
        ConsultationRequestDetailFragment detailFragment = ConsultationRequestDetailFragment.newInstance(request);
        
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, detailFragment)
                .addToBackStack(null)
                .commit();
        }
    }
    
    
    private void showLoading(boolean show) {
        layoutLoading.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
    
    private void updateStats() {
        if (consultationRequests.isEmpty()) {
            tvStats.setText("상담 신청이 없습니다.");
        } else {
            tvStats.setText(String.format("전체: %d개", consultationRequests.size()));
        }
    }
}
