package com.company.server;

/**
 * Created by SangwooSong on 5/9/18.
 */

import com.company.etc.PrintModule;
import com.google.gson.Gson;
import com.sun.javafx.iio.ImageStorage;
import sun.misc.IOUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public abstract class TCPModule {
    private final int MAX_MESSAGE_SIZE = 1024;


    protected final void send(String msg){
        try{
            send(msg.getBytes("UTF-8"));
            PrintModule.print("[문자열 전송] : " + msg);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    protected final void send(byte[] bytes){
        try {
            OutputStream os = socket().getOutputStream();
            os.write(bytes);
            os.flush();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public final void sendImage(BufferedImage bufferedImage) {
        try {
            DataOutputStream out = new DataOutputStream(socket().getOutputStream());
            byte[] bytes = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            out.flush();
            out.write(bytes);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    protected final BufferedImage readImage(int width, int height){
        if(isReadable()) {
            try {
            /*final byte[] frame = new byte[width * height * 3];
            BufferedInputStream bufferedInputStream = new BufferedInputStream(socket().getInputStream());
            bufferedInputStream.read(frame);*/

                byte[] frame = IOUtils.readFully(socket().getInputStream(), width * height * 3, false);
                BufferedImage currentImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
                byte[] imgData = ((DataBufferByte) currentImage.getRaster().getDataBuffer()).getData();
                System.arraycopy(frame, 0, imgData, 0, frame.length);

                //img.setData(Raster.createRaster(img.getSampleModel(), new DataBufferByte(bytes, bytes.length), new Point() ) );
                return currentImage;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    protected final String read(){
        String message = "error";
        try {
            final byte[] bytes = new byte[MAX_MESSAGE_SIZE];
            final InputStream is = socket().getInputStream();
            final int readByteCount = is.read(bytes);
            message = new String(bytes, 0, readByteCount, "UTF-8");
            PrintModule.print("[문자열 수신]: " + message);
        }catch (Exception e){e.printStackTrace();}

        return message;
    }

    public final void sendJson(Object object){
        Gson gson = new Gson();
        String msg = gson.toJson(object);
        PrintModule.print("[Json 전송 준비] : "+msg);
        send(gson.toJson(object));
    }

    public final String readJson(){
        String jsonString = read();
        Gson gson = new Gson();
        return jsonString;
    }

    public final ServerProtocol readGson(){
        String jsonString = read();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, ServerProtocol.class);
    }

    public boolean isReadable(){
        try {
            return socket().getInputStream().available() > 0;
        }catch (Exception e){
            return false;
        }
    }



    public static BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        if( w == newW && h == newH ) return img;
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }


    public abstract Socket socket();

    public static class ServerProtocol {
        public final static String CLIENT_REQUEST = "0";
        public final static String CCTV_REQUEST = "1";
        public final static String CCTV_RESPONSE = "2";
        public final static String CCTV_FINISH = "3";
        public final static String CLIENT_RESPONSE = "4";
        public final static String CLIENT_RESULT = "5";

        public final static String IS_CRIMINAL_YES = "yes";

        public String status = CLIENT_REQUEST;
        public String width;
        public String height;
        public String channel;
        public String person_id;
        public String person_face_score;
        public String person_gender;
        public String person_age;
        public String is_criminal;
        public String requested_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        public String crop_width;// = "200";
        public String crop_height;// = "200";
        public String crop_channel;// = "gray";
        public String frameDelay;

        private ServerProtocol(){

        }

        public static ServerProtocol CLEINT_REQUEST(int width, int height){
            ServerProtocol serverProtocol = new ServerProtocol();
            serverProtocol.status = CLIENT_REQUEST;
            serverProtocol.crop_width = width+"";
            serverProtocol.crop_height = height+"";
            serverProtocol.crop_channel = "RGB";
            return serverProtocol;
        }
        public static ServerProtocol CCTV_FINISH(){
            ServerProtocol serverProtocol = new ServerProtocol();
            serverProtocol.status = CCTV_FINISH;
            return serverProtocol;
        }
        public static ServerProtocol CCTV_REQUEST(int width, int height){
            ServerProtocol serverProtocol = new ServerProtocol();
            serverProtocol.status = CCTV_REQUEST;
            serverProtocol.width = width+"";
            serverProtocol.height = height+"";
            serverProtocol.channel = "RGB";
            serverProtocol.frameDelay = "0";
            return serverProtocol;
        }

        public static ServerProtocol CCTV_RESPONSE(int width, int height){
            ServerProtocol serverProtocol = new ServerProtocol();
            serverProtocol.status = CCTV_RESPONSE;
            serverProtocol.width = width+"";
            serverProtocol.height = height+"";
            serverProtocol.channel = "RGB";
            return serverProtocol;
        }
        public boolean isCriminal(){
            return Float.valueOf(person_face_score) >= 50;
            //return is_criminal != null && is_criminal.equals(IS_CRIMINAL_YES);
        }
    }
}