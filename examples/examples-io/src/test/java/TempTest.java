/*
 * Copyright (c) 2001-2020 GuaHao.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GuaHao Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GuaHao.com.
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

/**
 * TODO
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-11-25 23:42
 */
public class TempTest {

    static final String TCP_FW_SESSION_FILE = "/Users/hongzhu/Downloads/xy_fw_session.log";
    static final String NGINX_ACCESS_LOG_FILE = "/Users/hongzhu/Downloads/xyaccess_his.log";

    public static void main(String[] args) {
        System.out.println(new Random().nextInt(3));

        /*

        Set<String> ipList = new TreeSet<>();
        List<String> tcpFWSessions = populateList(TCP_FW_SESSION_FILE);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(NGINX_ACCESS_LOG_FILE)));
            for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                int lastSpace = line.lastIndexOf(" ");
                String remotePort = line.substring(lastSpace + 2, line.length() - 1);
                String target = String.format("[%s:%s]", "115.238.25.33", remotePort);
                tcpFWSessions.forEach(tcpSession -> {
                    if (tcpSession.contains(target)) {
//                        System.out.println(tcpSession);
                        int end = tcpSession.indexOf(target);
                        String addr = tcpSession.substring(31, end);
                        String ip = addr.substring(0, addr.indexOf(":"));
                        ipList.add(ip);
                    }
                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(ipList);

         */
    }

    private static List<String> populateList(String tcpFwSessionFile) {
        List<String> tcpFWSessions = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(tcpFwSessionFile)));
            for(String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                if (line.length() != 0 && line.startsWith(" http  VPN: public --> public")) {
                    tcpFWSessions.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return tcpFWSessions;
        } catch (IOException e) {
            e.printStackTrace();
            return tcpFWSessions;
        }
        return tcpFWSessions;
    }

}
