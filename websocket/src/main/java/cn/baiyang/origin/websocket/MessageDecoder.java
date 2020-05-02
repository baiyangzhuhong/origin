package cn.baiyang.origin.websocket;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.Gson;

import cn.baiyang.origin.websocket.module.Message;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-04-21 16:21
 */
public class MessageDecoder implements Decoder.Text<Message> {

    private static Gson gson = new Gson();

    public Message decode(String s) throws DecodeException {
        Message message = gson.fromJson(s, Message.class);
        return message;
    }

    @Override
    public boolean willDecode(String s) {
        return (s != null);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        // Custom initialization logic
    }

    @Override
    public void destroy() {
        // Close resources
    }
}
