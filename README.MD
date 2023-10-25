# Micro notes project

### Description

This project experimental project - to build minimal spring REST application with quarkus.

The application implements the same APIs and functionality as spring apps:

1. Pure spring app - https://github.com/nickperov/micro-notes-spring
2. Spring boot app - https://github.com/nickperov/micro-notes-spring-boot

### Build
```
mvn clean package
```
### Run
```
java -jar target/quarkus-app/quarkus-run.jar
```
### Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

### APIs

**notes-controller**

|                                                           |                     |                       |
|-----------------------------------------------------------|---------------------|-----------------------|
| <font style="background-color:#4379a8;">**GET**</font>    | /api/notes          | list all notes        |
| <font style="background-color:#4379a8;">**GET**</font>    | /api/notes/{noteId} | get single note       |
| <font style="background-color:#6e8641;">**POST**</font>   | /api/notes          | create note           |
| <font style="background-color:#ffd541;">**PUT**</font>    | /api/notes/{noteId} | create or update note |
| <font style="background-color:#b22222;">**DELETE**</font> | /api/notes/{noteId} | delete note           |

    noteId is UUID
    note body is plain string

### Authors

Nikolay Perov

### License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

