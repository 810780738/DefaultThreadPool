package com.xiaozhu.ThreadPoolHttpServer;

import com.xiaozhu.ThreadPoolHttpServer.thread.DefaultThreadPool;
import com.xiaozhu.ThreadPoolHttpServer.thread.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleHttpServer {

    static ThreadPool<HttpRequestHandler> threadPool = new DefaultThreadPool<HttpRequestHandler>(1);

    static String basePath;

    static ServerSocket serverSocket;
    static int port =8080;

    public static void setPort(int port){
        if (port > 0){
            SimpleHttpServer.port = port;
        }
    }

    public static void setBasePath(String basePath){
        if (null != basePath && new File(basePath).exists() && new File(basePath).isDirectory()){
            SimpleHttpServer.basePath = basePath;
        }
    }

    public static void start() throws IOException {
        serverSocket = new ServerSocket(port);
        Socket socket = null;
        while ((socket = serverSocket.accept()) != null){
            threadPool.execute(new HttpRequestHandler(socket));
        }
        serverSocket.close();
    }

    public static void main(String[] args) throws IOException {
        start();
    }
}
