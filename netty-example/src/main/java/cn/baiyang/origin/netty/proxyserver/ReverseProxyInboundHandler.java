/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.proxyserver;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOption;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-12 13:09
 */
@ChannelHandler.Sharable
public class ReverseProxyInboundHandler extends ChannelInboundHandlerAdapter {
    static final AttributeKey<ChannelHandlerContext> ORI_CTX = AttributeKey.valueOf("OriginalCtx");
    /* Not Pool Solution
    private static final ReverseProxyClientChannelInitializer proxyClientChannelInitializer = new ReverseProxyClientChannelInitializer();
     */
    static final ReverseProxyClientChannelPoolMap CHANNEL_POOL_MAP = new ReverseProxyClientChannelPoolMap();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        DecoderResult decoderResult = request.decoderResult();
        if (decoderResult.isFailure()) {
            decoderFailureResponse(ctx);
            return;
        }

        inspectRequest(request);

        InetSocketAddress address = route(request);
        /* Not Pool Solution
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
            .channel(KQueueSocketChannel.class)
            .handler(proxyClientChannelInitializer);
        bootstrap.connect(address).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                future.channel().attr(ORI_CTX).set(ctx);
                future.channel().writeAndFlush(request);
            }
        });
         */

        ReverseClientChannelPool channelPool = CHANNEL_POOL_MAP.get(address);
        channelPool.setEventLoopGroup(ctx.channel().eventLoop());
        Future<Channel> future = channelPool.acquire();
        future.addListener(new FutureListener<Channel>() {
            @Override
            public void operationComplete(Future<Channel> f) throws Exception {
                if (f.isSuccess()) {
                    Channel channel = f.getNow();
                    channel.attr(ORI_CTX).set(ctx);

                    HttpHeaders headers = request.headers();
                    headers.set(HttpHeaderNames.HOST, address.getHostName());
                    headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

                    channel.writeAndFlush(request);

                    channelPool.release(channel);
                }
            }
        });

    }

    private InetSocketAddress route(FullHttpRequest request) {
        return new InetSocketAddress("www.baidu.com", 80);
    }

    private void inspectRequest(FullHttpRequest request) {
        HttpVersion version = request.protocolVersion();
        HttpMethod method = request.method();
        String uri = request.uri();
        HttpHeaders headers = request.headers();
        System.out.println(String.format("httpVersion: %s, httpMethod: %s, uri: %s, headerSize: %d",
            version.text(), method.name(), uri, headers.size()));

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
    }

    private void decoderFailureResponse(ChannelHandlerContext ctx) {
        DefaultFullHttpResponse decoderFailureResponse = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
        String decoderFailureResponseBody = "{\"code\":1,\"success\":false, \"message\":\"Request Decode Failed\"}";
        ByteBuf byteBuf = Unpooled.copiedBuffer(decoderFailureResponseBody, Charset.forName("UTF-8"));
        decoderFailureResponse.content().writeBytes(byteBuf);
        HttpHeaders responseHeaders = decoderFailureResponse.headers();
        responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, decoderFailureResponse.content().readableBytes());
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(decoderFailureResponse);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                System.out.println("Decode Failed Response Finish");
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
