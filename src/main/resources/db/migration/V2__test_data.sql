INSERT INTO users (name)
VALUES ('Dmitrii'),
       ('Maxim'),
       ('Oleg');

INSERT INTO files(name, file_path, status)
VALUES ('1.txt', '/Users/dmitriiglukhov/Documents/GIT/file-server/src/main/resources/uploads/1.txt', 'AVAILABLE'),
       ('2.txt', '/Users/dmitriiglukhov/Documents/GIT/file-server/src/main/resources/uploads/2.txt', 'ARCHIVED'),
       ('3.txt', '/Users/dmitriiglukhov/Documents/GIT/file-server/src/main/resources/uploads/3.txt', 'DELETED');

INSERT INTO events(user_id, file_id)
VALUES (1, 3),
       (2, 2),
       (3, 1);