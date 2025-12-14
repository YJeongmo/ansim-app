## 연구 자료 개요

- **목적**: OpenAI 기반 건강분석 응답의 정확도/안정성 평가용 데이터·문서·노트북 허브
- **범위**: CDC NHANES DPQ(PHQ-9) 라벨을 활용한 평가. 연구는 `back/research` 내부에서만 수행

### 디렉터리 구조
```
back/research/
├── README.md                    # 개요 문서(본 파일)
├── data/                        # 원시/중간 데이터 보관(대용량은 Git LFS/외부 스토리지 권장)
│   └── .gitkeep
├── docs/
│   ├── nhanes_dpq_codebook_ko.md# NHANES DPQ 코드북 요약(KO)
│   ├── evaluation_plan.md       # 평가 지표/라벨 매핑/프로토콜
│   └── data_sources.md          # 데이터 소스/접근 절차/링크
└── notebooks/                   # 분석 노트북
```

### DPQ (Depression Screening Questionnaire)
#### 개요
- DPQ는 CDC NHANES에서 사용하는 변수명으로, 실제로는 **PHQ-9 (Patient Health Questionnaire-9)**와 동일한 우울증 선별 도구입니다.

#### 측정 방식

- 항목 수: 9개 증상 질문
- 평가 기간: 지난 2주간
- 점수 범위: 각 항목 0-3점 (총 0-27점)

```
응답 범주:

0: 전혀 그렇지 않다
1: 며칠 동안
2: 일주일 이상
3: 거의 매일
```


#### 점수 계산
- 총점 = DPQ010 + DPQ020 + DPQ030 + DPQ040 + DPQ050 + DPQ060 + DPQ070 + DPQ080 + DPQ090

#### 심각도 분류
- 점수 범위우울증 심각도1-4점최소한의 우울증5-9점경미한 우울증10-14점중등도 우울증15-19점중등도-심한 우울증20-27점심한 우울증

#### 추가 평가

- 증상이 하나라도 있는 경우 기능적 손상 정도 평가 (DPQ100)
- 자살 사고 관련 항목 (DPQ090) 주의 필요

### 작업 원칙
- 개인식별정보(PII) 금지, 공개/승인 데이터만 사용
- 원본 불변성 유지(원시 데이터 read-only), 파생물은 버전 구분
- 라벨 규칙/변환 로직은 문서화하고 재현 가능하게 유지

### 우선 데이터셋
- NHANES DPQ(PHQ-9): 즉시 다운로드 가능, 조사원 프로토콜 기반 품질 보장
- CMS MDS 3.0 RIF: 요양원 도메인 최적합(승인 필요)
- KNHANES: 국내 맥락 정합(신청 필요)

### 빠른 시작(로딩→라벨→분할)
1) 데이터 배치: `back/research/data/DPQ_L.xpt`
2) 노트북 실행: `back/research/notebooks/nhanes_dpq_quickstart.ipynb`
   - DPQ 로딩 → PHQ‑9 총점/중증도/needsAttention 생성
   - 재현성 해시 기록 → 견고한(계층 보강) 분할 80:20 → `outputs/splits/{train,test}.csv` 저장
3) 매핑 정책/세부 규칙은 `docs/label_mapping_policy.md` 참조


### 예측·평가(요약)
1) 예측 수집(OpenAI): 테스트셋 각 `SEQN`의 9문항을 프롬프트로 호출 → `outputs/predictions/test_predictions.csv`
   - 스키마: `SEQN`, `pred_riskLevel{none|mild|moderate|moderately_severe|severe}`, `pred_needsAttention{true|false}`
   - 샘플: `outputs/predictions/test_predictions.sample.csv`
   - 권장 설정: temperature 0.2–0.3, JSON 응답 강제, 동일 시스템 프롬프트
2) 평가 실행: 노트북 하단 평가 셀 → accuracy, macro‑F1, 혼동행렬(riskLevel), accuracy/F1(needsAttention)

### 주의: 지표가 1.0으로 나오는 이슈
- 현상: accuracy/F1이 1.0으로 과도하게 높게 나오는 경우가 있음
- 원인 추정:
  - (1) 베이스라인 파일(`test_predictions.csv`)을 평가 입력으로 사용하여 정답과 동일한 파일을 비교
  - (2) 프롬프트에 PHQ‑9 총점/중증도 힌트가 포함되어 정답 누설 발생
  - (3) PHQ‑9 severity/needsAttention는 규칙으로 완전 결정 가능(LLM과 동일 결과 가능)
- 조치/해결:
  - 평가 입력을 반드시 `test_predictions.model.csv`(모델 예측)로 사용하고, 베이스라인은 `test_predictions.baseline.csv`로 분리
  - 프롬프트는 PHQ‑9 9문항(Q1~Q9)만 포함(총점/중증도/임계값 미포함)
  - LLM 평가는 생성형 목표(예: concerns/recommendations/요약)로 확장하고 해당 지표를 추가
  - 추가 데이터셋 확보로 일반화 검증 강화(아래 “데이터 확장” 참조)

### 데이터 확장 제안(일반화 검증)
- NHANES 외 다주기 결합 또는 다른 코호트 병행: KNHANES(신청형), UK Biobank/All of Us(승인형)
- 요양원 도메인 정합성 강화를 위해 CMS MDS 3.0 RIF(승인형) 신청
- 텍스트 기반 임상 노트(비식별)로 concerns/recommendations 평가 세트 구축

### 환경 변수(필수)
```
OPENAI_API_KEY=...
OPENAI_API_URL=https://api.openai.com/v1/chat/completions
```

정책/절차 상세는 다음 문서를 참조하십시오:
- `docs/evaluation_protocol.md` (SOP)
- `docs/label_mapping_policy.md` (라벨/임계값/결측)
- `docs/metrics_reporting.md` (지표/리포팅)
- `docs/data_governance.md` (거버넌스)


