package com.company.server;

import com.company.server.CCTVModule;
import com.google.gson.Gson;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;


import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Created by SangwooSong on 5/9/18.
 */
public class CCTV extends CCTVModule {
    VideoCapture cap;
    Mat imageMat = new Mat();
    MyFrame frame;
    static{
       System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    public CCTV(int port) {
        super(port);
        cap = new VideoCapture(0);

        // Check if video capturing is enabled
        if (!cap.isOpened()) {
            System.exit(-1);
        }


        frame = new MyFrame();
        frame.setVisible(true);

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

    @Override
    public BufferedImage getCurrentImage() {
        cap.read(imageMat);
        if (!imageMat.empty()) {
            BufferedImage image1 = MatToBufferedImage(imageMat);
            frame.render(image1);
            return image1;
        } else {
            System.out.println("No captured frame -- camera disconnected");
            return null;
        }
    }

    public void run(){
        openConnection();
    }



    public class MyFrame {

        private final JFrame frame;
        private final MyPanel panel;

        public MyFrame() {
            // JFrame which holds JPanel
            frame = new JFrame();
            frame.getContentPane().setLayout(new FlowLayout());
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // JPanel which is used for drawing image
            panel = new MyPanel();
            frame.getContentPane().add(panel);
        }

        public void setVisible(boolean visible) {
            frame.setVisible(visible);
        }

        public void render(Mat image) {
            Image i = toBufferedImage(image);
            render(i);
        }
        public void render(Image image) {
            panel.setImage(image);
            panel.repaint();
            frame.pack();
        }

        public Image toBufferedImage(Mat m){
            // Code from http://stackoverflow.com/questions/15670933/opencv-java-load-image-to-gui

            // Check if image is grayscale or color
            int type = BufferedImage.TYPE_BYTE_GRAY;
            if ( m.channels() > 1 ) {
                type = BufferedImage.TYPE_3BYTE_BGR;
            }

            // Transfer bytes from Mat to BufferedImage
            int bufferSize = m.channels()*m.cols()*m.rows();
            byte [] b = new byte[bufferSize];
            m.get(0,0,b); // get all the pixels
            BufferedImage image = new BufferedImage(m.cols(), m.rows(), type);
            final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            System.arraycopy(b, 0, targetPixels, 0, b.length);
            return image;
        }
    }

    public class MyPanel extends JPanel {

        private Image img;

        public MyPanel() {

        }

        public void setImage(Image img) {
            // Image which we will render later
            this.img = img;

            // Set JPanel size to image size
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setLayout(null);
        }

        @Override
        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }



}
