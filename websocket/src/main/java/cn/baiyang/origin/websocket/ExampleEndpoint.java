package cn.baiyang.origin.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-04-21 16:19
 */
@ServerEndpoint(value = "/example/{username}")
public class ExampleEndpoint {
    private Session session;
    private static final Set<ExampleEndpoint> chatEndpoints = new CopyOnWriteArraySet<>();
    private static HashMap<String, String> users = new HashMap<>();
    private static final long NEVER = 0;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) throws IOException, EncodeException {

        this.session = session;
        session.setMaxIdleTimeout(NEVER);
        chatEndpoints.add(this);
        users.put(session.getId(), username);

        broadcast(username + " Connected!");
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException, EncodeException {
        broadcast(users.get(session.getId())+ message);
    }

    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        chatEndpoints.remove(this);
        broadcast(users.get(session.getId()) + "Disconnected!");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(String message) throws IOException, EncodeException {
        chatEndpoints.forEach(endpoint -> {
            try {
                endpoint.session.getBasicRemote()
                    .sendObject(message);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        });
    }

}
