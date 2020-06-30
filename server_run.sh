./server_build.sh
adb push stf-server.jar /data/local/tmp
adb shell CLASSPATH=/data/local/tmp/stf-server.jar app_process /data/local/tmp com.xingren.stf.Server