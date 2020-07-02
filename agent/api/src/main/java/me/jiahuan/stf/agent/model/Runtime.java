package me.jiahuan.stf.agent.model;

import me.jiahuan.stf.agent.pojo.DeviceInfo;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Runtime {
    // 设备列表
    public static List<DeviceInfo> deviceList = new ArrayList<>();

    public static ConcurrentHashMap<String, Session> deviceSessionHashMap = new ConcurrentHashMap<>();
}
