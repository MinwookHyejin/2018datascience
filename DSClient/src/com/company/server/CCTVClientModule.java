package com.company.server;

import com.company.etc.PrintModule;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by SangwooSong on 5/9/18.
 */
public class CCTVClientModule extends TCPClientModule {
    private OnCCTVEventListener onCCTVEventListener;
    private final int width;
    private final int height;
    private boolean requestFinish = false;
    LimitedSizeQueue<BufferedImage> imageCache = new LimitedSizeQueue<>(9);

    public CCTVClientModule(String SERVER_IP, int SERVER_PORT, int width, int height) {
        super(SERVER_IP, SERVER_PORT);
        this.width = width;
        this.height = height;
    }

    @Override
    public void openConnection() {
        super.openConnection();
        if(isConnected()) {
            if (onCCTVEventListener != null) onCCTVEventListener.onConnectionOpened();
            ServerProtocol request = ServerProtocol.CCTV_REQUEST(width, height);
            sendJson(request);
            int frameDelay = Integer.parseInt(request.frameDelay);
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ServerProtocol protocol = readGson();
            if (protocol.status.equals(ServerProtocol.CCTV_RESPONSE)) {
                //int count = 0;
                //long time = System.currentTimeMillis();
                while (true) {

                    /*if(System.currentTimeMillis() - time > 1000){
                        PrintModule.print2(count+", "+String.valueOf(System.currentTimeMillis() - time));
                        count = 0;
                        time = System.currentTimeMillis();
                    }*/

                    BufferedImage image = readImage(width, height);
                    imageCache.add(image);
                    //if(image != null) count++;
                    if (onCCTVEventListener != null) {
                        onCCTVEventListener.onImageRecived(image);
                    }
                    if (requestFinish) {
                        sendJson(ServerProtocol.CCTV_FINISH());
                        break;
                    }
                    if(frameDelay > 0) {
                        try {
                            Thread.sleep(frameDelay);
                        } catch (Exception e) {
                        }
                    }
                }
            }
            closeConnection();
            if (onCCTVEventListener != null) onCCTVEventListener.onConnectionClosed();
        }else{
            if( onCCTVEventListener != null) onCCTVEventListener.onConnectionTimedOut();
        }
    }

    public void requestFinish(){
        if(isConnected()){
            this.requestFinish = true;
        }else{
            closeConnection();
        }
    }

    @Override
    public void closeConnection() {
        boolean wasConnected = isConnected();
        super.closeConnection();
        if(onCCTVEventListener != null && wasConnected) onCCTVEventListener.onConnectionClosed();
    }

    public interface OnCCTVEventListener{
        public void onConnectionOpened();
        public void onImageRecived(BufferedImage image);
        public void onConnectionTimedOut();
        public void onConnectionClosed();
    }

    public void setOnCCTVEventListener(OnCCTVEventListener onCCTVEventListener) {
        this.onCCTVEventListener = onCCTVEventListener;
    }

    public LimitedSizeQueue<BufferedImage> getImageCache() {
        return imageCache;
    }
}
