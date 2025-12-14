## 평가 계획

### 목표
- OpenAI 건강분석 응답을 라벨 데이터(PHQ-9 기반)로 정량 평가

### 타깃 필드와 지표
- riskLevel: 다중분류 정확도, macro-F1
- needsAttention: 이진 정확도, F1
- concerns/recommendations: 키워드 정합(P/R@k, Jaccard)
- overallAssessment/detailedAnalysis: 임베딩 유사도(cosine), 보조로 ROUGE
- 구조화 신뢰도: JSON 파싱 성공률, 필드 누락률

### 라벨 소스/매핑
- NHANES DPQ 총점/중증도 → riskLevel 매핑
- needsAttention: 총점 임계값(예: ≥10) 또는 `DPQ100`(기능장애) 동시 고려

### 데이터 분할
- 80:20 계층화 분할(중증도 기준)
- 반복 평가: 시드 고정, 3회 평균

### 베이스라인
- 규칙 기반(약복용 누락+건강악화 등)과 비교

### 품질관리
- 결측/특수코드 정리, 가중치 설계는 분석 목적에 맞춰 선택적 적용


