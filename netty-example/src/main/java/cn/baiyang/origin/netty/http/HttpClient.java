/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.http;

import java.net.URI;
import java.nio.charset.Charset;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-10 22:34
 */
public class HttpClient {

    static final String URL = System.getProperty("url", "http://127.0.0.1:8080/?name=zhangsan");

    public static void main(String[] args) throws Exception {
        URI uri = new URI(URL);
        String scheme = uri.getScheme() == null? "http" : uri.getScheme();
        String host = uri.getHost() == null? "127.0.0.1" : uri.getHost();
        int port = uri.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new HttpClientChannelInitializer(sslCtx));

            // Make the connection attempt.
            Channel ch = b.connect(host, port).sync().channel();

            String body = "{\"name\": \"zhangsan\", \"age\": 22, \"gender\": \"m\"}";
//            ByteBuf byteBuf = Unpooled.copiedBuffer(body, Charset.forName("UTF-8"));
            ByteBuf byteBuf = Unpooled.wrappedBuffer(body.getBytes(), 0, body.length());
            // Prepare the HTTP request.
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toString(), byteBuf);
            HttpHeaders requestHeaders = request.headers();
            requestHeaders.set(HttpHeaderNames.HOST, host);
            requestHeaders.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            requestHeaders.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
            requestHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            requestHeaders.set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());

//            QueryStringEncoder queryEncoder = new QueryStringEncoder("");

            // Set some example cookies.
            requestHeaders.set(
                HttpHeaderNames.COOKIE,
                ClientCookieEncoder.STRICT.encode(
                    new DefaultCookie("my-cookie", "foo"),
                    new DefaultCookie("another-cookie", "bar")));

            // Send the HTTP request.
            ch.writeAndFlush(request);

            // Wait for the server to close the connection.
            ch.closeFuture().sync();
        } finally {
            // Shut down executor threads to exit.
            group.shutdownGracefully();
        }
    }

}
