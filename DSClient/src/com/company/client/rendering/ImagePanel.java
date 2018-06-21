package com.company.client.rendering;

import com.company.etc.PrintModule;
import com.company.server.TCPModule;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.File;

/**
 * Created by SangwooSong on 5/10/18.
 */
public class ImagePanel extends JPanel {
    Thread t;
    private BufferedImage img;
    private BufferedImage imgForDrawing;
    private int WIDTH_MAX = 400;
    private int HEIGHT_MAX = 400;
    private boolean isWarning = false;
    public ImagePanel() {
        Dimension size = new Dimension(WIDTH_MAX, HEIGHT_MAX);
        setPreferredSize(size);
        setMinimumSize(size);
        setMaximumSize(size);
        setSize(size);
        setLayout(null);
    }


    public void setImagefromLocal(String file){
        File img = new File(file);
        try {
            BufferedImage in = ImageIO.read(img);
            setImage(in);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void setImage(BufferedImage img) {
        this.img = TCPModule.resize(img, WIDTH_MAX, HEIGHT_MAX);
        if(isWarning) {
            this.imgForDrawing = redMask(this.img);
        }else{
            this.imgForDrawing = this.img;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(imgForDrawing, 0, 0, null);
    }

    public void enableWarningFrame(){
        if(!isWarningFrameEnabled()) {

            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        try {
                            isWarning = true;
                            setImage(img);
                            Thread.sleep(500);
                            isWarning = false;
                            setImage(img);
                            Thread.sleep(500);
                        }catch (InterruptedException e){
                            isWarning = false;
                            break;
                        }
                    }


                }


            });
            t.start();
        }
    }

    public void disableWarningFrame(){
        if(isWarningFrameEnabled()) {
            isWarning = false;
            t.interrupt();
            t = null;
        }

    }
    public boolean isWarningFrameEnabled(){
        return t != null && t.getState() != Thread.State.TERMINATED;
    }

    public BufferedImage redMask(final BufferedImage img){
        final int w = img.getWidth();
        final int h = img.getHeight();
        final BufferedImage warningFrame = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
        int color;
        for(int x=0; x<w; x++){
            for(int y=0; y<h; y++){
                color = img.getRGB(x, y) & 0xff0000;
                warningFrame.setRGB(x, y, color);
            }
        }
        return warningFrame;
    }
}