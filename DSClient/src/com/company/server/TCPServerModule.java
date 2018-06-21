package com.company.server;

import com.company.etc.PrintModule;
import com.google.gson.Gson;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by SangwooSong on 5/9/18.
 */
public abstract class TCPServerModule extends TCPModule{
    ServerSocket server = null;
    protected final int SERVER_PORT;

    public TCPServerModule(int port) {
        SERVER_PORT = port;
    }

    @Override
    protected void finalize() throws Throwable {
        closeConnection();
        super.finalize();
    }

    protected void closeConnection(){
        if(isConnected()){
            try {
                socket().close();
            } catch (IOException e1) {e1.printStackTrace();}
        }
        if(server.isBound()) {
            try {
                server.close();
            } catch (IOException e1) {e1.printStackTrace();}
        }
    }

    public final boolean isConnected(){return socket() != null && socket().isConnected();}

    public final void openConnection() {
        try
        {
            server = new ServerSocket(SERVER_PORT); //port 번호: 7000으로 ServerSocket 생성

            System.out.println("*****  Server Program이 Clinet 접속을 기다립니다. *****");

            //*** Clinet 접속이 있을 때까지 대기: 접속하는 순간 Socket을 반환 ***//

            Socket client = server.accept();

            //*** 접속이 되면 Clinet로부터 IP 정보를 얻어 출력 ***//
            System.out.println(client.getInetAddress()+"로부터 연결요청");
            onConnected(client);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }






    public abstract void onConnected(Socket client);



}
