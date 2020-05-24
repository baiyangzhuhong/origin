/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.proxyserver;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.AbstractChannelPoolHandler;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-19 23:10
 */
public class ReverseProxyClientChannelPoolHandler extends AbstractChannelPoolHandler {

    private static final ReverseProxyClientInboundHandler proxyClientInboundHandler = new ReverseProxyClientInboundHandler();

    @Override
    public void channelCreated(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpClientCodec());
        pipeline.addLast(new HttpObjectAggregator(512 * 1024));
        pipeline.addLast(proxyClientInboundHandler);
    }

    @Override
    public void channelAcquired(Channel ch) throws Exception {
        super.channelAcquired(ch);
    }

    @Override
    public void channelReleased(Channel ch) throws Exception {
        super.channelReleased(ch);
    }
}
