# Instructions

To start the app you need
- Java 21
- Postgres
- A Maven build system (comes with IntelliJ by default)

First, through any DB software (or terminal) create 4 postgres databases, one for each microservice.
They should be named `nwt_auth`, `nwt_notification`, `nwt_nutrition` and `nwt_workout`.

For each microservice, inside its package run `mvn clean install`. 

We can now start the applications.

Due to services being interconnected and dependent they have to be
started in a specific order.
1. **Eureka Server** - this must be started first as it acts as Eureka Microservice Registry
2. **Auth Service**
3. **Workout Service**
4. **Nutrition Service**
5. **Notification Service**


After all of the above the services should be started. To confirm they started correctly 
go to Eureka registry (`http://localhost:8761/`) and confirm that there are 4 registered services.
