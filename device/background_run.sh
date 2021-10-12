adb push openrc_device_settings.json /sdcard/Android/data/me.jiahuan.openrc.device/files/
adb shell CLASSPATH=$(adb shell pm path me.jiahuan.openrc.device) app_process /system/bin --nice-name=openrc_device_background me.jiahuan.openrc.device.background.Background
