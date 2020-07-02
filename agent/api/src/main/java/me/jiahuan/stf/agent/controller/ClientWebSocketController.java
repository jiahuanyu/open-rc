package me.jiahuan.stf.agent.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@RestController
@ServerEndpoint("/ws/client")
public class ClientWebSocketController {

    private final Logger logger = LoggerFactory.getLogger(ClientWebSocketController.class);

    private static CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet();

    private Session session;

    @OnOpen
    public void onOpen(Session session) {
        logger.info("onOpen");
        this.session = session;
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("onMessage message = " + message);
    }

    @OnClose
    public void onClose() {
        logger.info("onClose");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("onError");
    }
}
