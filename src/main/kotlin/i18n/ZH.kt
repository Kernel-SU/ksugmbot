package i18n

val ZH = Model(
    lang = "zh",
    problem = """
题目：
这里有一台内核版本为5.10.157-android12-9-xxxx-xxxx，Android版本为13的GKI 2.0设备，内核压缩方式未知，请在网站上认真翻阅教程后为该设备刷入正确版本和压缩格式的镜像。

教程链接：
https://kernelsu.org/zh_CN/guide/installation.html

条件：
设备状态：已进入系统

terminalfaker内置命令(可键入help查看) : status, adb, fastboot, magiskboot, cd, ls, cat, clear, echo, help, history, pwd, reboot, rm, touch, uname, version, whoami

magiskboot： 这里使用的是x86_64平台的二进制文件，直接在linux终端操作即可，无需尝试将它和boot.img推送至手机。
 
status: 用于检查设备状态(system/bootloader/fastbootd/recovery)

在terminalfaker中执行任何命令都不要加上“./”

目录/home/user/workdir : 本次测试所有用到的文件都被放在这个目录下，如 magiskboot, boot-official.img 等

https://exam.kernelsu.org/?pwd=[PASSWORD]

完成后，你将得到一个密码，对机器人发送

/join [你得到的密码] (例如 /join 123456)

即可进群
""".trimIndent(),
    correct = "密码正确！",
    incorrect = "密码错误！",
    usage = "使用方法: /join [密码]",
    notFound = "未找到你的进群申请"
)