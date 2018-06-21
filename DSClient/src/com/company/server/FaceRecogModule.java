package com.company.server;

import com.company.etc.PrintModule;
import com.google.gson.Gson;

import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by SangwooSong on 5/9/18.
 */
public class FaceRecogModule extends TCPClientModule {
    private OnServerEventListener onServerEventListener;
    private final int width;
    private final int height;
    LimitedSizeQueue<BufferedImage> imageCache;


    public FaceRecogModule(String SERVER_IP, int SERVER_PORT, int width, int height) {
        super(SERVER_IP, SERVER_PORT);
        this.width = width;
        this.height = height;
    }

    @Override
    public void openConnection() {
        super.openConnection();
        if(isConnected()) {
            System.out.print("이미지 캐시 눌?");
            if(imageCache != null){
                ArrayList<BufferedImage> cache = new ArrayList<>(imageCache);
                System.out.print("이미지 캐시 존재함 "+cache.size());
                for(BufferedImage image : cache){
                    sendImage(resize(image, width, height));
                    BufferedImage image2 = readImage(width, height);
                    if (onServerEventListener != null) {
                        onServerEventListener.onImageRecived(image2);
                    }
                }
            }
            closeConnection();
            if (onServerEventListener != null) onServerEventListener.onConnectionClosed();
        /*
        if(isConnected()) {
            if (onServerEventListener != null) onServerEventListener.onConnectionOpened();
            sendJson(ServerProtocol.CLEINT_REQUEST(width, height));
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ServerProtocol protocol = readGson();
            if (protocol.status.equals(ServerProtocol.CLIENT_RESPONSE)) {
                if(imageCache != null){
                    for(BufferedImage image : imageCache){
                        sendImage(resize(image, width, height));
                    }
                }

                while (true) {
                    BufferedImage image = readImage(width, height);
                    if (onServerEventListener != null) {
                        onServerEventListener.onImageRecived(image);
                    }
                }
            }
            closeConnection();
            if (onServerEventListener != null) onServerEventListener.onConnectionClosed();*/
        }else{
            if (onServerEventListener != null) onServerEventListener.onConnectionTimedOut();
        }

    }

    @Override
    public void closeConnection() {
        boolean wasConnected = isConnected();
        super.closeConnection();
        if(onServerEventListener != null && wasConnected) onServerEventListener.onConnectionClosed();
    }

    public interface OnServerEventListener{
        public void onConnectionOpened();
        public void onImageRecived(BufferedImage image);
        public void onConnectionClosed();
        public void onConnectionTimedOut();
    }

    public void setOnServerEventListener(OnServerEventListener onServerEventListener) {
        this.onServerEventListener = onServerEventListener;
    }

    public void setImageCache(LimitedSizeQueue<BufferedImage> imageCache) {
        this.imageCache = imageCache;
    }


}
