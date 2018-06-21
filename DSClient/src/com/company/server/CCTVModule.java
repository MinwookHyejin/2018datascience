package com.company.server;

import com.company.etc.PrintModule;
import com.google.gson.Gson;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by SangwooSong on 5/9/18.
 */
public abstract class CCTVModule extends TCPServerModule{
    Socket client;
    OnCCTVEventListener onCCTVEventListener;


    final int port;

    public CCTVModule(int port) {
        super(port);
        this.port = port;
    }


    @Override
    public Socket socket() {
        return client;
    }


    public void onConnected(Socket client){
        this.client = client;
        if(onCCTVEventListener != null) onCCTVEventListener.onConnectionOpened();
        ServerProtocol recieve = readGson();
        if(recieve.status.equals(ServerProtocol.CCTV_REQUEST)){
            final int width = Integer.parseInt(recieve.width);
            final int height = Integer.parseInt(recieve.height);
            final int frameDelay = Integer.parseInt(recieve.frameDelay);
            ServerProtocol response = ServerProtocol.CCTV_RESPONSE(width, height);
            sendJson(response);
            try {
                Thread.sleep(1000);
            }catch (Exception e){}
            int count = 0;
            long time = System.currentTimeMillis();
            while (true) {
                // Read current camera frame into matrix
                BufferedImage image = getCurrentImage();
                // Render frame if the camera is still acquiring images
                if (image != null) {
                    //frame.render(image);
                    //System.out.println(image1.getType());
                    image = resize(image, width, height);
                    sendImage(image);
                    if(System.currentTimeMillis() - time > 1000){
                        PrintModule.print2(count+", "+String.valueOf(System.currentTimeMillis() - time));
                        count = 0;
                        time = System.currentTimeMillis();
                    }
                    if(onCCTVEventListener != null) onCCTVEventListener.onImageSent(image);
                    try {
                        Thread.sleep(frameDelay);
                    }catch (Exception e){}
                    if(isReadable()) {
                        recieve = readGson();
                        if (recieve.status.equals(ServerProtocol.CCTV_FINISH)) {
                            return;
                        }
                    }

                } else {
                    System.out.println("No captured frame -- camera disconnected");
                }
            }
        }
        closeConnection();

    }


    @Override
    protected void closeConnection() {
        boolean wasConnected = isConnected();
        super.closeConnection();
        if(onCCTVEventListener != null && wasConnected) onCCTVEventListener.onConnectionClosed();
    }

    public interface OnCCTVEventListener{
        public void onConnectionOpened();
        public void onImageSent(BufferedImage image);
        public void onConnectionClosed();
    }


    public abstract BufferedImage getCurrentImage();

    public void setOnCCTVEventListener(OnCCTVEventListener onCCTVEventListener) {
        this.onCCTVEventListener = onCCTVEventListener;
    }
}
