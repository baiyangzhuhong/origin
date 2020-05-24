/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.proxyserver;

import static cn.baiyang.origin.netty.proxyserver.ReverseProxyInboundHandler.ORI_CTX;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-12 22:06
 */
@ChannelHandler.Sharable
public class ReverseProxyClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpResponse response = (FullHttpResponse) msg;
        DecoderResult decoderResult = response.decoderResult();
        if (decoderResult.isFailure()) {
            decoderFailureProxyResponse(ctx);
            return;
        }

        inspectResponse(response);

        ChannelHandlerContext oriCtx = ctx.channel().attr(ORI_CTX).get();
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        ChannelFuture future = oriCtx.channel().writeAndFlush(response);
        /* Not Keep Alive
        future.addListener(ChannelFutureListener.CLOSE);
        ctx.close();
         */
    }

    private void inspectResponse(FullHttpResponse response) {
        HttpVersion httpVersion = response.protocolVersion();
        HttpResponseStatus httpResponseStatus = response.status();
        System.out.println(String.format("httpVersion:%s; responseStatus:%s", httpVersion.text(), httpResponseStatus.code()));

        HttpHeaders responseHeaders = response.headers();
        for (String name: responseHeaders.names()) {
            for (String value: responseHeaders.getAll(name)) {
                System.err.println("HEADER: " + name + " = " + value);
            }
        }

        ByteBuf responseContent = response.content();
        System.out.println(responseContent.toString(Charset.forName("UTF-8")));
    }

    private void decoderFailureProxyResponse(ChannelHandlerContext ctx) {
        DefaultFullHttpResponse decoderFailureProxyResponse = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        String decoderFailureResponseBody = "{\"code\":1,\"success\":false, \"message\":\"Proxy Response Decode Failed\"}";
        ByteBuf byteBuf = Unpooled.copiedBuffer(decoderFailureResponseBody, Charset.forName("UTF-8"));
        decoderFailureProxyResponse.content().writeBytes(byteBuf);
        HttpHeaders responseHeaders = decoderFailureProxyResponse.headers();
        responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, decoderFailureProxyResponse.content().readableBytes());

        ChannelHandlerContext oriCtx = ctx.channel().attr(ORI_CTX).get();
        ChannelFuture channelFuture = oriCtx.channel().writeAndFlush(decoderFailureProxyResponse);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ctx.close();
                future.channel().close();
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
