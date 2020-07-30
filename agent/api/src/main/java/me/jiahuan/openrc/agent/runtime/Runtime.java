package me.jiahuan.openrc.agent.runtime;

import me.jiahuan.openrc.agent.pojo.DeviceInfo;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Runtime {
    public static List<DeviceInfo> deviceList = new ArrayList<>();

    public static ConcurrentHashMap<String, Session> deviceSessionHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Session> clientSessionHashMap = new ConcurrentHashMap<>();
}
