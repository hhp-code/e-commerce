### K6 및 부하테스트

## K6를 활용한 부하 테스트 계획
1. ** 스펙 **
   - cpu : 2
   - memory : 4G
   - 도커로 띄운 어플리케이션에 대한 부하테스트

2. **테스트 대상 선정**
    - 주문 결제 api 
    - 인기 상품조회 api

3. **테스트 목적**
    - 시스템의 최대 처리 용량 파악
    - 응답 시간 및 처리량 측정
    - 병목 지점 식별
    - 200TPS 부하에 대한 안전성 보장

4. **시나리오 작성**
  - 부하 테스트
  - 내구성 테스트
  - 스트레스 테스트
  - 최고 부하 테스트

## K6 테스트 스크립트 작성 및 실행
`




## 성능 지표 분석 및 병목 탐색
1회 이터레이션 성공후 시나리오별 설정후 병렬 실행
데드락 발생 'User' 도메인 변경에 동시성이 생김
데드락 발생으로 인해 락 적용 후 재시도
IllegalStateException: Illegal pop() with non-matching JdbcValuesSourceProcessingState
NullPointerException: Cannot invoke "org.hibernate.engine.spi.SharedSessionContractImplement
문제 분석.. 하이버네이트의 스레드는 스레드세이프 하지 않다.
주문 결제 스레드에서 저장하는것에있어 유저도메인의 변경이 일어나는데 이때 데드락이 발생한다.
데드락 해결을 위해서 1가지 변경을 했습니다.
주문-> 유저 참조를 통해 변경을 하는것이 아니라 유저번호, 주문번호 및 유저번호를 통해 변경을 하도록 변경하겠습니다.
이로인해 데드락은 해결했지만, 2CPU 4G의 환경에서 50TPS를 처리하는데에는 문제가 있었습니다.
일단 도커에서 스펙업을 해서 4CPU 8G로 변경하겠습니다.
메트릭을 봐도 TPS의 문제보다는 서버가 죽는게 문제였습니다.


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


2024-08-20 09:16:56 org.springframework.dao.CannotAcquireLockException: could not execute statement [Deadlock found when trying to get lock; try restarting transaction] [/* update for com.ecommerce.domain.user.User */update users set deleted_at=?,is_deleted=?,point=?,username=? where id=?]; SQL [/* update for com.ecommerce.domain.user.User */update users set deleted_at=?,is_deleted=?,point=?,username=? where id=?]
2024-08-20 09:16:56     at org.springframework.orm.jpa.vendor.HibernateJpaDialect.convertHibernateAccessException(HibernateJpaDialect.java:283) ~[spring-orm-6.1.10.jar!/:6.1.10]
2024-08-20 09:16:56     at org.springframework.orm.jpa.vendor.HibernateJpaDialect.translateExceptionIfPossible(HibernateJpaDialect.java:244) ~[spring-orm-6.1.10.jar!/:6.1.10]
2024-08-20 09:16:56     at org.springframework.orm.jpa.JpaTransactionManager.doCommit(JpaTransactionManager.java:566) ~[spring-orm-6.1.10.jar!/:6.1.10]
2024-08-20 09:16:56     at org.springframework.transaction.support.AbstractPlatformTransactionManager.processCommit(AbstractPlatformTransactionManager.java:795) ~[spring-tx-6.1.10.jar!/:6.1.10]
2024-08-20 09:16:56     at org.springframework.transaction.support.AbstractPlatformTransactionManager.commit(AbstractPlatformTransactionManager.java:758) ~[spring-tx-6.1.10.jar!/:6.1.10]
2024-08-20 09:16:56     at org.springframework.transaction.interceptor.TransactionAspectSupport.commitTransactionAfterReturning(TransactionAspectSupport.java:676) ~[spring-tx-6.1.10.jar!/:6.1.10]
2024-08-20 09:16:56     at org.springframework.transaction.interceptor.TransactionAspectSupport.invokeWithinTransaction(TransactionAspectSupport.java:426) ~[spring-tx-6.1.10.jar!/:6.1.10]