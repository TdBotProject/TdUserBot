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
  -s 贴纸
  -f 转发
  -m 服务消息
  -k 群组中保留频道消息
  -h 进度发送到收藏夹而不是当前聊天
!upgrade: 升级基本群组到超级群组
!filter_users: 清理群组 / 频道成员
  -m 无发言
  -p 无头像
  -a 看起来像广告 (-m && -p && 另外一个特征)
  -k 不清理死号
  -h 进度发送到收藏夹而不是当前聊天
```