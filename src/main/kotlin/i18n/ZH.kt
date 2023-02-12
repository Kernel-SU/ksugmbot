package i18n

val ZH = Model(
    lang = "zh",
    problem = """
题目：
这里有一台内核版本为5.10.81-android12-9-xxxx-xxxx，Android版本为13的GKI设备，内核压缩方式未知，请在网站上认真翻阅教程后为该设备刷入正确版本和压缩格式的镜像。

条件：
系统状态：已进入bootloader(fastboot)

terminalfaker内置命令(可键入help查看) : fastboot, magiskboot, cd, ls, cat, clear, echo, help, history, pwd, reboot, rm, touch, uname, version, whoami

magiskboot： 这里使用的是x86_64平台的二进制文件，直接在linux终端操作即可，无需尝试将它和boot.img推送至手机。
 
在terminalfaker中执行任何命令都不要加上“./”

目录/home/user/workdir : 本次测试所有用到的文件都被放在这个目录下，如 magiskboot, boot-offical.img 等

https://natsumerinchan.github.io/terminalfaker/

完成后，你将得到一个密码，对机器人发送

/join [你得到的密码]

即可进群
""".trimIndent(),
    correct = "密码正确！",
    incorrect = "密码错误！",
    usage = "使用方法: /join [密码]",
    notFound = "未找到你的进群申请"
)