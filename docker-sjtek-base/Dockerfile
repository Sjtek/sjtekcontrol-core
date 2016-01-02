FROM java:8
RUN DEBIAN_FRONTEND=noninteractive apt-get update -y && \
    DEBIAN_FRONTEND=noninteractive apt-get install -y pulseaudio espeak curl mpc
COPY speak.sh /usr/bin/speak
RUN chmod +x /usr/bin/speak
