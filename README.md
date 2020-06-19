# TdUserBot

## 安装

#### 依赖

```shell script
apt install -y openssl zlib1g libc++-dev default-jdk maven
```

## 配置 ( 环境变量, 可选 )

`BOT_LANG`: 工作语言, 暂仅支持 `zh_CN`, `en_US`, 默认简中.   

## 使用

```
!del_me: 删除自己的所有消息在当前聊天
!del_all: 删除所有消息在当前聊天
  -s 仅贴纸
  -k 群组中保留频道消息
  -h 进度发送到收藏夹而不是当前聊天
!upgrade: 升级基本群组到超级群组
!clean_da: 清理群组/频道中的死号
```