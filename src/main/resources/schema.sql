CREATE TABLE reservation (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    date VARCHAR(255) NOT NULL,
    time VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    -- shortcut: VARCHAR 중복 제약은 저장 시 정규화를 전제로 한다. DB 직접 입력의 표현 차이까지 막을 때 DATE/TIME으로 전환한다.
    CONSTRAINT reservation_date_time_unique UNIQUE (date, time)
);
