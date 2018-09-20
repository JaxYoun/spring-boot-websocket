package com.yang.springbootwebsocket.websocket;

import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Auther: Yang
 * @Date: 2018/8/14 22:24
 * @Description:
 */
//@Component
//@ServerEndpoint("/user/websocket/${id}")
public class UserWebsocketServer {

    private Session session;

    private static final List<UserWebsocketServer> userWebsocketServerList = new CopyOnWriteArrayList<UserWebsocketServer>();

    /**
     * 发生客户端连接事件时调用
     *
     * @param session
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") Integer id) {
        System.out.println(id + "----------");
        this.session = session;
        userWebsocketServerList.add(this);
    }

    /**
     * 发生客户端连接断开事件时调用
     */
    @OnClose
    public void onClose() {
        userWebsocketServerList.remove(this);
    }

    /**
     * 有客户端消息发来时调用
     *
     * @param message
     */
    @OnMessage
    public void onMessage(String message, @PathParam("userId") Integer userId) {
        System.out.println(userId + "=========");
        for (UserWebsocketServer webSocketServer : userWebsocketServerList) {  //群发消息
            try {
                sendMessage(webSocketServer, message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 当发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     * 将消息发送到客户端
     *
     * @param webSocketServer
     * @param message
     * @throws IOException
     */
    public void sendMessage(UserWebsocketServer webSocketServer, String message) throws IOException {
        webSocketServer.session.getBasicRemote().sendText(message);
    }

}
