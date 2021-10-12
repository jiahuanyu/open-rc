./server_build.sh
adb push ./output/openrc-server.jar /data/local/tmp
adb shell CLASSPATH=/data/local/tmp/openrc-server.jar app_process /data/local/tmp me.jiahuan.openrc.device.server.Server
