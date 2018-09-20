package com.yang.springbootwebsocket.config;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: Yang
 * @date: 2018/9/20 23:21
 * @description:
 */
@Data
public final class WebsocketSessionContainer {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Set<WebsocketServer>>> sessionBox;

    private WebsocketSessionContainer(ConcurrentHashMap<String, ConcurrentHashMap<String, Set<WebsocketServer>>> sessionBox) {
        super();
        this.sessionBox = sessionBox;
    }

    public static WebsocketSessionContainer getInstance() {
        return Holder.container;
    }

    private static class Holder {
        public static final WebsocketSessionContainer container = new WebsocketSessionContainer(new ConcurrentHashMap<>(32));
    }

    public ConcurrentHashMap<String, ConcurrentHashMap<String, Set<WebsocketServer>>> getSessionBox() {
        return sessionBox;
    }

    public static void main(String[] args) {
        Set<WebsocketServer> websocketServerSet = new LinkedHashSet<>(128);
        ConcurrentHashMap<String, Set<WebsocketServer>> inMap = new ConcurrentHashMap<>(64);
        inMap.put("1", websocketServerSet);
        WebsocketSessionContainer.getInstance().getSessionBox().put("exam", inMap);

        WebsocketSessionContainer.getInstance().getSessionBox().get("");
    }

    public void addSession(String biz, String entity, WebsocketServer websocketServer) {
        WebsocketSessionContainer container = WebsocketSessionContainer.getInstance();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Set<WebsocketServer>>> sessionBox = container.getSessionBox();
        ConcurrentHashMap<String, Set<WebsocketServer>> innerMap = sessionBox.get(biz);

        if (innerMap == null) {
            Set<WebsocketServer> websocketServerSet = new LinkedHashSet<>(128);
            websocketServerSet.add(websocketServer);
            innerMap = new ConcurrentHashMap<>(64);
            innerMap.put(entity, websocketServerSet);
            sessionBox.put(biz, innerMap);
        } else {
            Set<WebsocketServer> websocketServerSet = innerMap.get(entity);
            if (websocketServerSet == null) {
                websocketServerSet = new LinkedHashSet<>(128);
                websocketServerSet.add(websocketServer);
                innerMap.put(entity, websocketServerSet);
            }
        }
    }

    public void removeSession(String biz, String entity, WebsocketServer websocketServer) {
        WebsocketSessionContainer container = WebsocketSessionContainer.getInstance();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Set<WebsocketServer>>> sessionBox = container.getSessionBox();
        ConcurrentHashMap<String, Set<WebsocketServer>> innerMap = sessionBox.get(biz);
        if (innerMap != null) {
            Set<WebsocketServer> websocketServerSet = innerMap.get(entity);
            if (websocketServerSet != null) {
                websocketServerSet.remove(websocketServer);
            }
            if (websocketServerSet.isEmpty()) {
                innerMap.remove(entity);
            }
        }
        if (innerMap.isEmpty()) {
            sessionBox.remove(biz);
        }
    }

    /*public Set<WebsocketServer> send(String biz, String entity) {
        WebsocketSessionContainer container = WebsocketSessionContainer.getInstance();
        ConcurrentHashMap<String, ConcurrentHashMap<String, Set<WebsocketServer>>> sessionBox = container.getSessionBox();
        ConcurrentHashMap<String, Set<WebsocketServer>> innerMap = sessionBox.get(biz);
        innerMap.get(entity);
    }*/

}
