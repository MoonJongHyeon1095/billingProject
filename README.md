# BillingProject
[**📚 Notion**](https://foggy-unicorn-28d.notion.site/BillingProject-2024-0e94e88952b24e95a99bb799d2377008?pvs=4) |
**Apr 2024 ~ May 2024**

<br>

## 🌱 프로젝트 소개
- 대량의 영상 시청기록에 대한 통계 및 정산 Batch 작업
- 부하분산 및 장애복구 기능을 포함한 MSA 구조

<br>

## 🛠️ 주요 기능
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
![무제 001](https://github.com/MoonJongHyeon1095/billingProject/assets/109948801/869b42b7-7259-4f24-bdcf-024cd4e26863)
<br>

## 📚 기술적 의사결정
### 가상스레드
[Batch 작업 멀티스레드 도입 : 플랫폼 스레드 vs 가상스레드](https://foggy-unicorn-28d.notion.site/Batch-vs-f4520db77a054b81bee6b2210e620c45?pvs=4)
### DB Partitioning
[수억건의 시청기록 통계 및 정산 : 날짜별 파티션 vs 인덱스 일치검색](https://foggy-unicorn-28d.notion.site/vs-43cf5d57050c4fb8a45b0f2db5bfe4d3?pvs=4)

<br>

## 🕹️ 성능 개선  
### Batch 작업: **가상스레드 도입 성능개선**  
<details><summary>I/O 집약적인 프로젝트의 특징에 따른 가설
</summary>

가설 1. 이 프로젝트는 CPU 사용에 있어 가상 스레드가 우위를 보일 것이다. 

가설 2. 이 프로젝트는 메모리 사용에 있어 가상 스레드가 우위를 보일 것이다.  

가설 3. 이 프로젝트는 처리량(동일 작업에 대한 Batch 작업 수행시간으로 측정)에 있어 가상 스레드가 우위를 보일 것이다.
</details>
<details>
<summary>검증  및 성능개선 : 플랫폼 스레드 대비 CPU Load 약 10% 절약 & 수행시간 약 24% 단축
</summary>  

1. jdk.CPULoad의 JVM User 지표 : 가상스레드가 유의미한 우위를 보임
2. JVM Heap 사용량 : 가상스레드가 전반적으로 더 많이 사용
3. 처리량 : 가상스레드가 전반적으로 더 빠른 시간 내에 작업 처리
4. 하루 시청기록 300만건, chunkSize 2000일 때의 비교

   |   | jdk.CPULoad <br/>JVM User | JVM Heap used      | Batch job compeleted       |
   |---|---------------------------|--------------------|----------------------------|
   | Platform Thread  | 52.2 % | 26.8 MiB – 137 MiB | 약 9~11초 (9s56ms, 10s922ms) |
   | Virtual Thread  | 42.8 % | 27.5 MiB – 190 MiB | 약 8~9초 (8s308ms, 8s832ms)  |


</details>

상세기록 : [Batch: 가상 스레드 성능개선](https://foggy-unicorn-28d.notion.site/Batch-3294e9ced0eb42cf8d88c711811f4235?pvs=4)



### Batch 작업: 전역 캐시객체 사용 성능개선
<details><summary>통계작업 처리시간 단축 : 22s458ms → 3s402ms
</summary>

1. Singleton 패턴의 전역 캐시객체 도입  
2. processor 삭제 & writer 단계 전역 캐시 저장
3. JobListener 활용, JobScope로 DB 갱신 작업  
4. 총 100만 행의 테이블, chunk size 20일 때 일간 통계 **22s458ms → 3s402ms**

</details>

상세기록 : [전역 캐시도입 성능개선](https://foggy-unicorn-28d.notion.site/Batch-bf180a139be045a8bb2dc42321331973?pvs=4)

<br>

## 🐞 트러블 슈팅
### chunk read 동시성 제어  
<details>
<summary>복합적인 문제원인
</summary>

1. 날짜별 DB Partition으로 인해 **auto increment PK 사용 불가**  
2. chunk read 동시성 처리를 위한 새로운 sort 방식 필요  
      a. chunk paging을 위한 추가정렬 : **FileSort 발생시 성능 대폭 저하**  
      b. sort의 기준 칼럼이 unique 하지 않을 경우 잘못된 통계 결과 산출
</details>

<details>
<summary>해결방법
</summary>

1. chunk paging을 위한 별도의 정렬 칼럼 **인덱스 별도 생성 및 쿼리 최적화**
2. 사용중인 인터페이스(JdbcPagingItemReader)가 **off-set 방식의 페이징을 하지 않는 것**을 확인
</details>  

상세 기록 : [SpringBatch: chunk read 동시성 제어(JdbcPagingItemReader)](https://foggy-unicorn-28d.notion.site/SpringBatch-chunk-read-JdbcPagingItemReader-sortKey-df25e96ae7c2494891bfc039b79592ab?pvs=4)

### VirtualThread pinned issue  
<details>
<summary>문제원인
</summary>

1. synchronized 블록 안에서 `VirtualThread.park()` 가 발생하면 가상스레드는 CarrierThread에서 unmount 되지 않는다.
2. MySQL JDBC 연결은 synchronized 키워드로 구현된 부분이 많다.
</details>
<details>
<summary>대안모색 및 결정
</summary>

1. MySQL R2DBC 연결과 대조    
2. MariaDB JDBC 연결과 대조
3. 대조결과  
a. 현재의 환경에서 **MariaDB나 R2DBC를 통해 Virtual Thread Pinned가 눈에 띄게 감소하는 일은 없었다.**  
b. **Virtual Thread Pinned 지표와 성능(수행시간, CPU부하 등) 사이에도 유의미한 관계는 발견되지 않았다.**
4. 기존의 JDBC 기반 Batch 작업 유지
</details>

상세 기록: [VirtualThread pinned issue](https://foggy-unicorn-28d.notion.site/Virtual-Thread-Pinned-Issue-59caf6e9dd784700bb84b4e6514bb564?pvs=4)

<br>

## 🛠 기능구현 요약
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