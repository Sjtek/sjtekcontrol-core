FROM whhoesj/sjtek-base:latest
COPY build/libs/SjtekControlV3-1.0-SNAPSHOT.jar /usr/bin/SjtekControl.jar
CMD ["java", "-jar", "/usr/bin/SjtekControl.jar"]
