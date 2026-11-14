# SpringMVC 

## 패키지 구조
```aiignore
src
└── main
    └── java
        └── roomescape
            ├── RoomescapeApplication.java           # 애플리케이션 실행
            ├── controller
            │   ├── HomeViewController.java          # 홈 화면 반환
            │   ├── ReservationController.java       # 예약 조회, 생성, 삭제 및 입력값 검증
            │   └── ReservationViewController.java   # 예약 관리 화면 반환
            ├── domain
            │   └── Reservation.java                 # 예약 정보
            ├── dto
            │   └── ReservationRequest.java          # 예약 생성 요청 데이터
            ├── exception
            │   ├── GlobalExceptionHandler.java      # 예외를 404 응답으로 처리
            │   └── ReservationNotFoundException.java # 삭제할 예약이 없는 경우
            └── repository
                └── ReservationRepository.java       # 메모리에 예약 저장, 조회, 삭제
```

## 1단계 - 홈 화면
- `localhost:8080` 요청 시 어드민 메인 페이지 응답
- 어드민 메인 페이지는 `templates/home.html` 파일 사용

## 2단계 - 예약 조회
- `/reservations` 요청 시 예약 관리 페이지 응답
- 어드민 메인 페이지는 `templates/reservation.html` 파일 사용
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
- 이름이 누락되거나 빈 문자열, 공백인 경우 -> 400 Bad Request
- 날짜 또는 시간이 누락되거나 빈 문자열인 경우 -> 400 Bad Request
- 날짜 또는 시간의 형식이나 값이 유효하지 않은 경우 -> 400 Bad Request
- 삭제할 예약의 식별자로 저장된 예약을 찾을 수 없는 경우 -> 404 Not Found
