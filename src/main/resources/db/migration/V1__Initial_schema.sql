CREATE TABLE users (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL
);

CREATE TABLE files (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       file_path VARCHAR(255) NOT NULL,
                       status ENUM('UPLOADED', 'DELETED', 'ARCHIVED', 'AVAILABLE')
);

CREATE TABLE events (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      user_id BIGINT NOT NULL,
                      file_id BIGINT NOT NULL,
                      FOREIGN KEY (file_id) REFERENCES Files(id),
                      FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE
);