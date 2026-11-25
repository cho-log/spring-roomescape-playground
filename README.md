# 방탈출 예약 관리

## 기능 요구사항

### 메인 페이지

- `localhost:8080` 요청 시 메인 페이지를 응답한다.
- 메인 페이지는 `templates/home.html` 파일을 이용한다.

### 예약 관리 페이지

- `/reservation` 요청 시 예약 관리 페이지를 응답한다.
- 예약 관리 페이지는 `templates/reservation.html` 파일을 이용한다.

### 예약 API

- API 명세에 따라 예약 관리 페이지 로드 시 호출되는 예약 목록 조회 API를 구현한다.
- API 명세에 따라 예약 추가 API를 구현한다.
- API 명세에 따라 예약 삭제 API를 구현한다.
- 예약 관리 페이지에서 예약 추가와 취소가 정상적으로 동작해야 한다.

### 데이터베이스

- H2 데이터베이스를 사용하며, `DataSource`는 스프링 부트 자동 구성을 이용한다.
- 애플리케이션 시작 시 `schema.sql`로 `reservation` 테이블을 생성한다.
    - 컬럼: `id`(자동 증가), `name`, `date`, `time`
- 예약 조회/추가/삭제는 메모리가 아닌 데이터베이스를 사용한다.
    - 조회: `reservation` 테이블의 모든 예약을 응답한다.
    - 추가: 예약을 저장하고, 생성된 `id`를 포함해 `201 Created`와 `Location` 헤더로 응답한다.
    - 삭제: 해당 `id`의 예약을 삭제하고 `204 No Content`로 응답한다. 삭제할 예약이 없으면 `404 Not Found`로 응답한다.

### 파일 구조
```
src
├── main
│   ├── java
│   │   └── roomescape
│   │       ├── RoomescapeApplication.java
│   │       ├── controller
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   ├── ReservationController.java
│   │       │   └── ViewController.java
│   │       ├── dto
│   │       │   └── ReservationDto.java
│   │       ├── exception
│   │       │   └── NotFoundReservationException.java
│   │       ├── model
│   │       │   └── Reservation.java
│   │       ├── repository
│   │       │   └── ReservationRepository.java
│   │       └── service
│   │           └── ReservationService.java
│   └── resources
│       ├── application.properties
│       ├── schema.sql
│       ├── static
│       └── templates
└── test
    └── java
        └── roomescape
            └── MissionStepTest.java
```
 