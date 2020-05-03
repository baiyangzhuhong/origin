/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-03 22:12
 */
public class TimeServerHandlerWithPojo extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ChannelFuture f = ctx.writeAndFlush(new UnixTime());
        f.addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
