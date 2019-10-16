FROM gradle:jdk12 AS builder
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle shadowJar

FROM openjdk:12-alpine
EXPOSE 3245
COPY --from=builder /home/gradle/src/build/libs/bot.jar /
ENTRYPOINT ["java", "-jar", "bot.jar"]