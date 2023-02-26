package i18n

val EN = Model(
    lang = "en",
    problem = """
Q:
There is a GKI 2.0 device with kernel version 5.10.117-android12-9-xxxx-xxxx, android version 13, The kernel compression method is unknown, please read the tutorial carefully on the website and flash the image of the correct version and compression format for the device.

Condition:
Device Status：OS is booted

terminalfaker Built-in commands (type help to view) : status, adb, fastboot, magiskboot, cd, ls, cat, clear, echo, help, history, pwd, reboot, rm, touch, uname, version, whoami

magiskboot： The binary file of the x86_64 platform is used here, and it can be operated directly on the linux terminal, without trying to push it and boot.img to the phone.

status: Used to check the device status (system/bootloader/fastbootd/recovery).

Do not add "./" to execute any command in terminalfaker

Directory /home/user/workdir : All the files used in this test are placed in this directory, such as magiskboot, boot-offical.img, etc

https://exam.kernelsu.org/?pwd=[PASSWORD]

Once done, you will be given a password to send to the robot

/join [password] (Such as /join 123456)

And the invitation will be approved automatically!
""".trimIndent(),
    correct = "Correct Password!",
    incorrect = "Incorrect Password!",
    usage = "Usage: /join [password]",
    notFound = "JoinRequest not found"
)