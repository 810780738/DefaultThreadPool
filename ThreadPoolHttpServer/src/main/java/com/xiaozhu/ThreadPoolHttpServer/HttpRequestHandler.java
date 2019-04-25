package com.xiaozhu.ThreadPoolHttpServer;

import java.io.*;
import java.net.Socket;

public class HttpRequestHandler implements Runnable {
    private Socket socket;

    public HttpRequestHandler(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        String line = null;
        BufferedReader br = null;
        BufferedReader reader = null;
        PrintWriter out = null;
        InputStream in = null;
        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String header = reader.readLine();
            String filepath = SimpleHttpServer.basePath + header.split(" ")[1];
            out = new PrintWriter(socket.getOutputStream());
            if (filepath.endsWith("jpg") || filepath.endsWith("ico")){
                in = new FileInputStream(filepath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int i =  0;
                while ((i = in.read()) != -1){
                    baos.write(i);
                }
                byte[] bytes = baos.toByteArray();
                out.println("HTTP/1.1 200 ok");
                out.println("Server: Molly");
                out.println("Content-Type: image/jpeg");
                out.println("Conten-Length: "+bytes.length);
                out.println("");
                socket.getOutputStream().write(bytes,0,bytes.length);
            }else {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));
                out = new PrintWriter(socket.getOutputStream());
                out.println("HTTP/1.1 200 ok");
                out.println("Server: Molly");
                out.println("Content-Type: text/html; charset=UTF-8");
                out.println("");
                while ((line = br.readLine()) != null){
                    out.println(line);
                }
                out.flush();
            }
        } catch (IOException e) {
            out.println("HTTP/1.0 500");
            out.println("");
            out.flush();
        }finally {
            close(br,in,reader,out,socket);
        }
    }

    private void close(Closeable... closeables) {
        if (closeables != null){
            for (Closeable c: closeables) {
                try {
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
