package me.jiahuan.stf.agenter.model;

import me.jiahuan.stf.agenter.pojo.DeviceInfo;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Runtime {
    public static List<DeviceInfo> deviceList = new ArrayList<>();

    public static ConcurrentHashMap<String, Session> deviceSessionHashMap = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Session> clientSessionHashMap = new ConcurrentHashMap<>();
}
