# TdGroupBot

## 安装

#### 依赖

```shell script
apt install -y openssl git zlib1g libc++-dev python3-pip maven
```

#### NSFW 检测服务器

```shell script
echo "deb [arch=amd64] http://storage.googleapis.com/tensorflow-serving-apt stable tensorflow-model-server tensorflow-model-server-universal" | tee /etc/apt/sources.list.d/tensorflow-serving.list
curl https://storage.googleapis.com/tensorflow-serving-apt/tensorflow-serving.release.pub.gpg | apt-key add -
apt update && apt install -y tensorflow-model-server

pip3 install -r extra/nsfw/requirements.txt
bash nsfw.sh init
bash nsfw.sh start
```

## 配置 ( 环境变量, 可选 )

`BOT_LANG`: 工作语言, 暂仅支持 `zh_CN`, `en_US`, 默认简中.   
`BOT_TOKEN`: 如果填写, 则跳过交互式认证.