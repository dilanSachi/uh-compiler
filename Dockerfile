FROM bellsoft/liberica-openjdk-alpine:23-cds
COPY build/libs/uh-compiler-0.0.1.jar ./

RUN apk add --no-cache bash binutils
RUN apk add --no-cache build-base

EXPOSE 3000
ENTRYPOINT ["java", "-jar", "./uh-compiler-0.0.1.jar", "serve", "--port=3000"]
