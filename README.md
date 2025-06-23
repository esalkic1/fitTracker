# Instructions

To start the app you need
- Java 21
- Postgres
- A Maven build system (comes with IntelliJ by default)

## Docker compose
To run through Docker Compose simply run `docker-compose up` in root folder. Do note that after building containers 
it takes about 10-20 seconds to run all of them due to inter-service dependencies.

## Running manually

First, through any DB software (or terminal) create 4 postgres databases, one for each microservice.
They should be named `nwt_auth`, `nwt_notification`, `nwt_nutrition`, `nwt_events` and `nwt_workout`.

For each microservice, inside its package run `mvn clean install` (`common` and `events` have to be compiled first).

We can now start the applications.

Due to services being interconnected and dependent they have to be
started in a specific order.
1. **Eureka Server** - this must be started first as it acts as Eureka Microservice Registry
2. **Config Server** - must be started before services to offer them their app config. More info on setting this up in config-server README.
3. **System Events Service**
4. **Auth Service**
5. **Workout Service**
6. **Nutrition Service**
7. **Notification Service**


After all of the above the services should be started. To confirm they started correctly 
go to Eureka registry (`http://localhost:8761/`) and confirm that there are 4 registered services.

---
A demo of the application and completed tasks can be found at [the following link](https://drive.google.com/drive/folders/1JNBdTgggExFMfduXcrpLc0UFXzsZQH7y?usp=drive_link).
