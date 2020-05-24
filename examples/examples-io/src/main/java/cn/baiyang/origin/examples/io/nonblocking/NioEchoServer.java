/*
 * Copyright (c) 2001-2020 GH.com Corporation Limited. All rights reserved.
 * This software is the confidential and proprietary information of GH Company.
 * ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with GH.com.
 */
package cn.baiyang.origin.examples.io.nonblocking;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 *
 *
 * @author hongzhu
 * @version V1.0
 * @since 2020-05-04 11:57
 */
public class NioEchoServer {

    public static void serve(int port) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket ss = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        ss.bind(address);
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

//        boolean writeOn = true;
        for (;;){
            try {
                int numOfSelectedKeys = selector.select();
                if (numOfSelectedKeys == 0) {
                    continue;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                //handle exception
                break;
            }

//            writeOn = eventLoop(selector, writeOn);
            eventLoop(selector);
        }

    }

    private static void eventLoop(Selector selector) {
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator();
        while (iterator.hasNext()) {
            SelectionKey selectionKey = iterator.next();
            try {
                StringBuilder builder = new StringBuilder();
                if (selectionKey.isAcceptable()) {
                    ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);
                    socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ | SelectionKey.OP_CONNECT);
                    System.out.println("Accepted connection from " + socketChannel);

                }
                if (selectionKey.isConnectable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    System.out.println("connectable connection from " + socketChannel);

                }
                if (selectionKey.isReadable()) {
                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                    System.out.println("readable connection from " + socketChannel);

                    ByteBuffer readBuffer = ByteBuffer.allocate(256);
                    int num = socketChannel.read(readBuffer);
                    while (num != -1 && num != 0) {
                        builder.append(new String(readBuffer.array(), 0, num).trim());
                        readBuffer.clear();
                        num = socketChannel.read(readBuffer);
                    }
                    String message = builder.toString();
                    if (message.equals("Done")) {
                        socketChannel.close();
                        break;
                    }

                    message = message + "\n";
                    ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes(), 0, message.length());
                    socketChannel.write(writeBuffer);
                    writeBuffer.clear();

//                    writeOn = true;
                }

//                if (selectionKey.isWritable() && writeOn) {
//                    writeOn = false;
//
//                    SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
//                    System.out.println("writable connection from " + socketChannel);
//
//                    String message = builder.toString();
//                    message = message.isEmpty() ? "Hi" : message;
//                    message = message + "\n";
//                    ByteBuffer writeBuffer = ByteBuffer.wrap(message.getBytes(), 0, message.length());
//                    socketChannel.write(writeBuffer);
//                    writeBuffer.clear();
//                }

            } catch (IOException ex) {
                selectionKey.cancel();
                try {
                    selectionKey.channel().close();
                } catch (IOException cex) {
                    // ignore on close
                }
            }
            iterator.remove();
        }

//        return writeOn;
    }

    public static void main(String[] args) {
        try {
            serve(8088);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static Process start() throws IOException, InterruptedException {
//        String javaHome = System.getProperty("java.home");
//        String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
//        String classpath = System.getProperty("java.class.path");
//        String className = NioEchoServer.class.getCanonicalName();
//
//        ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
//
//        return builder.start();
//    }

}
