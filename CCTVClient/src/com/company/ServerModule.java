package com.company;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by SangwooSong on 5/9/18.
 */
public class ServerModule extends TCPModule{
    protected enum Status {CLIENT_REQUEST, CCTV_REQUEST, CCTV_RESPONSE, CCTV_FINISH, CLIENT_RESPONSE;

        @Override
        public String toString() {
            return "Status{}";
        }
    };
    protected final static String SERVER_IP = "124.197.181.80";
    protected final static int SERVER_PORT = 6969;

    public ServerModule() {
        super(SERVER_IP, SERVER_PORT);
    }

    /*public void sendJson(JsonObject object){
        send(object.toString());
    }*/

    public void sendJson(Object object){
        Gson gson = new Gson();
        System.out.print(gson.toJson(object));
        send(gson.toJson(object));
    }

    public String readJson(){
        String jsonString = read();
        Gson gson = new Gson();
        return jsonString;
    }

    public ServerProtocol readGson(){
        String jsonString = read();
        Gson gson = new Gson();
        return gson.fromJson(jsonString, ServerProtocol.class);
    }

    //  http://yookeun.github.io/java/2017/05/27/java-gson/
    public static class ServerProtocol {
        Status status = Status.CLIENT_REQUEST;
        String width = "400";
        String height = "400";
        String channel = "RGB";
        String person_id = "120";
        String person_face_score = "0.5";
        String person_gender = "F";
        String person_age = "18";
        String requested_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        String crop_width;// = "200";
        String crop_height;// = "200";
        String crop_channel;// = "gray";

        public void tosring(){
        }
    }
}
