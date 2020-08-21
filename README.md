# TdUserBot

## 安装

#### 依赖 (Linux)

```shell script
apt install -y openssl git zlib1g libc++-dev default-jdk
```

(Windows 下会自动下载)

注： 仅支持 `Win32, Win64, Linux amd64, Linux i386, Linux arm64`, 否则需自行编译 [LibTDJNi](https://github.com/TdBotProject/LibTDJni) 放置在 libs 文件夹下.

## 配置 ( 环境变量, 可选 )

`BOT_LANG`: 工作语言, 暂仅支持 `zh_CN`, `en_US`, 默认简中.  
`BINLOG`: 指定 binlog, 跳过交互式认证.

## 管理

`./bot.sh run` 进入交互式认证  
`./bot.sh init` 注册 systemd 服务  
`./bot.sh <start/stop/restart>` 启动停止  
`./bot.sh <enable/disable>` 启用禁用  
`./bot.sh log` 实时日志  
`./bot.sh logs` 打印所有日志

## 使用

`注: 为防止与其他机器人框架的命令重复, 建议使用 "!" 作为命令前缀而不是 "/".`

`注2: 为防止与 TdGroupBot 的命令重复, 添加 "_" 作为命令前缀.`

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
!chat_in_common <目标> : 查找共同聊天, 包括群组与自己为管理员的频道.
```