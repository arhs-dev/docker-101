# Hands-on
In this first hands-on exercise, we're going to build our first container image, push it to Docker Hub, and then run a container using that image.

## Prerequisites
1. If don't already have on, create a [docker hub](https://hub.docker.com/) account. 

## Setting up  
There are two ways to accomplish the workshop. The first is to have local [docker installation](https://docs.docker.com/v17.09/engine/installation/). If you don't have a local installation and you don't want to create one you may use [play-with-docker](http://play-with-docker.com/).

### Local installation
Ensure that you have an up-to-date, working local installation:

```bash
docker --version
```
should output something like 

```
$ docker --version 
$ Docker version 19.03.5, build 633a0ea838
```
If you don't have docker installed just follow the guide at [https://docs.docker.com/install/](https://docs.docker.com/install/) for your OS. 

### Play with Docker
Head to [http://play-with-docker.com/](http://play-with-docker.com/) and login with your docker account.

## Getting started
We 'll be creating a container image using a Dockerfile.

1. Create a directory named `exercise1`.
2. Clone the repo github.com/arhs-dev/first-app
3. Change into `first-app` directory and build the app e.g. using `./mvnw package`.
4. Create a file named `Dockerfile`. In the file put the following:

	```
	FROM adoptopenjdk/openjdk11:alpine-slim
	COPY target/first-app*.jar /usr/app/app.jar
	EXPOSE 8080
	ENTRYPOINT ["java","-jar","/usr/app/app.jar"]
	```
	
  - The `FROM` command tells the builder that our container image is based on `adoptopenjdk/openjdk11:alpine-slim`. The image contain OpenJDK binaries that are built by AdoptOpenJDK. Any instructions we perform afterwards will add additional layers on top of that image. 
  - The `COPY` command places our appliation uber-jar into the filesystem we are creating for our container image at `/usr/app/` directory.
  - The `EXPOSE` instruction informs docker that the container listens to port 8080 (captures the intention of the Dockerfile author to publish the port). The responsibility to actually publish the port is left to the person running the container.
  - The `ENTRYPOINT` instruction tells the container to execute the prescribed command (i.e. `java -jar /usr/app/app.jar`).  

5. Build the image
```sh
docker build -t first-app .
```

  - The `-t` flag indicates a 'tag', or a name that we want to apply to the image. We can then use that to start a container from that name (which we'll do next).
  -  The trailing . tells the Docker Engine that the Dockerfile and build context to be used is the current directory. The build context indicates the root of files we will use in the Dockerfile (like the copying of our application uber-jar file).

6. Run the container image
```
docker run -d -p 8080:8080 first-app
``` 

  - The -p flag tells the Docker Engine that we want to create a mapping of port 8080 on the host (the left-side of the argument) to port 8080 of the container (the right side).
  - The -d flag tells the Docker Engine that we want to run the new container in 'detached mode'. In other words, simply run it in the background.
  
6. Now our application should be up and running. You can can verify by a get request at the endpoint, e.g. 
```bash
curl localhost:8080/hello
```

---
In case you don't have a proper java installation in order to build the application (i.e. `./mvnw package` fails) you can use docker to build your application;
```
docker run -it --rm --name first-app-built -v "$PWD":/usr/src/app -w /usr/src/app maven:3 mvn clean install
```
and will be able to build the image (step 5).

How about combining the steps above, compile the code and build the image? This can be done with Docker multi-stage builds, which actually exists to support another need that is the built of optimized images. You may use the following Dockerfile to achieve this
```
FROM maven:3 AS build
COPY src /usr/src/app/src
COPY pom.xml /usr/src/app
RUN mvn -f /usr/src/app/pom.xml clean package

FROM adoptopenjdk/openjdk11:alpine-slim
COPY --from=build /usr/src/app/target/first-app*.jar /usr/app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/app/app.jar"]
```
  - The `RUN` instruction executes an arbitrary command inside the command inside the container.
  - The `COPY` instruction is a bit different now, it tells docker to copy a resource from a build stage labeled 'build' instead of the context (the directory we build docker image). Another already built image can be used to copy resources from.
``` 
