package com.yang.springbootwebsocket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * @author: Yang
 * @date: 2018/9/20 22:59
 * @description:
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{biz}/{entity}")
public class WebsocketServer {

    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
//    private static CopyOnWriteArraySet<WebsocketServer> webSocketSet = new CopyOnWriteArraySet<WebsocketServer>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //接收sid
    private String biz;

    private String entity;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("biz") String biz, @PathParam("entity") String entity) {
        this.biz = biz;
        this.entity = entity;
        this.session = session;
        this.addSession(biz, entity, this);

        addOnlineCount();
        sendMessage("连接成功");
        log.info("有新窗口开始监听:" + biz + ",当前在线人数为" + getOnlineCount());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        this.removeSession(this.biz, this.entity, this);
        subOnlineCount();
        log.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        this.sendMessage(message);
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 群发自定义消息
     */
    public void sendInfo(String biz, String entity, String message) throws IOException {
        this.send(biz, entity, message);
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebsocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebsocketServer.onlineCount--;
    }

    public void addSession(String biz, String entity, WebsocketServer websocketServer) {
        WebsocketSessionContainer.getInstance().addSession(biz, entity, websocketServer);
    }

    public void removeSession(String biz, String entity, WebsocketServer websocketServer) {
        WebsocketSessionContainer.getInstance().removeSession(biz, entity, websocketServer);

    }

    public void send(String biz, String entity, String message) {
        WebsocketSessionContainer.getInstance().getSessionBox().get(biz).get(entity).forEach(it -> it.sendMessage(message));
    }

}
