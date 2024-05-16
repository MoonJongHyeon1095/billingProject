# BillingProject
[**📚 Notion**](https://foggy-unicorn-28d.notion.site/BillingProject-2024-0e94e88952b24e95a99bb799d2377008?pvs=4) |
**Apr 2024 ~ May 2024**

<br>

## 🌱 프로젝트 소개

---
- 대량의 영상 시청기록에 대한 통계 및 정산 Batch 작업
- 부하분산 및 장애복구 기능을 포함한 MSA 구조

<br>

## 🛠️ 주요 기능  

---
1. 통계 및 정산 기능  
   a. 2대의 Batch 서버에서 동시 작업  
   b. 각 Batch 서버에서 [가상스레드](#가상스레드)를 활용  
   c. [DB Partitioning](#DB-Partitioning)  
   d. [chunk read 동시성 제어](#chunk-read-동시성-제어)


2. 부하분산 및 장애복구 기능  
   a. 도메인 별 서버 분리  
   b. [로드밸런싱](#로드밸런싱)  
   c. [장애복구](#장애복구)  
   d. [CQRS](#CQRS)


3. [관련 API 기능](#관련-API-기능)

<br>

## 🚀 아키텍처

---
![무제 001](https://github.com/MoonJongHyeon1095/billingProject/assets/109948801/869b42b7-7259-4f24-bdcf-024cd4e26863)
<br>

## 📚 기술적 의사결정

---
#### 가상스레드
- [Batch 작업 멀티스레드 도입 : 플랫폼 스레드 vs 가상스레드](https://www.notion.so/BillingProject-2024-0e94e88952b24e95a99bb799d2377008?pvs=4#497977a420de41c28c33f17145619643)
#### DB Partitioning
- [수억건의 시청기록 통계 및 정산 : 날짜별 파티션 vs 인덱스 일치검색](https://www.notion.so/BillingProject-2024-0e94e88952b24e95a99bb799d2377008?pvs=4#f18414da6c1b447a89ca2a715221aa39)

<br>

## 🕹️ 성능 개선

---
- [SpringBatch: 전역 캐시 객체 사용, 통계 작업  22s458ms → 3s402ms ](https://www.notion.so/BillingProject-2024-0e94e88952b24e95a99bb799d2377008?pvs=4#4c47024cb04e4985b5009752f4aa2265)
- [SpringBatch: 가상 스레드 활용, 플랫폼 스레드 대비 CPU Load 약 10% 절약, 수행시간 약 24% 단축](https://www.notion.so/BillingProject-2024-0e94e88952b24e95a99bb799d2377008?pvs=4#f6921637fe124560939036ae9dfdb300)

<br>

## 🐞 트러블 슈팅

---
#### chunk read 동시성 제어
- [SpringBatch: chunk read 동시성 제어(JdbcPagingItemReader)](https://foggy-unicorn-28d.notion.site/SpringBatch-chunk-read-JdbcPagingItemReader-sortKey-df25e96ae7c2494891bfc039b79592ab?pvs=4)
#### VirtualThread 관련
- [VirtualThread pinned issue](https://foggy-unicorn-28d.notion.site/Virtual-Thread-Pinned-Issue-59caf6e9dd784700bb84b4e6514bb564?pvs=4)

<br>

## 🛠 기능구현 요약

---
#### 로드밸런싱
- 부하가 큰 일부 서비스에 대해 다수 인스턴스 부하분산 기능 구현
- Spring Cloud Gateway &  LoadBalancer & Eureka 활용
#### 장애복구
- Feign Client로 통신하는 일부 서버에 대해 회복 탄력성 및 예비 서버로의 라우팅 기능 구현
- resilience4J CircuitBreaker 활용
  <details><summary>상세 파라미터</summary>  

    - 최근 100번의 호출에 대해 50% 실패하면 Circut Breaker 개방
    - 개방 상태에서 10초 대기 후 반개방 상태로 전환
    - 반개방 상태에서 허용되는 호출횟수 3회 제한, 복구 가능성 평가

  ```
  resilience4j.circuitbreaker:
  instances:
    adFeignClient:
      registerHealthIndicator: true
      slidingWindowSize: 100
      minimumNumberOfCalls: 10
      permittedNumberOfCallsInHalfOpenState: 3
      automaticTransitionFromOpenToHalfOpenEnabled: true
      waitDurationInOpenState: 10s
      failureRateThreshold: 50
      eventConsumerBufferSize: 10
  ```

  </details>

#### CQRS
- DB main - replica 구조 적용
- main ec2에서 **다른 2대의 ec2 인스턴스로 MySQL replication**

#### 관련 API 기능
- 일간, 주간, 월간 조회수 및 재생시간 Top5 조회
- 일간, 주간, 월간 사용자 정산내역 조회
- 주간 Top5 정보 및 주간 사용자 정산내역 pdf 형태로 메일발송