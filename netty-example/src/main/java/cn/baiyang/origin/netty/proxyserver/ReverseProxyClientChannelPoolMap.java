/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.proxyserver;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.kqueue.KQueueSocketChannel;
import io.netty.channel.pool.AbstractChannelPoolMap;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-19 23:05
 */
public class ReverseProxyClientChannelPoolMap extends AbstractChannelPoolMap<InetSocketAddress, ReverseClientChannelPool> {

    @Override
    protected ReverseClientChannelPool newPool(InetSocketAddress key) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(KQueueSocketChannel.class)
            .option(ChannelOption.SO_KEEPALIVE, true);
        return new ReverseClientChannelPool(bootstrap.remoteAddress(key), new ReverseProxyClientChannelPoolHandler());
    }

}
