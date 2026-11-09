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
│           ├── model
│           │   └── Reservation.java
│           └── repository
│               └── ReservationRepository.java
└── test
    └── java
        └── roomescape
            └── MissionStepTest.java
```
