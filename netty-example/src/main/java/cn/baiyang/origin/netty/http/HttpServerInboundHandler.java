/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.http;

import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.util.CharsetUtil;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-05 21:17
 */
@ChannelHandler.Sharable
public class HttpServerInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler channel registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler channel unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler channel active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler channel inactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        HttpVersion version = request.protocolVersion();
        HttpMethod method = request.method();
        String uri = request.uri();
        HttpHeaders headers = request.headers();
        System.out.println(String.format("httpVersion: %s, httpMethod: %s, uri: %s, headerSize: %d",
            version.text(), method.name(), uri, headers.size()));

        URI uriTool = new URI(uri);
        String path = uriTool.getPath();
        String rawPath = uriTool.getRawPath();
        System.out.println(String.format("Path: %s, RawPath: %s", path, rawPath));

        StringBuilder builder = new StringBuilder();
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri);
        Map<String, List<String>> params = queryStringDecoder.parameters();
        if (!params.isEmpty()) {
            for (Map.Entry<String, List<String>> p: params.entrySet()) {
                String key = p.getKey();
                List<String> vals = p.getValue();
                for (String val : vals) {
                    builder.append("PARAM: ").append(key).append(" = ").append(val).append("\n");
                }
            }
        }
        System.out.println(String.format("Query Params:\n%s", builder.toString()));

        ByteBuf requestContent = request.content();
        System.out.println(String.format("request content:\n%s", requestContent.toString(Charset.forName("UTF-8"))));

        DecoderResult decoderResult = request.decoderResult();
        System.out.println(String.format("finished:%s, success:%s", decoderResult.isFinished(), decoderResult.isSuccess()));
        if (decoderResult.isFailure()) {
            decoderResult.cause().printStackTrace();
        }

        boolean keepAlive = HttpUtil.isKeepAlive(request);
        String content = uri.equals("/") ? "{\"code\":0,\"success\":true}"
            : "{\"code\":1,\"success\":false, \"message\":\"Not Supported\"}";
        ByteBuf result = Unpooled.wrappedBuffer(content.getBytes(), 0, content.length());
//        ByteBuf result = Unpooled.copiedBuffer(content.toString(), CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(version, OK, result);
        HttpHeaders responseHeaders = response.headers();
        responseHeaders
            .set(CONTENT_TYPE, APPLICATION_JSON)
            .setInt(CONTENT_LENGTH, response.content().readableBytes());

        // Encode the cookie.
        String cookieString = headers.get(HttpHeaderNames.COOKIE);
        if (cookieString != null) {
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieString);
            if (!cookies.isEmpty()) {
                // Reset the cookies if necessary.
                for (Cookie cookie: cookies) {
                    responseHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode(cookie));
                }
            }
        } else {
            // Browser sent no cookie.  Add some.
            responseHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key1", "value1"));
            responseHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.STRICT.encode("key2", "value2"));
        }

        if (keepAlive) {
            if (!version.isKeepAliveDefault()) {
                responseHeaders.set(CONNECTION, KEEP_ALIVE);
            }
        } else {
            // Tell the client we're going to close the connection.
            responseHeaders.set(CONNECTION, CLOSE);
        }

        ChannelFuture f = ctx.writeAndFlush(response);
        if (!keepAlive) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler channel read complete");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("HttpServerInboundHandler user event triggered");
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler channel writability");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("HttpServerInboundHandler exception caught");
        cause.printStackTrace();
        ctx.writeAndFlush("Errored");
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler handler added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpServerInboundHandler handler removed");
    }
}
