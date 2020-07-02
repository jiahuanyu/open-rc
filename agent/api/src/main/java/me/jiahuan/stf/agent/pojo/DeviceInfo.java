package me.jiahuan.stf.agent.pojo;

import lombok.Data;

@Data
public class DeviceInfo {
    private String deviceId;
    private String manufacturer;
    private String model;
    private String os;
    private String osVersion;
}
