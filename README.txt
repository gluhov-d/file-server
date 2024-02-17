Необходимо реализовать REST API, которое взаимодействует с файловым хранилищем и предоставляет возможность получать доступ к файлам и истории загрузок.
Сущности:
User -> Integer id, String name, List<Event> events
Event -> Integer id, User user, File file
File -> Integer id, String name, String filePath
Требования:
Все CRUD операции для каждой из сущностей
Придерживаться подхода MVC
Для сборки проекта использовать Maven
Для взаимодействия с БД - Hibernate
Для конфигурирования Hibernate - аннотации
Инициализация БД должна быть реализована с помощью flyway
Взаимодействие с пользователем необходимо реализовать с помощью Postman (https://www.getpostman.com/)
Технологии: Java, MySQL, Hibernate, HTTP, Servlets, Maven, Flyway, Swagger.
