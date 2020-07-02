package me.jiahuan.stf.agent.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import me.jiahuan.stf.agent.model.Runtime;
import me.jiahuan.stf.agent.pojo.DeviceInfo;
import me.jiahuan.stf.agent.pojo.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.UUID;

@RestController
@ServerEndpoint("/ws/device")
public class DeviceWebSocketController {
    private final static Gson GSON = new Gson();

    private final Logger logger = LoggerFactory.getLogger(DeviceWebSocketController.class);

    private final String deviceId = UUID.randomUUID().toString();


    @OnOpen
    public void onOpen(Session session) {
        logger.info("onOpen deviceId = " + deviceId);
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("onMessage message = " + message + ", session = " + session);
        WebSocketMessage webSocketMessage = null;
        try {
            webSocketMessage = GSON.fromJson(message, WebSocketMessage.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (webSocketMessage == null) {
            return;
        }

        final String name = webSocketMessage.getName();

        if (name == null) {
            return;
        }

        if ("device_join".equals(name)) {
//            if (deviceInfo != null) {
//                deviceInfo.setDeviceId(deviceId);
//                Runtime.deviceList.add(deviceInfo);
//                logger.info("有新设备加入 deviceInfo = " + deviceInfo);
//            }
        }
    }

    @OnMessage
    public void onMessage(Session session, byte[] messages) {

    }

    @OnClose
    public void onClose(Session session) {
        logger.info("onClose session = " + session);
        Runtime.deviceSessionHashMap.remove(deviceId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("onError error = " + error.getMessage() + ", session = " + session);
        // onError 会继续走 onClose
    }
}
