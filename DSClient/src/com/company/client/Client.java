package com.company.client;

import javax.swing.*;
import java.awt.*;

/**
 * Created by SangwooSong on 5/10/18.
 */
public class Client extends JFrame {

    public static void openClient(){
        new Client();
    }

    private Client() {
        // JFrame which holds JPanel
        FaceRecogPanel faceRecogPanel = new FaceRecogPanel();
        CCTVPanel cctvPanel = new CCTVPanel(faceRecogPanel);
        final int borderWidth = 10;
        final int width = faceRecogPanel.getWidth()+cctvPanel.getWidth();
        final int height = Math.max(faceRecogPanel.getHeight(), cctvPanel.getHeight()) + borderWidth * 2;

        JPanel container = new JPanel();
        container.setLayout(new GridLayout(1, 2));
        add(container);

        container.add(faceRecogPanel);
        container.add(cctvPanel);
        faceRecogPanel.setBorder(BorderFactory.createMatteBorder(borderWidth,
                borderWidth,borderWidth,borderWidth,
                Color.BLACK));
        cctvPanel.setBorder(BorderFactory.createMatteBorder(borderWidth,
                0,borderWidth,borderWidth,
                Color.BLACK));

        /*cctvPanel.setBorder(BorderFactory.createMatteBorder(0,
                borderWidth,0,0,
                Color.BLACK));*/
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800+20);
        //setSize(width, height);

        setVisible(true);
        validate();
    }
}
