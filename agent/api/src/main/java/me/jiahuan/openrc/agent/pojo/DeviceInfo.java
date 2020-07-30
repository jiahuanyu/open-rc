package me.jiahuan.openrc.agent.pojo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class DeviceInfo {
    private String deviceId;
    private String manufacturer;
    private String model;
    private String os;
    private String osVersion;
    private Size size;
}
