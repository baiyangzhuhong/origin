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
import static io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;

/**
 * In the pipeline, if you want to make handler process chained, you should call ChannelHandlerContext#fireXX method to
 * deliver to the next handler manually or you just call super#channelXXX method.<br/>
 *
 * And attention to the invoke sequence.
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-08-21 13:20
 */
public class TestPipelineChainHandler extends ChannelInboundHandlerAdapter {
    public TestPipelineChainHandler() {
        super();
    }

    @Override
    protected void ensureNotSharable() {
        super.ensureNotSharable();
    }

    @Override
    public boolean isSharable() {
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler handler added");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler handler removed");
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler channel registered");
        ctx.fireChannelRegistered();
//        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler channel unregistered");
        ctx.fireChannelUnregistered();
//        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler channel active");
        ctx.fireChannelActive();
//        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler channel inactive");
        ctx.fireChannelInactive();
//        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("TestPipelineChainHandler channel read");

        /**
         * Exception Scenario
         */
//        if (true) {
//            throw new TestPipelineChainException("failed");
//        }

        /**
         * Successful Scenario
         */
//        FullHttpRequest request = (FullHttpRequest) msg;
//        HttpVersion version = request.protocolVersion();
//        String content = "{\"code\":0,\"success\":true}";
//        ByteBuf result = Unpooled.wrappedBuffer(content.getBytes(), 0, content.length());
//        FullHttpResponse response = new DefaultFullHttpResponse(version, OK, result);
//        HttpHeaders responseHeaders = response.headers();
//        responseHeaders
//            .set(CONTENT_TYPE, APPLICATION_JSON)
//            .setInt(CONTENT_LENGTH, response.content().readableBytes());
//        boolean keepAlive = HttpUtil.isKeepAlive(request);
//        if (keepAlive) {
//            if (!version.isKeepAliveDefault()) {
//                responseHeaders.set(CONNECTION, KEEP_ALIVE);
//            }
//        } else {
//            responseHeaders.set(CONNECTION, CLOSE);
//        }
//        ChannelFuture f = ctx.writeAndFlush(response);
//        if (!keepAlive) {
//            f.addListener(ChannelFutureListener.CLOSE);
//        }

        ctx.fireChannelRead(msg);
//        super.channelRead(ctx, msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler channel read complete");
        ctx.fireChannelReadComplete();
//        super.channelReadComplete(ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println("TestPipelineChainHandler user event triggered");
        ctx.fireUserEventTriggered(evt);
//        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println("TestPipelineChainHandler channel writability");
        ctx.fireChannelWritabilityChanged();
//        super.channelWritabilityChanged(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("TestPipelineChainHandler exception caught");
        ctx.fireExceptionCaught(cause);
//        super.exceptionCaught(ctx, cause);
    }
}
