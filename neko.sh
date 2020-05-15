#!/bin/bash

# --------------------------- #
serviceName="td-user-bot"
artifact="td-user-bot"
# --------------------------- #

LC_ALL="C"

mvnHome=$M2_HOME
if ! [ $mvnHome ]; then
  mvnHome="$HOME/.m2"
fi

info() { echo "I: $*"; }

error() {

  echo "E: $*"
  exit 1

}

if [ ! "$1" ]; then

  echo "bash neko.sh [ init | update | run | log | start | stop | ... ]"

  exit

fi

if [ "$1" == "init" ]; then

  echo ">> 写入服务"

  cat >/etc/systemd/system/$serviceName.service <<EOF
[Unit]
Description=Telegram Bot ($serviceName)
After=network.target
Wants=network.target

[Service]
Type=simple
WorkingDirectory=$(readlink -e ./)
ExecStart=/bin/bash neko.sh run
Restart=on-failure
RestartPreventExitStatus=100

[Install]
WantedBy=multi-user.target
EOF

  systemctl daemon-reload

  echo ">> 写入启动项"

  systemctl enable $serviceName &>/dev/null

  echo "<< 完毕."

  exit

elif [ "$1" == "run" ]; then

  [ -f "$artifact.jar" ] || bash $0 rebuild

  shift

  java -jar $artifact.jar $@

elif [ "$1" == "start" ]; then

  systemctl start $serviceName

  bash $0 log

elif [ "$1" == "restart" ]; then

  systemctl restart $serviceName

  bash $0 log

elif [ "$1" == "rebuild" ]; then

  git submodule update --init --force --recursive

  mvn -Dmaven.repo.local="$mvnHome" clean package

  if [ $? -eq 0 ]; then

    cp -f bot/target/$artifact-*.jar $artifact.jar

  fi

elif [ "$1" == "update" ]; then

  git fetch &>/dev/null

  if [ "$(git rev-parse HEAD)" = "$(git rev-parse FETCH_HEAD)" ]; then

    echo "<< 没有更新"

    exit 1

  fi

  echo ">> 检出更新 $(git rev-parse FETCH_HEAD)"

  git reset --hard FETCH_HEAD &>/dev/null

  bash $0 rebuild

  exit $?

elif [ "$1" == "log" ]; then

  journalctl -u $serviceName -f

elif [ "$1" == "logs" ]; then

  shift 1

  journalctl -u $serviceName --no-tail $@

else

  systemctl "$1" $serviceName

fi
