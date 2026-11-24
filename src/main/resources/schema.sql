CREATE TABLE time (
                      id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                      time VARCHAR(255) NOT NULL
);

CREATE TABLE reservation (
                             id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(255) NOT NULL,
                             date VARCHAR(255) NOT NULL,
                             time_id BIGINT NOT NULL,
                             FOREIGN KEY (time_id) REFERENCES time(id)
);