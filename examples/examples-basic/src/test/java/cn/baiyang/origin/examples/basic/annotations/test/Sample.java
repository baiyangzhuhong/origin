/*
 * Copyright (c) 2001-2021 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package cn.baiyang.origin.examples.basic.annotations.test;

import cn.baiyang.origin.examples.basic.annotations.Test;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2021-06-10 10:03
 */
public class Sample {

    @Test
    public static void m1() {
    } // Test should pass

    public static void m2() {
    }

    @Test
    public static void m3() { // Test should fail
        throw new RuntimeException("Boom");
    }

    public static void m4() {
    }

    @Test
    public void m5() {
    } // INVALID USE: nonstatic method

    public static void m6() {
    }

    @Test
    public static void m7() { // Test should fail
        throw new RuntimeException("Crash");
    }

    public static void m8() {
    }

}
