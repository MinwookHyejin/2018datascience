package com.company;

import com.company.etc.PrintModule;
import com.company.server.TCPModule;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

/**
 * Created by SangwooSong on 5/14/18.
 */
public abstract class TestCCTV {
    Thread t;
    int width;
    int height;
    VideoCapture cap;
    static{
        nu.pattern.OpenCV.loadShared();
    }
    public TestCCTV(int width, int height) {
        this.width = width;
        this.height = height;
        cap = new VideoCapture(0);
    }

    public BufferedImage MatToBufferedImage(Mat frame) {
        //Mat() to BufferedImage
        int type = 0;
        if (frame.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (frame.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
        WritableRaster raster = image.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        frame.get(0, 0, data);

        return image;
    }

    public BufferedImage scaleImage(BufferedImage img, int width, int height,
                                    Color background) {
        int imgWidth = img.getWidth();
        int imgHeight = img.getHeight();
        if (imgWidth*height < imgHeight*width) {
            width = imgWidth*height/imgHeight;
        } else {
            height = imgHeight*width/imgWidth;
        }
        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setBackground(background);
            g.clearRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }

    public void start(){
        if( !isRunning() ) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {


                    // Check if video capturing is enabled
                    if (!cap.isOpened()) {
                        System.exit(-1);
                    }


                    // Matrix for storing image
                    Mat image = new Mat();
// Frame for displaying image

                    // Main loop
                    while (true) {
                        // Read current camera frame into matrix
                        cap.read(image);
                        // Render frame if the camera is still acquiring images
                        if (!image.empty()) {
                            BufferedImage image1 = TCPModule.resize(MatToBufferedImage(image), width, height);
                            PrintModule.print(image1.getWidth()+", "+image1.getHeight());
                            onImageLoaded( image1);
                            //serverModule.sendImage(image1);
                        } else {
                            System.out.println("No captured frame -- camera disconnected");
                        }
                    }
                }
            };
            t = new Thread(runnable);
            t.start();
        }
    }
    public void end(){
        if(isRunning()){
            t.interrupt();
            t = null;
        }
    }

    public boolean isRunning(){
        return ! (t == null || t.getState() == Thread.State.TERMINATED);
    }

    public abstract void onImageLoaded(BufferedImage bufferedImage);
}
