CLASSPATH=/data/local/tmp/stf-server.jar app_process /data/local/tmp me.jiahuan.stf.server.Server

CLASSPATH=$(echo /data/app/com.package.name-*/base.apk) nohup app_process /system/bin --nice-name=process_name com.package.name.Main > /dev/null 2>&1 &

adb shell CLASSPATH=$(adb shell pm path me.jiahuan.openrc.device) app_process /system/bin --nice-name=openrc_device_background me.jiahuan.openrc.device.background.Background