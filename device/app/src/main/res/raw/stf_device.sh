CLASSPATH=/data/local/tmp/stf-server.jar app_process /data/local/tmp me.jiahuan.stf.server.Server
CLASSPATH=$(echo /data/app/com.package.name-*/base.apk) \
nohup app_process /system/bin --nice-name=process_name com.package.name.Main > /dev/null 2>&1 &
CLASSPATH=/data/app/me.jiahuan.stf.server-QwmZTqvUmiW_mAdHQbeQqQ==/base.apk app_process /system/bin --nice-name=stf_device me.jiahuan.stf.server.Server