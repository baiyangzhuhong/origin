/*
 * Copyright (c) 2001-2021 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */
package cn.baiyang.origin.examples.basic.enums.test;

import java.util.Arrays;
import java.util.Collection;

import cn.baiyang.origin.examples.basic.enums.BasicOperation;
import cn.baiyang.origin.examples.basic.enums.ExtendedOperation;
import cn.baiyang.origin.examples.basic.enums.Operation;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2021-06-09 18:19
 */
public class EnumWithInterfaceTest {

    public static void main(String[] args) {
        double x = Double.parseDouble(args[0]);
        double y = Double.parseDouble(args[1]);

        test(ExtendedOperation.class, x, y);

        test(BasicOperation.class, x, y);

        test(Arrays.asList(ExtendedOperation.values()), x, y);
    }

    private static <T extends Enum<T> & Operation> void test(
        Class<T> opEnumType, double x, double y) {
        for (Operation op : opEnumType.getEnumConstants())
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        System.out.println();
    }

    private static void test(Collection<? extends Operation>
        opSet, double x, double y) {
        for (Operation op : opSet)
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        System.out.println();
    }

}
