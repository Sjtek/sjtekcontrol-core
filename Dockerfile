FROM openjdk:8-jre
MAINTAINER Wouter Habets (wouterhabets@gmail.com)

EXPOSE 4567

#COPY core/build/libs/core.jar /opt/sjtekcontrol-core/core.jar
#CMD ["java", "-jar", "/opt/sjtekcontrol-core/core.jar"]
ADD core/build/distributions/core.tar /opt/sjtekcontrol-core/
CMD ["/opt/sjtekcontrol-core/core/bin/core"]