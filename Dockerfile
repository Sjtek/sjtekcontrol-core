FROM whhoesj/sjtek-base:latest
COPY core/build/libs/core.jar /usr/bin/SjtekControl.jar
CMD ["java", "-jar", "/usr/bin/SjtekControl.jar"]
