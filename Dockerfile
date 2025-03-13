FROM bellsoft/liberica-openjdk-alpine:23-cds
COPY build/libs/uh-compiler-0.0.1.jar ./
COPY src/ ./source_code/src/
COPY Readme.md ./source_code/
COPY build.gradle ./source_code/
COPY settings.gradle ./source_code/
COPY gradlew ./source_code/
COPY gradle/ ./source_code/gradle/

RUN apk add --no-cache bash binutils
RUN apk add --no-cache build-base

EXPOSE 3000
ENTRYPOINT ["java", "-jar", "./uh-compiler-0.0.1.jar", "serve", "--port=3000"]
