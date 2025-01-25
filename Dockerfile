FROM openjdk:21-jdk-slim

WORKDIR /app
COPY /build/compiled/Telegram-Emails.jar /app/Telegram-Emails.jar

CMD ["java", "-jar", "/app/Telegram-Emails.jar"]