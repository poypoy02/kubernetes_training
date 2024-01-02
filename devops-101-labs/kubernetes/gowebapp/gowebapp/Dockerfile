FROM ubuntu:jammy

ENV DB_PASSWORD=mydefaultpassword
COPY ./code /opt/gowebapp
# COPY ./config /opt/gowebapp/config
VOLUME /opt/gowebapp/config

EXPOSE 8080
USER 1000

WORKDIR /opt/gowebapp

ENTRYPOINT [ "./gowebapp" ]