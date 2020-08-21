FROM docker.pkg.github.com/tdbotproject/nekolib/td-base:latest

WORKDIR /root

ADD bot/target/td-user-bot.jar .

RUN java -jar td-user-bot.jar --download-library

ENTRYPOINT java -jar td-user-bot.jar