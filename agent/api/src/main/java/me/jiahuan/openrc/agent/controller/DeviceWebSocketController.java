package me.jiahuan.openrc.agent.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import me.jiahuan.openrc.agent.runtime.Runtime;
import me.jiahuan.openrc.agent.pojo.DeviceInfo;
import me.jiahuan.openrc.agent.pojo.WebSocketMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.nio.ByteBuffer;
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
        session.setMaxBinaryMessageBufferSize(1024000);
        session.setMaxTextMessageBufferSize(1024000);
        session.setMaxIdleTimeout(0);
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
            DeviceInfo deviceInfo = GSON.fromJson(webSocketMessage.getData(), DeviceInfo.class);
            deviceInfo.setDeviceId(deviceId);
            Runtime.deviceList.add(deviceInfo);
            Runtime.deviceSessionHashMap.put(deviceId, session);
        }
    }

    @OnMessage
    public void onMessage(Session session, byte[] messages) {
        Session clientSession = Runtime.clientSessionHashMap.get(deviceId);
        if (clientSession == null) {
            return;
        }
        try {
            clientSession.getBasicRemote().sendBinary(ByteBuffer.wrap(messages));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("onClose session = " + session);
        Runtime.deviceList.removeIf(deviceInfo -> deviceId.equals(deviceInfo.getDeviceId()));
        Runtime.deviceSessionHashMap.remove(deviceId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("onError error = " + error.getMessage() + ", session = " + session);
        // onError 会继续走 onClose
    }
}
