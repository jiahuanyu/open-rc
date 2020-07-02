./server_build.sh
adb push ./output/stf-server.jar /data/local/tmp
adb shell CLASSPATH=/data/local/tmp/stf-server.jar app_process /data/local/tmp me.jiahuan.stf.server.Server