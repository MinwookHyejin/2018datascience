package com.company;

/**
 * Created by SangwooSong on 5/9/18.
 */

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;


public class TCPModule {
    private final int MAX_MESSAGE_SIZE = 1024;
    private final Socket socket = new Socket();

    protected TCPModule(String SERVER_IP, int SERVER_PORT) {
        openConnection(SERVER_IP, SERVER_PORT);
    }

    @Override
    protected void finalize() throws Throwable {
        closeConnection();
        super.finalize();
    }

    protected final void send(String msg){
        try{
            send(msg.getBytes("UTF-8"));
            System.out.println("String sent : " + msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected final void send(byte[] bytes){
        try {
            OutputStream os = socket.getOutputStream();
            os.write(bytes);
            os.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected final void sendImage(BufferedImage bufferedImage, String formatName) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            ImageIO.write(bufferedImage, formatName, baos);
            baos.flush();
            byte[] bytes = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            out.flush();
            out.write(bytes);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected final void sendImage(BufferedImage bufferedImage){
        sendImage(bufferedImage, "JPG");
    }

    protected final String read(){
        String message = "error";
        try {
            final byte[] bytes = new byte[MAX_MESSAGE_SIZE];
            final InputStream is = socket.getInputStream();
            final int readByteCount = is.read(bytes);
            message = new String(bytes, 0, readByteCount, "UTF-8");
            System.out.println("[데이터 받기 성공]: " + message);
        }catch (Exception e){e.printStackTrace();}

        return message;

    }

    protected final void openConnection(String SERVER_IP, int SERVER_PORT) {
        if(!isConnected()) {
            try {
                System.out.println("[연결 요청]");
                socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
                System.out.println("[연결 성공]");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected final void closeConnection(){
        if(!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e1) {e1.printStackTrace();}
        }
    }

    protected final boolean isConnected(){return socket.isConnected();}
}