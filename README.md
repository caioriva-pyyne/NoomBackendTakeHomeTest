# NoomBackendTakeHomeTest
This is a Spring-Boot application created by Caio Riva as an assigment for Noom.

## Requirements
This project was created using [JDK 11](https://openjdk.org/projects/jdk/11/) (the version defined in the test's template) 
and [Gradle 8.4](https://docs.gradle.org/8.4/release-notes.html), so make sure to properly use them.

## Run migration scripts
Before starting the app, is important that an accessible postgres instance is running and the database schema 
is up-to-date with all migrations. To ensure that, run the following command from the [sleep directory](sleep):
```
./gradlew flywayMigrate \
  -Dflyway.url=${url} \
  -Dflyway.user=${user} \
  -Dflyway.password=${password}
```
By default, you can use the configs stored in the [application.yml](sleep/src/main/resources/application.yml) and in
the [docker-compose file](docker-compose.yml). in case you decide to change them, all places and the flyway migration
command should be updated accordingly.

OBS: In a real world scenario, sensitive configs wouldn't be stored in the repository.

OBS 2: To help starting a local postgres instance, you can start it with docker-compose. Go to the
[root](.) and run:
```
docker-compose up -d db
```

## Run app
Execute the following commend on the [sleep directory](sleep):
```
./gradlew bootRun
```

OBS: You can also run the app with docker-compose. Go to the [root](.) and run:
```
docker-compose up -d sleep_api
```

## Unit Tests
To run the unit tests, go to the [sleep directory](sleep) and run:
```
./gradlew test 
```

### API Contract
There are 4 endpoints in total (inside the controller package):
* GET endpoints return 200 and produce serialized JSON objects based on
  [StandardResponse](sleep/src/main/java/com/noom/interview/fullstack/sleep/model/dto/response/StandardResponse.java) class;
* POST and PUT return 201 and produce serialized JSON objects based on
  [StandardResponse](sleep/src/main/java/com/noom/interview/fullstack/sleep/model/dto/response/StandardResponse.java) class;
* When request validation errors happen, the endpoints return 400 and produce a serialized JSON based
  on `ErrorResponse`record inside [GlobalExceptionHandler](sleep/src/main/java/com/noom/interview/fullstack/sleep/exception/GlobalExceptionHandler.java)
* Trying to retrieve or accessing a nonexistent data returns 404 and produces a
  serialized JSON based on `ErrorResponse` record inside [GlobalExceptionHandler](sleep/src/main/java/com/noom/interview/fullstack/sleep/exception/GlobalExceptionHandler.java).

#### Successful Request and Response Examples
##### POST /api/user
###### Request Body
```json
{
  "name": "Caio Riva",
  "email": "caio.riva@email.com",
  "age": 27
}
```
###### Response - Status: 201 Created
```json
{
    "status": "CREATED",
    "timestamp": "2024-09-25T02:36:21.986675Z",
    "data": {
        "id": "51d3f4e9-9154-47d4-a07d-8e3c9d3dbfb4",
        "name": "Caio Riva",
        "email": "caio.riva@email.com",
        "age": 27
    }
}
```
##### PUT api/sleep?userId=
###### Request Body
```json
{
    "startDateTimeInBed": "2024-09-20T22:30:00",
    "endDateTimeInBed":  "2024-09-21T08:30:00",
    "feeling": "BAD"
}
```
###### Response - Status: 201 Created
```json
{
  "status": "CREATED",
  "timestamp": "2024-09-25T02:37:20.626959Z",
  "data": {
    "sleepDate": "2024-09-20",
    "timeInBedStart": "22:30:00",
    "timeInBedEnd": "08:30:00",
    "totalTimeInBed": "PT10H",
    "feeling": "BAD"
  }
}
```
##### GET /api/sleep/last-night?userId=
###### Request Body
```json
```
###### Response - Status: 200 Ok
```json
{
  "status": "OK",
  "timestamp": "2024-09-25T02:40:15.085982Z",
  "data": {
    "sleepDate": "2024-09-23",
    "timeInBedStart": "22:30:00",
    "timeInBedEnd": "08:30:00",
    "totalTimeInBed": "PT10H",
    "feeling": "BAD"
  }
}
```
##### GET /api/sleep/last-days-average?userId=&numOfDays=
- If `numOfDays` is not provided, default value will be 30. 
###### Request Body
```json
```
###### Response - Status: 200 Ok
```json
{
  "status": "OK",
  "timestamp": "2024-09-25T02:50:26.637811Z",
  "data": {
    "startDate": "2024-08-24",
    "endDate": "2024-09-23",
    "averageTotalTimeInBed": "PT10H",
    "averageTimeInBedStart": "22:30:00",
    "averageTimeInBedEnd": "08:30:00",
    "feelingFrequencies": {
      "GOOD": 0,
      "OK": 0,
      "BAD": 2
    }
  }
}
```

## Suggestions
### Using Postman
If you want to test the application using [Postman](https://www.postman.com/downloads/), a [Postman collection](Noom-Backend-Take-Home-Test.postman_collection.json)
file is included in this project.
