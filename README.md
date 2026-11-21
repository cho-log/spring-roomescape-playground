# SpringMVC 

## 패키지 구조
```aiignore
src
├── main
│   ├── java
│   │   └── roomescape
│   │       ├── RoomescapeApplication.java           # 애플리케이션 실행
│   │       ├── controller
│   │       │   ├── HomeViewController.java          # 홈 화면 반환
│   │       │   ├── ReservationController.java       # 예약 조회, 생성, 삭제 API
│   │       │   └── ReservationViewController.java   # 예약 관리 화면 반환
│   │       ├── domain
│   │       │   └── Reservation.java                 # 예약 정보
│   │       ├── dto
│   │       │   ├── ErrorResponse.java               # 예외 응답 데이터
│   │       │   └── ReservationRequest.java          # 예약 생성 요청 데이터
│   │       ├── exception
│   │       │   ├── DuplicateReservationException.java  # 중복 예약 예외
│   │       │   ├── GlobalExceptionHandler.java         # 예외별 HTTP 응답 처리
│   │       │   ├── InvalidReservationException.java    # 유효하지 않은 예약 예외
│   │       │   ├── PastReservationException.java       # 과거 예약 예외
│   │       │   └── ReservationNotFoundException.java   # 존재하지 않는 예약 예외
│   │       ├── repository
│   │       │   └── ReservationRepository.java       # DB 예약 저장, 조회, 삭제
│   │       └── service
│   │           └── ReservationService.java          # 예약 검증 및 비즈니스 로직
│   └── resources
│       ├── application.properties                   # H2 데이터베이스 설정
│       ├── schema.sql                               # 예약 테이블 생성
│       └── templates
│           ├── home.html                            # 홈 화면
│           └── reservation.html                     # 예약 관리 화면
└── test
    └── java
        └── roomescape
            ├── MissionStepTest.java                # 미션 단계별 통합 테스트
            ├── controller
            │   └── ReservationControllerTest.java   # 예약 API 테스트
            ├── domain
            │   └── ReservationTest.java             # 예약 도메인 테스트
            ├── repository
            │   └── ReservationRepositoryTest.java   # DB 접근 테스트
            └── service
                └── ReservationServiceTest.java      # 예약 서비스 테스트
```

## 1단계 - 홈 화면
- `localhost:8080` 요청 시 어드민 메인 페이지 응답
- 어드민 메인 페이지는 `templates/home.html` 파일 사용

## 2단계 - 예약 조회
- `/reservations` 요청 시 예약 관리 페이지 응답
- 어드민 메인 페이지는 `templates/reservation.html` 파일 사용
- 예약 목록 조회 API 구현
- API 명세에 맞춰서 구현
  - Request 
    ```
    GET /reservations HTTP/1.1
    ```
  - Response
    ```
    HTTP/1.1 200 
    Content-Type: application/json
      
    [
      {
        "id": 1,
        "name": "브라운",
        "date": "2023-01-01",
        "time": "10:00"
      },
      {
        "id": 2,
        "name": "브라운",
        "date": "2023-01-02",
        "time": "11:00"
      }
    ]
    ```

## 3단계 - 예약 추가 / 취소
- 예약 추가 API 구현
  - Request
    ```aiignore
    POST /reservations HTTP/1.1
    content-type: application/json
    
    {
      "date": "2023-08-05",
      "name": "브라운",
      "time": "15:40"
    }
    ```
  - Response
    ```
    HTTP/1.1 201 
    Location: /reservations/1
    Content-Type: application/json
    
    {
      "id": 1,
      "name": "브라운",
      "date": "2023-08-05",
      "time": "15:40"
    }
    ```
- 예약 취소 API 구현
  - Request
    ```
    DELETE /reservations/1 HTTP/1.1
    ```
  - Response
    ```
    HTTP/1.1 204 No Content
    ``` 

## 4단계 - 예외 처리
- 예약 요청 데이터의 유효성을 검증하고, 잘못된 요청에 대해 적절한 HTTP 상태 코드 반환
- `GlobalExceptionHandler`를 통해 예외를 처리하고 일관된 오류 응답 제공
- 이름이 누락되거나 빈 문자열, 공백인 경우 -> 400 Bad Request
- 날짜 또는 시간이 누락되거나 빈 문자열인 경우 -> 400 Bad Request
- 날짜 또는 시간의 형식이나 값이 유효하지 않은 경우 -> 400 Bad Request
- 삭제할 예약의 식별자로 저장된 예약을 찾을 수 없는 경우 -> 404 Not Found

#### 예외 처리 기준
- 예약자 이름이 누락되거나 빈 문자열 또는 공백인 경우 -> 400 Bad Request
- 날짜 또는 시간이 누락되거나 비어있는 경우 -> 400 Bad Request
- 날짜 또는 시간의 형식이나 값이 올바르지 않은 경우 -> 400 Bad Request
- 삭제하려는 예약이 존재하지 않는 경우 -> 404 Not Found
- 과거 날짜로 예약 하는 경우 -> 400 Bad Request
- 이미 예약된 날짜와 시간에 예약하는 경우 -> 400 Bad Request

## 5단계 - 데이터베이스 적용하기
- 기존 메모리 기반 데이터 저장 방식에서 H2 데이터베이스를 사용하는 구조로 전환하기 위한 환경 구성
- `build.gradle`에 `spring-boot-starter-jdbc`와 h2 의존성 추가
- `application.properties`에 H2 데이터베이스 연결 정보 설정
- `schema.sql`을 작성하여 애플리케이션 실행 시 예약 테이블이 생성되도록 구성

#### 테이블 스키마
```aiignore
CREATE TABLE reservation
(
    id   BIGINT       NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    date DATE         NOT NULL,
    time TIME         NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uq_reservation_date_time UNIQUE (date, time)
);
```

## 6단계 - 데이터 조회하기
- 기존 메모리 기반 예약 조회 로직을 데이터베이스 조회 방식으로 변경
- `JdbcTemplate`의 `query()` 메서드로 예약 목록 조회
- 조회 결과를 `Reservation` 객체로 변환하여 반환

## 7단계 - 데이터 추가/삭제하기
- 예약 생성 및 취소 기능을 데이터베이스 기반으로 변경
- 기존 메모리 저장에 사용하던 List와 동시성 제어를 위한 synchronized 제거
- `SimpleJdbcInsert`를 사용해 예약 정보를 저장하고 자동 생성된 ID 반환
- `JdbcTemplate`의 `update()` 메서드를 사용해 예약 삭제
