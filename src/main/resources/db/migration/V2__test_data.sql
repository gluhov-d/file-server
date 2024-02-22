INSERT INTO Users (name)
VALUES ('Dmitrii'),
       ('Maxim'),
       ('Oleg');

INSERT INTO Files(name, file_path)
VALUES ('1.txt', '/1.txt'),
       ('2.txt', '/2.txt'),
       ('3.txt', '/3.txt');

INSERT INTO Events(user_id, file_id)
VALUES (1, 3),
       (2, 2),
       (3, 1);