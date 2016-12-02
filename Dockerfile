FROM openjdk:8-jre-alpine
MAINTAINER Wouter Habets (wouterhabets@gmail.com)

EXPOSE 8000 8001

COPY core/build/libs/core.jar /usr/bin/SjtekControl.jar
CMD ["java", "-jar", "/usr/bin/SjtekControl.jar"]
