FROM bellsoft/liberica-openjdk-alpine:23-cds
#FROM openjdk:23-jdk-slim-bullseye
COPY build/libs/compiler-project-1.0-SNAPSHOT.jar ./

RUN apk add --no-cache curl
RUN apk add --no-cache bash binutils
RUN apk add --no-cache build-base

#ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=5005,server=y,suspend=n

EXPOSE 3000
EXPOSE 3001
EXPOSE 5005
ENTRYPOINT ["java", "-jar", "./compiler-project-1.0-SNAPSHOT.jar", "serve", "--port=3000"]
#ENTRYPOINT ["java", "-cp", "./compiler-project-1.0-SNAPSHOT.jar", "fi.helsinki.compiler.Compiler", "serve", "--port=3000"]
