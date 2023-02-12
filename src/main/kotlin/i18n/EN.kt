package i18n

val EN = Model(
    lang = "en",
    problem = """
Q:
There is a GKI device with kernel version 5.10.81-android12-9-xxxx-xxxx, android version 13, The kernel compression method is unknown, please read the tutorial carefully on the website and flash the image of the correct version and compression format for the device.

Condition:
Device Status：Entered bootloader(fastboot)

terminalfaker Built-in commands (type help to view) : fastboot, magiskboot, cd, ls, cat, clear, echo, help, history, pwd, reboot, rm, touch, uname, version, whoami

magiskboot： The binary file of the x86_64 platform is used here, and it can be operated directly on the linux terminal, without trying to push it and boot.img to the phone.
 
Do not add "./" to execute any command in terminalfaker

Directory /home/user/workdir : All the files used in this test are placed in this directory, such as magiskboot, boot-offical.img, etc

https://natsumerinchan.github.io/terminalfaker/

Once done, you will be given a password to send to the robot

/join [password]

And the invitation will be approved automatically!
""".trimIndent(),
    correct = "Correct Password!",
    incorrect = "Incorrect Password!",
    usage = "Usage: /join [password]",
    notFound = "JoinRequest not found"
)