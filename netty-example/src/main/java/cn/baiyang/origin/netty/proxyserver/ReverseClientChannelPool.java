/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.proxyserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-19 23:59
 */
public class ReverseClientChannelPool extends SimpleChannelPool {

    public ReverseClientChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
        super(bootstrap, handler);
    }

    public void setEventLoopGroup(EventLoopGroup eventLoopGroup) {
        BootstrapConfig config = bootstrap().config();
        EventLoopGroup group = config.group();
        if (null == group) {
            bootstrap().group(eventLoopGroup);
        }
    }
}
