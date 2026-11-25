# 방탈출 예약 관리

## 기능 요구사항

### 메인 페이지

- `localhost:8080` 요청 시 메인 페이지를 응답한다.
- 메인 페이지는 `templates/home.html` 파일을 이용한다.

### 예약 관리 페이지

- `/reservation` 요청 시 예약 관리 페이지를 응답한다.
- 예약 관리 페이지는 `templates/reservation.html` 파일을 이용한다.

### 데이터베이스

- h2 데이터베이스를 활용하여 데이터를 저장한다.
- `spring-boot-starter-jdbc`, `h2` 의존성을 추가한다.
- `schema.sql`에 예약 테이블을 생성하는 쿼리를 작성한다.
- h2 데이터베이스의 console 기능을 활성화한다.
- datasource url을 `jdbc:h2:mem:database`로 지정한다.

#### 예약 조회

- 예약 조회 API는 데이터베이스에 저장된 예약을 조회한다.

#### 예약 추가·삭제

- 예약 추가·삭제 API는 데이터베이스를 활용한다.
- 기존에 사용하던 `List`와 `AtomicLong`을 제거한다.
- 예약 추가 시 데이터베이스가 생성한 id로 응답한다.

### 예외 처리

#### 예약 추가
- 이름, 날짜, 시간 중 하나라도 비어 있거나 없으면 '400 Bad Request' 를 응답한다.
- 지난 날짜·시간으로 예약하면 '400 Bad Request' 를 응답한다.
- 같은 날짜와 시간에 이미 예약이 있으면 '409 Conflict' 를 응답한다.
#### 예약 삭제
- 존재하지 않는 예약 ID 로 삭제하면 '404 Not Found' 를 응답한다.

## 파일 구조
```text
src
├── main
│   └── java
│       └── roomescape
│           ├── RoomescapeApplication.java
│           ├── controller
│           │   ├── GreetingViewController.java
│           │   ├── ReservationController.java
│           │   └── ReservationViewController.java
│           ├── dto
│           │   ├── ReservationRequest.java
│           │   └── ReservationResponse.java
│           ├── exception
│           │   ├── DuplicateReservationException.java
│           │   ├── ErrorResponse.java
│           │   ├── GlobalExceptionHandler.java
│           │   ├── InvalidReservationException.java
│           │   ├── NotFoundReservationException.java
│           │   └── PastReservationException.java
│           ├── model
│           │   └── Reservation.java
│           ├── repository
│           │   └── ReservationRepository.java
│           └── service
│               └── ReservationService.java
└── test
    └── java
        └── roomescape
            ├── MissionStepTest.java
            ├── model
            │   └── ReservationTest.java
            └── service
                └── ReservationServiceTest.java
```
