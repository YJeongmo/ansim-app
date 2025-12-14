## 지표 및 리포팅 기준

### 분류 지표
- riskLevel(다중분류): 정확도, macro-F1, 혼동행렬
- needsAttention(이진): 정확도, F1, 정밀도/재현율

### 산출 규칙
- 데이터 분할은 계층화(중증도 기준) 80:20, 시드 고정.
- 지표는 테스트셋 기준만 최종 보고. 개발 중간에는 학습셋 지표도 참고.

### 보고 템플릿
- 표1: 클래스 분포(학습/테스트)
- 표2: riskLevel 혼동행렬
- 표3: riskLevel 정확도/precision/recall/F1(macro)
- 표4: needsAttention 정확도/precision/recall/F1
- 부록: 입력 파일 해시, 분할 시드, 노트북/커밋 해시


