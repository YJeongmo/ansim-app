# CodeRelief - 노인 요양원 관리 시스템

## 📋 프로젝트 개요
노인 요양원을 위한 종합 관리 시스템으로, 환자 건강상태 모니터링, AI 기반 건강 분석, 알림 시스템 등을 제공합니다.

## 🏗️ 기술 스택

### 백엔드
- **Spring Boot**: 3.2.5
- **Java**: 17
- **데이터베이스**: MySQL 8.0
- **마이그레이션**: Flyway 9.22.3
- **AI 모델**: GPT-5-mini (OpenAI)

### 프론트엔드
- **Android**: 네이티브 앱
- **네트워킹**: Retrofit + OkHttp
- **UI**: Material Design

## 🚀 주요 기능

### 1. 환자 관리
- 환자 기본 정보 관리
- 건강상태 기록 관리
- 일별 활동 기록

### 2. AI 건강 분석
- **GPT-5-mini 기반**: 비용 효율적인 건강상태 분석
- **실시간 분석**: 환자 데이터 기반 위험도 평가
- **캐싱 시스템**: 동일 요청에 대해 즉시 응답
- **자동 재시도**: 네트워크 오류 시 자동 복구

### 3. 알림 시스템
- 건강상태 주의 알림
- 예약 관련 알림
- 실시간 알림 배송

### 4. 예약 관리
- 방문 예약 시스템
- 승인/거부 관리
- 일정 관리

## 📊 성능 최적화

### GPT-5-mini 최적화
- **비용 절약**: 95% 이상 (GPT-4 대비)
- **응답 속도**: 캐시된 요청 < 1초
- **분석 품질**: medium verbosity + reasoning

### 캐싱 시스템
- **메모리 기반**: 24시간 TTL
- **캐시 크기**: 최대 100개 응답
- **고유 키**: SHA-256 해시 기반

### 네트워크 최적화
- **타임아웃 설정**: 연결 30초, 읽기 2분
- **재시도 로직**: 최대 3회, 지수 백오프
- **안정성**: 자동 오류 복구

## 🛠️ 설치 및 실행

### 백엔드 실행
```bash
cd back
./gradlew bootRun
```

### Android 앱 빌드
```bash
cd front
./gradlew assembleDebug
```

### 데이터베이스 설정
```sql
-- MySQL 데이터베이스 생성
CREATE DATABASE ansim_yoyang;
CREATE USER 'appuser'@'localhost' IDENTIFIED BY '1111';
GRANT ALL PRIVILEGES ON ansim_yoyang.* TO 'appuser'@'localhost';
```

## 📁 프로젝트 구조

```
codeRelief-0914_notification/
├── back/                          # Spring Boot 백엔드
│   ├── src/main/java/
│   │   └── com/ansimyoyang/
│   │       ├── service/           # 비즈니스 로직
│   │       │   └── OpenAIService.java  # AI 분석 서비스
│   │       ├── controller/        # REST API 컨트롤러
│   │       ├── domain/           # 엔티티 및 DTO
│   │       └── repository/       # 데이터 접근 계층
│   ├── src/main/resources/
│   │   └── db/migration/         # Flyway 마이그레이션
│   └── build.gradle
├── front/                         # Android 앱
│   ├── app/src/main/java/
│   │   └── com/example/coderelief/
│   │       ├── api/              # API 클라이언트
│   │       │   └── ApiClient.java
│   │       └── ui/               # UI 컴포넌트
│   └── build.gradle
├── PERFORMANCE_OPTIMIZATION.md    # 성능 최적화 문서
├── CHANGELOG.md                   # 변경 이력
└── README.md                      # 프로젝트 문서
```

## 🔬 연구/평가(요약)
- OpenAI 예측 정확도 평가는 `back/research` 디렉터리에서 독립적으로 수행합니다.
- NHANES DPQ(PHQ‑9) 라벨로 분할/예측/평가를 진행하며, 상세 절차는 `back/research/README.md`를 참고하세요.
 - 참고: PHQ‑9 기반 분류는 규칙으로 완전 결정 가능한 항목이 있어 지표가 1.0으로 과대평가될 수 있습니다. 일반화 검증 강화를 위해 추가 데이터셋(예: KNHANES, CMS MDS 3.0 RIF, 승인형 코호트) 수집·평가를 병행합니다.

## 🔧 설정

### 환경 변수
```properties
# OpenAI API 설정
openai.api.key=your_api_key_here
openai.api.url=https://api.openai.com/v1/chat/completions

# 데이터베이스 설정
spring.datasource.url=jdbc:mysql://localhost:3306/ansim_yoyang
spring.datasource.username=appuser
spring.datasource.password=1111
```

### Android 네트워크 설정
```java
// ApiClient.java
OkHttpClient client = new OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build();
```

## 📈 성능 지표

### API 응답 시간
- **신규 분석**: 15-30초
- **캐시된 요청**: < 1초
- **재시도 시**: 자동 복구

### 비용 효율성
- **GPT-4**: $0.09-0.18 (1,000 토큰)
- **GPT-5-mini**: $0.002-0.004 (1,000 토큰)
- **절약률**: 95% 이상

## 🐛 알려진 이슈

1. **포트 충돌**: 8080 포트가 이미 사용 중일 때 발생
   - 해결: `pkill -f "ansim-yoyang"` 후 재시작

2. **Android APK 로딩 오류**: 권한 문제
   - 해결: `chmod +x ./gradlew` 후 재빌드

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 `LICENSE` 파일을 참조하세요.

## 📞 연락처

- **프로젝트 링크**: [https://github.com/your-username/codeRelief-0914_notification](https://github.com/your-username/codeRelief-0914_notification)

---

**마지막 업데이트**: 2025-09-15  
**버전**: 1.0.0  
**상태**: ✅ 운영 중
