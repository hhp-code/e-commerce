### K6 및 부하테스트

부하테스트 설계
K6를 사용한 부하 테스트 및 성능 분석 계획을 다음과 같이 수립할 수 있습니다:

## K6를 활용한 부하 테스트 계획

1. **테스트 대상 선정**
    - 주문 결제 api 
    - 인기 상품조회 api

2. **테스트 목적**
    - 시스템의 최대 처리 용량 파악
    - 응답 시간 및 처리량 측정
    - 병목 지점 식별

3. **시나리오 작성**
    - 일반 부하: 가상 사용자 100명
    - 중간 부하: 가상 사용자 500명
    - 최대 부하: 가상 사용자 1000명

## K6 테스트 스크립트 작성 및 실행
`

1. **다양한 부하 시나리오 실행**
    - 단계적 부하 증가 (Ramping VUs)
    - 스파이크 테스트
    - 지속 부하 테스트


## 성능 지표 분석 및 병목 탐색

1. **K6 결과 분석**
    - 응답 시간 (Response Time)
    - 요청 처리량 (Requests per Second)
    - 오류율 (Error Rate)

2. **추가 모니터링 도구 연동**
    - Grafana K6 대시보드 활용
    - Prometheus와 연동하여 시스템 메트릭 수집

3. **병목 지점 식별 및 개선**
    - 데이터베이스 쿼리 최적화
    - 캐시 도입 검토
    - 비동기 처리 적용 가능성 탐색

## 장애 대응 문서 작성

1. **K6 테스트 중 발견된 장애 시나리오**
    - 특정 부하에서의 응답 시간 급증
    - 오류율 증가 지점
    - 시스템 리소스 포화 상황

2. **대응 절차**
    - 모니터링 알림 설정
    - 부하 분산 전략 (로드 밸런싱)
    - 자동 스케일링 설정

## 최종 발표 자료 작성

1. **K6 테스트 결과 요약**
    - 주요 성능 지표 그래프 (K6 대시보드 활용)
    - 병목 지점 및 개선 사항 제시

2. **시스템 아키텍처 개선안**
    - K6 테스트 결과를 바탕으로 한 확장성 개선 제안

3. **향후 계획**
    - 지속적인 성능 테스트 자동화 방안
    - K6와 CI/CD 파이프라인 통합 계획

