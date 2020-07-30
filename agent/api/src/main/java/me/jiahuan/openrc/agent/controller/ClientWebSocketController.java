package me.jiahuan.openrc.agent.controller;

import me.jiahuan.openrc.agent.runtime.Runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;

@RestController
@ServerEndpoint("/ws/client")
public class ClientWebSocketController {

    private final Logger logger = LoggerFactory.getLogger(ClientWebSocketController.class);

    private String deviceId;

    @OnOpen
    public void onOpen(Session session) {
        logger.info("client onOpen");
        logger.info(session.getRequestParameterMap().toString());
        List<String> deviceIds = session.getRequestParameterMap().get("deviceId");
        if (deviceIds != null && deviceIds.size() > 0) {
            deviceId = deviceIds.get(0);
            if (deviceId != null) {
                Runtime.clientSessionHashMap.put(deviceId, session);
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        logger.info("client onMessage message = " + message);
        Session deviceSession = Runtime.deviceSessionHashMap.get(deviceId);
        if(deviceSession == null) {
            return;
        }
        try {
            deviceSession.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        logger.info("client onClose");
        Runtime.clientSessionHashMap.remove(deviceId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("client onError");
    }
}
