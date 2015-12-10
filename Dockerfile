FROM whhoesj/sjtek-base:latest
COPY build/libs/SjtekControlV3-1.0-SNAPSHOT.jar /usr/bin/SjtekControl.jar
COPY crontab /etc/cron.d/sjtekcontrol-nightmode-disable
RUN chmod 0644 /etc/cron.d/sjtekcontrol-nightmode-disable
CMD ["java", "-jar", "/usr/bin/SjtekControl.jar"]
