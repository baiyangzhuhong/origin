/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.examples.io.blocking;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-04 11:57
 */
public class EchoServer {

    public static void serve(int portNumber) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);

        while (true) {
            final Socket clientSocket = serverSocket.accept();

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream()));
                        PrintWriter out =
                            new PrintWriter(clientSocket.getOutputStream(), true);
                        String request, response;
                        while ((request = in.readLine()) != null) {
                            if ("Done".equals(request)) {
                                clientSocket.close();
                                break;
                            }
                            response = processRequest(request);
                            out.println(response);
                        }

                        Thread.currentThread().interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setName("_my_client_socket_thread");
            t.start();
        }

    }

    private static String processRequest(String request){
        return request;
    }

    public static void main(String[] args) {
        try {
            serve(8088);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
