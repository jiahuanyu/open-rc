package me.jiahuan.openrc.agent.pojo;

import com.google.gson.JsonElement;
import lombok.Data;

@Data
public class WebSocketMessage {
    private String name;
    private String guid;
    private JsonElement data;
}
