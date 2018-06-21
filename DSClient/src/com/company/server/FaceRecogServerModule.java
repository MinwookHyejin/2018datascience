package com.company.server;

import com.company.etc.PrintModule;

import java.awt.image.BufferedImage;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by SangwooSong on 5/9/18.
 */
public class FaceRecogServerModule extends TCPServerModule {
    private OnServerEventListener onServerEventListener;
    private final int width;
    private final int height;
    LimitedSizeQueue<BufferedImage> imageCache;
    Socket client;


    public FaceRecogServerModule(int SERVER_PORT, int width, int height) {
        super(SERVER_PORT);
        this.width = width;
        this.height = height;
    }


    @Override
    public void onConnected(Socket client) {
        this.client = client;
        if(isConnected()) {
            if(imageCache != null){
                //3. 상황실 -> 서버 : CLIENT_REQUEST 보냄[JSON]( crop_width, crop_height 채워서 보냄)
                /*sendJson(ServerProtocol.CLEINT_REQUEST(width, height));
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                //4. 서버 -> 상황실 : CLIENT_RESPONSE 보냄[JSON](받았던 crop_width, crop_height 채워서 보냄)
                //ServerProtocol protocol = readGson();
                //if (protocol.status.equals(ServerProtocol.CLIENT_RESPONSE)) {
                    //5. 상황실 -> 서버 : 최근 프레임 30장 연속 전송[byte[]]
                    ArrayList<BufferedImage> cache = new ArrayList<>(imageCache);
                    PrintModule.print(cache.size()+"");
                    int count = 1;
                    for (BufferedImage image : cache) {
                        sendImage(resize(image, width, height));
                        PrintModule.print("이미지 전송 : "+count);
                        count++;
                        BufferedImage read = readImage(width, height);
                        if(read != null) {
                            PrintModule.print("이미지 수신 : " + count);
                            count--;
                        }
                        if (onServerEventListener != null) {
                            onServerEventListener.onImageRecived(read);
                        }
                    }
                    //생략함 : 6. 서버 -> 상황실 : 30장 다 받았다는 메시지인 SERVER_ACK 보냄[JSON]
                    PrintModule.print("결과 처리 대기중");
                    while (true) {
                        //7. 상황실은 처리 결과가 올 때 까지 연결 유지한채로 InputStream 계속 체크하면서 대기
                        if (isReadable()) {
                            //9. 서버 -> 상황실 :  프레임 전송[byte[]]
                            BufferedImage image = readImage(width, height);
                            PrintModule.print("결과 이미지 수신");
                            try {
                                Thread.sleep(500);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //8. 서버 -> 상황실 : 처리 결과 전송[JSON](person_id, person_face_score, crop_width, crop_height)
                            ServerProtocol result = readGson();
                            if (result.status.equals(ServerProtocol.CLIENT_RESULT)) {
                                //10. 상황실 : 받은 처리결과를 바탕으로 프레임을 디코딩(width, height정보 받은거) 해서 클라이언트에 뿌려주고, 점수가 일정치 이상이면 빨간색 warning frame 씌워줌
                                if (onServerEventListener != null) {
                                    onServerEventListener.onResultRecived(image, result);
                                }
                                break;
                            }
                        }
                    }
                    PrintModule.print("서버 종료");
                    //}


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
    public Socket socket() {
        return client;
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
        public void onResultRecived(BufferedImage image, ServerProtocol serverProtocol);
    }

    public void setOnServerEventListener(OnServerEventListener onServerEventListener) {
        this.onServerEventListener = onServerEventListener;
    }

    public void setImageCache(LimitedSizeQueue<BufferedImage> imageCache) {
        this.imageCache = imageCache;
    }


}
