package com.company;

import com.company.client.Client;
import com.company.server.FaceRecogModule;
import com.company.server.TCPModule;
import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class Main {

    public static void main(String[] args) {
	// write your code here
        //sendJsonTest();
        //sendImageTest();
        Client.openClient();

    }

    public static void sendImageTest(){
        File img = new File("./testImage.jpg");
        try {
            BufferedImage in = ImageIO.read(img);
            FaceRecogModule module = new FaceRecogModule("124.197.181.80", 6969, 400, 400);
            module.sendImage(in);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
    public static void sendJsonTest(){

        FaceRecogModule.ServerProtocol serverProtocol = TCPModule.ServerProtocol.CCTV_REQUEST(400, 400);
        Gson gson = new Gson();
        System.out.print(gson.toJson(serverProtocol));

        FaceRecogModule module = new FaceRecogModule("124.197.181.80", 6969, 400, 400);
        module.sendJson(serverProtocol);
    }
}
