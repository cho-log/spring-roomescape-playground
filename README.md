# Spring 방탈출 예약 관리

## 1단계 - 홈 화면
- `localhost:8080` 요청 시 어드민 메인 페이지 응답
- 어드민 메인 페이지는 `templates/home.html` 파일 사용

## 2단계 - 예약 조회
- `/reservation` 요청 시 예약 관리 페이지 응답
- 예약 관리 페이지는 `templates/reservation.html` 파일 사용
- 예약 목록 조회 API 구현
- 예약 생성 로직이 아직 없어 정상 동작을 확인하기 위해 임의 데이터를 넣어서 확인
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
    ```
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
- 요청에 문제가 있는 경우 적절한 상태 코드 응답
  - 예약 추가 시 필요한 인자값이 비어있는 경우 `400 Bad Request` 응답
  - 삭제할 예약을 찾을 수 없는 경우 `404 Not Found` 응답
  - 조회할 예약을 찾을 수 없는 경우 `404 Not Found` 응답

## 5단계 - 데이터베이스 적용하기
- H2 데이터베이스 연동
  - `spring-boot-starter-jdbc`, `h2` 의존성 추가
  - `application.properties`에 H2 콘솔 활성화, 데이터베이스 URL 설정
  - `schema.sql`로 애플리케이션 실행 시 `reservation` 테이블 생성

## 6단계 - 데이터 조회하기
- 예약 목록 조회 시 기존 List 대신 데이터베이스에서 조회
- `JdbcTemplate`과 `RowMapper`를 사용해 조회 결과를 `Reservation` 객체로 변환

## 7단계 - 데이터 추가 / 삭제하기
- 예약 추가 / 삭제 시 기존 List 대신 데이터베이스 사용
  - 예약 추가 시 `KeyHolder`로 데이터베이스가 생성한 id를 받아 응답에 사용
  - 예약 삭제 시 삭제된 행이 없으면 `404 Not Found` 응답
- 데이터베이스 접근 로직을 `ReservationDao`로 분리
