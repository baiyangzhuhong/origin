/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.http;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-10 22:37
 */
public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpClientInboundHandler is registered");
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpClientInboundHandler is unregistered");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpClientInboundHandler is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpClientInboundHandler is inactive");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse httpResponse = (FullHttpResponse) msg;
        HttpVersion httpVersion = httpResponse.protocolVersion();
        HttpResponseStatus httpResponseStatus = httpResponse.status();
        System.out.println(String.format("httpVersion:%s; responseStatus:%s", httpVersion.text(), httpResponseStatus.code()));

        HttpHeaders responseHeaders = httpResponse.headers();
        for (String name: responseHeaders.names()) {
            for (String value: responseHeaders.getAll(name)) {
                System.err.println("HEADER: " + name + " = " + value);
            }
        }

        ByteBuf responseContent = httpResponse.content();
        System.out.println(responseContent.toString(Charset.forName("UTF-8")));
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpClientInboundHandler read complete");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpClientInboundHandler handler added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("HttpClientInboundHandler handler removed");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
