# Docker Compose Hands-on

In the workshop, we talked about creating a basic multi-service application. But, the final compose file wasn't _quite_ ready to run an application. The application is a typical spring-boot application that used JPA to access a MY_SQL database.

1. In the workshop directory, create a new `Dockerfile`. In it, place the following (just like in out first excercise):

```
FROM maven:3 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM adoptopenjdk/openjdk11:alpine-slim
COPY --from=build /usr/src/app/target/app*.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]    
```

2. Update the compose file to use this image to add the `build` instruction:

    ```
    services:
      app:
        build: app
    ```

    The `build` instruction tells Compose to build an image using the Dockerfile found in the `app` directory  and then use that as the image.

3. Remove the `image` declaration from the `app` service.

4. Now, if you run `docker-compose up`, you should see an image be created and the application attempt to start. You can see in the logs multiple errors related to database connection

## Connecting to the database

1. Docker compose provides a way to declare service dependencies. Open `docker-compose.yml` and declare that the `app` service depends on `mysql`. You set that in `app` section right under `ports` mapping

```
    depends_on:
      - mysql
```
where `mysql` is the name of the service defined below. This ensures that `mysql` service will start before the `app` service. 



2. If you re-attempt to start the application (`docker-compose up`) you will again see failure messages when attemping to connect to the database

3. Open the `application.properties` under `app/src/main/resources` directory. You may notice `spring.datasource.url`  is mentioning that the database is either at `${MYSQL_HOST}` or `localhost`. Obvously `localhost` cannot work :) We need to override specifying the env variable
Open `docker-compose.yml` and add it in `app` section right under `depends_on` provided before

```
    environment:
      - MYSQL_HOST=mysql
```

4. Running `docker-compose up -d ` should do the trick 

5. Verify that the applicaiton is running 
```
curl localhost:8080/demo/all | jq
```
should return a list of prepopulated users 

```
[
  {
    "id": 1,
    "name": "Jack Bauer",
    "email": "jack@ctu.gov.us"
  },
  {
    "id": 2,
    "name": "Chloe O'Brian",
    "email": "cloe@ctu.gov.us"
  },
  {
    "id": 3,
    "name": "Kim Bauer",
    "email": "kim@ctu.gov.us"
  },
  {
    "id": 4,
    "name": "David Palmer",
    "email": "palmer@usa.gov"
  },
  {
    "id": 5,
    "name": "Michelle Dessler",
    "email": "michelle@ctu.gov.us"
  }
]
``` 
