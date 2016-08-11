FROM java:8-jre
MAINTAINER Wouter Habets (wouterhabets@gmail.com)

RUN DEBIAN_FRONTEND=noninteractive apt-get update -y && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y pulseaudio espeak curl mpc
COPY speak.sh /usr/bin/speak
RUN chmod +x /usr/bin/speak

EXPOSE 8000 8001

COPY core/build/libs/core.jar /usr/bin/SjtekControl.jar
CMD ["java", "-jar", "/usr/bin/SjtekControl.jar"]
