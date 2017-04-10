FROM hseeberger/scala-sbt

MAINTAINER Artem Konovalov <izebit@gmail.com>

ENV DEBIAN_FRONTEND noninteractive

RUN echo 'deb http://http.debian.net/debian jessie-backports main'> /etc/apt/sources.list && \
    apt-key adv --keyserver keyserver.ubuntu.com --recv 7F0CEB10 && \
    echo "deb http://repo.mongodb.org/apt/debian wheezy/mongodb-org/3.0 main" >> /etc/apt/sources.list && \
    apt-get update 

RUN apt-get install -y mongodb-org-server && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y git


RUN git clone https://github.com/android-group/night-meet.git && \
    cd night-meet && \
    sbt assembly

VOLUME /opt/mongo

CMD cd night-meet/others/ && sh run.sh



