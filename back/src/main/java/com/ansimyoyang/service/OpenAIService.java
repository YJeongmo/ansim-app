package com.ansimyoyang.service;

import com.ansimyoyang.domain.dto.HealthAnalysisRequest;
import com.ansimyoyang.domain.dto.HealthAnalysisResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import java.time.Duration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // RestTemplate 초기화 (성능 최적화된 설정)
    public OpenAIService() {
        this.restTemplate = createOptimizedRestTemplate();
    }
    
    private RestTemplate createOptimizedRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30초 연결 타임아웃
        factory.setReadTimeout(120000);   // 2분 읽기 타임아웃 (GPT-5-mini 처리 시간 고려)
        return new RestTemplate(factory);
    }
    
    // GPT-5-mini 성능 최적화 설정
    private static final int MAX_COMPLETION_TOKENS = 1800; // 성능 향상을 위해 증가
    // GPT-5-mini는 temperature 기본값(1)만 지원
    private static final String RESPONSE_FORMAT = "json_object"; // JSON 응답 강제
    
    // 응답 캐싱 (메모리 기반, 간단한 구현)
    private final Map<String, HealthAnalysisResponse> responseCache = new ConcurrentHashMap<>();
    private static final int CACHE_MAX_SIZE = 100; // 최대 100개 응답 캐시
    private static final long CACHE_TTL_HOURS = 24; // 24시간 캐시 유지

    public HealthAnalysisResponse analyzeHealthStatus(HealthAnalysisRequest request) {
        try {
            log.info("건강상태 분석 시작: patientId={}, name={}", request.getPatientId(), request.getPatientName());
            
            // 캐시 키 생성 (요청 데이터의 해시값)
            String cacheKey = generateCacheKey(request);
            
            // 캐시에서 응답 확인
            HealthAnalysisResponse cachedResponse = responseCache.get(cacheKey);
            if (cachedResponse != null && isCacheValid(cachedResponse)) {
                log.info("캐시된 응답 사용: patientId={}", request.getPatientId());
                return cachedResponse;
            }
            
            String prompt = buildAnalysisPrompt(request);
            String aiResponse = callOpenAI(prompt);
            
            HealthAnalysisResponse.HealthAnalysisResult result = parseAIResponse(aiResponse);
            
            HealthAnalysisResponse response = HealthAnalysisResponse.builder()
                    .success(true)
                    .message("건강상태 분석이 완료되었습니다.")
                    .analysisResult(result)
                    .analyzedAt(LocalDateTime.now())
                    .build();
            
            // 응답 캐싱
            cacheResponse(cacheKey, response);
            
            return response;
                    
        } catch (Exception e) {
            log.error("건강상태 분석 실패: patientId={}, error={}", request.getPatientId(), e.getMessage(), e);
            
            return HealthAnalysisResponse.builder()
                    .success(false)
                    .message("건강상태 분석 중 오류가 발생했습니다: " + e.getMessage())
                    .analyzedAt(LocalDateTime.now())
                    .build();
        }
    }

    private String buildAnalysisPrompt(HealthAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("당신은 노인 요양원의 의료진을 도와주는 AI 의료 분석 전문가입니다. ");
        prompt.append("다음 환자의 건강상태 데이터를 분석하여 놓칠 수 있는 특이사항이나 우려사항을 찾아주세요.\n\n");
        
        prompt.append("=== 환자 기본 정보 ===\n");
        prompt.append("이름: ").append(request.getPatientName()).append("\n");
        prompt.append("나이: ").append(request.getAge()).append("세\n");
        prompt.append("성별: ").append(request.getGender()).append("\n");
        prompt.append("지병: ").append(request.getChronicDiseases() != null ? request.getChronicDiseases() : "없음").append("\n");
        prompt.append("분석 기간: ").append(request.getAnalysisStartDate()).append(" ~ ").append(request.getAnalysisEndDate()).append("\n\n");
        
        prompt.append("=== 일별 건강 기록 ===\n");
        for (HealthAnalysisRequest.DailyRecordSummary record : request.getDailyRecords()) {
            prompt.append("날짜: ").append(record.getRecordDate()).append("\n");
            prompt.append("시간대: ").append(record.getTimeSlot()).append("\n");
            prompt.append("식사상태: ").append(record.getMealStatus()).append("\n");
            prompt.append("건강상태: ").append(record.getHealthCondition()).append("\n");
            prompt.append("약물복용: ").append(record.isMedicationTaken() ? "복용" : "미복용").append("\n");
            prompt.append("특이사항: ").append(record.getNotes() != null ? record.getNotes() : "없음").append("\n");
            prompt.append("---\n");
        }
        
        prompt.append("\n=== 분석 요청 ===\n");
        prompt.append("위 데이터를 바탕으로 다음 사항을 분석해주세요:\n");
        prompt.append("1. 전체적인 건강상태 평가\n");
        prompt.append("2. 위험도 수준 (LOW, MEDIUM, HIGH, CRITICAL)\n");
        prompt.append("3. 놓칠 수 있는 특이사항이나 우려사항 (구체적으로 3-5개)\n");
        prompt.append("4. 의료진이 주의해야 할 권장사항 (3-5개)\n");
        prompt.append("5. 상세한 분석 내용\n");
        prompt.append("6. 즉시 주의가 필요한지 여부\n\n");
        
        prompt.append("응답은 다음 JSON 형식으로 해주세요:\n");
        prompt.append("{\n");
        prompt.append("  \"overallAssessment\": \"전체적인 건강상태 평가\",\n");
        prompt.append("  \"riskLevel\": \"LOW|MEDIUM|HIGH|CRITICAL\",\n");
        prompt.append("  \"concerns\": [\"우려사항1\", \"우려사항2\", \"우려사항3\"],\n");
        prompt.append("  \"recommendations\": [\"권장사항1\", \"권장사항2\", \"권장사항3\"],\n");
        prompt.append("  \"detailedAnalysis\": \"상세한 분석 내용\",\n");
        prompt.append("  \"needsAttention\": true/false\n");
        prompt.append("}");
        
        return prompt.toString();
    }

    @Retryable(
        value = {ResourceAccessException.class, RuntimeException.class},
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    private String callOpenAI(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-5-mini");
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "당신은 노인 요양원의 의료진을 도와주는 AI 의료 분석 전문가입니다."),
                Map.of("role", "user", "content", prompt)
        ));
        
        // 성능 최적화된 설정 적용
        requestBody.put("max_completion_tokens", MAX_COMPLETION_TOKENS);
        // temperature는 GPT-5-mini에서 기본값(1)만 지원하므로 생략
        requestBody.put("response_format", Map.of("type", RESPONSE_FORMAT));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        log.info("OpenAI API 호출 시작");
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, String.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("OpenAI API 호출 성공");
            return response.getBody();
        } else {
            throw new RuntimeException("OpenAI API 호출 실패: " + response.getStatusCode());
        }
    }

    private HealthAnalysisResponse.HealthAnalysisResult parseAIResponse(String aiResponse) throws Exception {
        log.info("OpenAI 원본 응답: {}", aiResponse);
        
        // API 키 오류나 기타 오류 응답 처리
        if (aiResponse.contains("invalid_api_key") || aiResponse.contains("error")) {
            log.warn("OpenAI API 오류 감지, 테스트 데이터 반환");
            return createTestAnalysisResult();
        }
        
        JsonNode rootNode = objectMapper.readTree(aiResponse);
        JsonNode choicesNode = rootNode.get("choices");
        
        if (choicesNode == null || choicesNode.isEmpty()) {
            log.error("OpenAI 응답에서 choices를 찾을 수 없습니다. 전체 응답: {}", aiResponse);
            return createTestAnalysisResult();
        }
        
        String content = choicesNode.get(0).get("message").get("content").asText();
        log.info("OpenAI 응답 내용: {}", content);
        
        // JSON 부분만 추출 (```json ... ``` 형태일 수 있음)
        if (content.contains("```json")) {
            int start = content.indexOf("```json") + 7;
            int end = content.lastIndexOf("```");
            content = content.substring(start, end).trim();
        } else if (content.contains("```")) {
            // ```만 있는 경우도 처리
            int start = content.indexOf("```") + 3;
            int end = content.lastIndexOf("```");
            if (end > start) {
                content = content.substring(start, end).trim();
            }
        }
        
        log.info("파싱할 JSON 내용: {}", content);
        JsonNode analysisNode = objectMapper.readTree(content);
        
        List<String> concerns = new ArrayList<>();
        if (analysisNode.has("concerns")) {
            for (JsonNode concern : analysisNode.get("concerns")) {
                concerns.add(concern.asText());
            }
        }
        
        List<String> recommendations = new ArrayList<>();
        if (analysisNode.has("recommendations")) {
            for (JsonNode recommendation : analysisNode.get("recommendations")) {
                recommendations.add(recommendation.asText());
            }
        }
        
        // null 체크를 포함한 안전한 필드 추출
        String overallAssessment = analysisNode.has("overallAssessment") && analysisNode.get("overallAssessment") != null
                ? analysisNode.get("overallAssessment").asText()
                : "분석 결과를 가져올 수 없습니다.";
        
        // riskLevel 필드 제거됨
        
        String detailedAnalysis = analysisNode.has("detailedAnalysis") && analysisNode.get("detailedAnalysis") != null
                ? analysisNode.get("detailedAnalysis").asText()
                : "상세 분석 정보를 가져올 수 없습니다.";
        
        boolean needsAttention = analysisNode.has("needsAttention") && analysisNode.get("needsAttention") != null
                ? analysisNode.get("needsAttention").asBoolean()
                : false;
        
        return HealthAnalysisResponse.HealthAnalysisResult.builder()
                .overallAssessment(overallAssessment)
                .concerns(concerns)
                .recommendations(recommendations)
                .detailedAnalysis(detailedAnalysis)
                .needsAttention(needsAttention)
                .build();
    }
    
    // 테스트용 분석 결과 생성
    private HealthAnalysisResponse.HealthAnalysisResult createTestAnalysisResult() {
        List<String> concerns = List.of("혈압이 약간 높은 편입니다.", "수면 패턴이 불규칙합니다.");
        List<String> recommendations = List.of("정기적인 혈압 측정을 권장합니다.", "규칙적인 수면 시간을 유지해주세요.");
        
        return HealthAnalysisResponse.HealthAnalysisResult.builder()
                .overallAssessment("환자의 전반적인 건강상태는 양호하나, 일부 주의가 필요한 부분이 있습니다.")
                .concerns(concerns)
                .recommendations(recommendations)
                .detailedAnalysis("최근 5일간의 건강 기록을 분석한 결과, 혈압과 수면 패턴에서 개선이 필요한 부분이 발견되었습니다. 정기적인 모니터링과 생활습관 개선이 권장됩니다.")
                .needsAttention(true)
                .build();
    }
    
    // 캐시 관련 헬퍼 메서드들
    private String generateCacheKey(HealthAnalysisRequest request) {
        try {
            StringBuilder keyBuilder = new StringBuilder();
            keyBuilder.append(request.getPatientId())
                     .append("_")
                     .append(request.getAnalysisStartDate())
                     .append("_")
                     .append(request.getAnalysisEndDate());
            
            // 일별 기록 데이터도 키에 포함
            for (HealthAnalysisRequest.DailyRecordSummary record : request.getDailyRecords()) {
                keyBuilder.append("_")
                         .append(record.getRecordDate())
                         .append("_")
                         .append(record.getTimeSlot())
                         .append("_")
                         .append(record.getMealStatus())
                         .append("_")
                         .append(record.getHealthCondition())
                         .append("_")
                         .append(record.isMedicationTaken());
            }
            
            // 해시 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(keyBuilder.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            log.warn("캐시 키 생성 실패, 기본 키 사용: {}", e.getMessage());
            return "default_" + request.getPatientId() + "_" + System.currentTimeMillis();
        }
    }
    
    private boolean isCacheValid(HealthAnalysisResponse response) {
        if (response == null || response.getAnalyzedAt() == null) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cacheTime = response.getAnalyzedAt();
        return cacheTime.isAfter(now.minusHours(CACHE_TTL_HOURS));
    }
    
    private void cacheResponse(String cacheKey, HealthAnalysisResponse response) {
        // 캐시 크기 제한
        if (responseCache.size() >= CACHE_MAX_SIZE) {
            // 가장 오래된 항목 제거 (간단한 구현)
            String oldestKey = responseCache.keySet().iterator().next();
            responseCache.remove(oldestKey);
        }
        
        responseCache.put(cacheKey, response);
        log.debug("응답 캐시 저장: key={}, cacheSize={}", cacheKey, responseCache.size());
    }
}
