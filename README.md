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
- 예약 관련 API 호출 시 에러가 발생하는 경우 중 요청의 문제인 경우 적절한 Status Code를 응답한다.

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
│           ├── model
│           │   └── Reservation.java
│           └── repository
│               └── ReservationRepository.java
└── test
    └── java
        └── roomescape
            └── MissionStepTest.java
```
