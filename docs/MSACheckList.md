# 마이크로서비스 전환 체크리스트
문서 정리는 /docs/develop 에 정리되어있습니다.
## Step 1: 현재 시스템 분석
- [ ] 현재 코드베이스 분석
- [ ] 의존성 및 결합도 파악

## Step 2: 서비스 트랜잭션 경계 정의
- [ ] 주문, 상품, 사용자 서비스 경계 명확히 구분
- [ ] 각 서비스의 책임 정의

## Step 3: 공통 코드 추출
- [ ] 공유 라이브러리로 분리할 코드 식별
- [ ] 공통 유틸리티 및 모델 클래스 추출

## Step 4: 데이터베이스 분리 준비
- [ ] 각 서비스별 독립 스키마 설계
- [ ] 데이터 마이그레이션 계획 수립

## Step 5: API 게이트웨이 도입
- [ ] API 게이트웨이 설계 및 구현
- [ ] 라우팅 및 인증 로직 구현

## Step 6: 이벤트 기반 아키텍처 도입
- [ ] 메시지 브로커 선택 (예: Kafka, RabbitMQ)
- [ ] 주요 도메인 이벤트 정의 및 구현

## Step 7: 서비스 간 통신 구현
- [ ] REST 또는 gRPC 기반 통신 구현
- [ ] 서비스 디스커버리 메커니즘 도입

## Step 8: 분산 트랜잭션 처리
- [ ] Saga 패턴 구현
- [ ] 보상 트랜잭션 로직 추가

## Step 9: 모니터링 및 로깅
- [ ] 분산 로깅 시스템 구축
- [ ] 모니터링 도구 통합 (예: Prometheus, Grafana)

## Step 10: 점진적 마이그레이션
- [ ] 서비스별 단계적 분리 및 배포
- [ ] 기존 시스템과의 병행 운영 전략 수립
