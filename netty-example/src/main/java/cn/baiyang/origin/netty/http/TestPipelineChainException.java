/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.netty.http;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-08-21 14:20
 */
public class TestPipelineChainException extends RuntimeException {
    public TestPipelineChainException() {
        super();
    }

    public TestPipelineChainException(String message) {
        super(message);
    }

    public TestPipelineChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestPipelineChainException(Throwable cause) {
        super(cause);
    }

    protected TestPipelineChainException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
