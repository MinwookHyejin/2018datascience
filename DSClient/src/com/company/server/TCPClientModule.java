package com.company.server;

/**
 * Created by SangwooSong on 5/9/18.
 */

import com.company.etc.PrintModule;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;

import java.net.InetSocketAddress;

import java.net.Socket;



public class TCPClientModule extends TCPModule {
    private final Socket socket = new Socket();

    protected final String SERVER_IP;
    protected final int SERVER_PORT;


    protected TCPClientModule(String SERVER_IP, int SERVER_PORT) {
        this.SERVER_IP = SERVER_IP;
        this.SERVER_PORT = SERVER_PORT;
    }

    @Override
    protected void finalize() throws Throwable {
        closeConnection();
        super.finalize();
    }

    @Override
    public Socket socket() {
        return socket;
    }

    public void openConnection() {
        if(!isConnected()) {
            try {
                PrintModule.print("[연결 요청]");
                socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
                PrintModule.print("[연결 성공]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection(){
        if(!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e1) {e1.printStackTrace();}
        }
    }

    public final boolean isConnected(){return socket.isConnected();}
}