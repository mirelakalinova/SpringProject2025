# DORELA SOFT

Car repair service management system

## Description
`Dorela Soft` is web based application which can help small car repair shops to manage clients data - cars and companies

- Car - add, edit and delete 
- Client - add, edit and delete 
- Company - add, edit and delete 
- Part - add, edit and delete 
- Service - add, edit and delete 
- Repair - add, edit and delete 
- User - add, edit, delete, block/unblock users  

All authenticated users could add, edit and view all data form all domains. 
Only ADMIN can delete data and block/unblock users

## Getting Started

**Dependencies**

- Java 17
- Spring Boot 3.4.0
- Spring Data JPA / Spring Data JDBC
- MySQL
- Spring Security
- OpenFeign
- Maven (build)
- ModelMapper
- JUnit
- Mockito
- HSQLDB
- Lombok
- Lombok
- jquery
- vue.js
- bootstrap 5

## Installing
### Docker image 
    Official docker image 
        > https://hub.docker.com/repository/docker/mirelakalinova/dorela/general
    Expample push command
        > docker push mirelakalinova/dorela:2

### Required environment variables
  - DB_HOST
  - DB_PORT
  - DB_USER
  - DB_PASS
  - PORT
  - REDIS_HOST
  - REDIS_PORT
  - API_BASE

### Default credentials
  - username: dorel-auto
  - password: 123
## Necessary resources
  - JDK 17
  - Maven 3.6+
  - MySQL 8.x (или друга съвместима база)
  - External Api service 
    > Docker image
     https://hub.docker.com/repository/docker/mirelakalinova/car-api
  - Redis 
    > Docker image
    https://hub.docker.com/_/redis

## Authors
- Mirela Kalinova
- mirelakalinova@gmial.com
- GitHub: https://github.com/mirelakalinov
- LinkedIn: https://www.linkedin.com/in/mirela-kalinova-6aa617198/
   
