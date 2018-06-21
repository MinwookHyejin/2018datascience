package com.company.client;

import com.company.TestCCTV;
import com.company.client.rendering.ImagePanel;
import com.company.etc.PrintModule;
import com.company.server.FaceRecogModule;
import com.company.server.LimitedSizeQueue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * Created by SangwooSong on 5/10/18.
 */
public class FaceRecogPanel2 extends JPanel {
    TestCCTV testCCTV;
    FaceRecogModule faceRecogModule;
    ImagePanel imagePanel;
    JTextField ipText;
    JTextField portText;
    JTextField widthText;
    JTextField heightText;
    LimitedSizeQueue<BufferedImage> imageCache;

    BufferedImage currentImage;
    public JButton button_openServer;
    public JButton button_closeServer;
    public Thread t;

    public FaceRecogPanel2() {
        // JFrame which holds JPanel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        settingButton();
        settingTextField();
        settingImagePanel();
        setVisible(true);
    }

    private void settingImagePanel(){
        imagePanel = new ImagePanel();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1,1));
        jPanel.add(imagePanel);
        imagePanel.setImagefromLocal("icon_profile.png");
        add(jPanel);
    }


    private void settingButton(){
        button_openServer = new JButton("Server 연결 요청");
        button_closeServer = new JButton("no-operation");

        JPanel buttonBox = new JPanel();
        buttonBox.setLayout(new GridLayout(1,2));
        buttonBox.add(button_openServer);
        buttonBox.add(button_closeServer);
        add(buttonBox);

        button_openServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if( !isServerRunning() ){ //t == null || t.getState() == Thread.State.TERMINATED
                    t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            faceRecogModule = new FaceRecogModule(getIp(), getPort(), getImageWidth(), getImageHeight());
                            faceRecogModule.setOnServerEventListener(new FaceRecogModule.OnServerEventListener() {
                                @Override
                                public void onConnectionOpened() {
                                    button_openServer.setText("서버 연결됨");
                                    PrintModule.print("서버 컨넥션 열림");
                                }

                                @Override
                                public void onImageRecived(final BufferedImage image) {
                                    render(image);
                                }
                                @Override
                                public void onConnectionClosed() {
                                    t = null;
                                    button_openServer.setText("서버 연결 끊김");
                                    PrintModule.print("서버 컨넥션 닫힘");
                                }

                                @Override
                                public void onConnectionTimedOut() {
                                    button_openServer.setText("서버 연결하기(타임 아웃)");
                                }
                            });
                            faceRecogModule.setImageCache(imageCache);
                            faceRecogModule.openConnection();
                        }
                    });
                    t.start();
                    PrintModule.print("서버 연결 시도 중");
                    button_openServer.setText("서버 연결 시도 중");
                }else{
                    faceRecogModule.closeConnection();
                    t.interrupt();
                    t = null;
                    button_openServer.setText("서버 연결하기");
                    PrintModule.print("서버 연결이 이미 진행중 입니다.");
                }
            }
        });

        button_closeServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(testCCTV == null) {
                    testCCTV = new TestCCTV(getImageWidth(), getImageHeight()) {
                        @Override
                        public void onImageLoaded(BufferedImage bufferedImage) {
                            render(bufferedImage);
                        }
                    };
                    testCCTV.start();
                    button_closeServer.setText("웹캠 끄기");
                }else{
                    testCCTV.end();
                    testCCTV = null;
                    button_closeServer.setText("웹캠 켜기");
                }
            }
        });
    }

    public void settingTextField(){
        ipText = generteTextField("ip addr", "124.197.181.80");
        portText = generteTextField("port", "6969");
        widthText = generteTextField("width", "400");
        heightText = generteTextField("height", "400");
    }


    public JTextField generteTextField(String labelString, String defaultValue){
        JPanel textBox = new JPanel();
        textBox.setLayout(new GridLayout(1,2));
        JLabel label = new JLabel(labelString);
        label.setHorizontalAlignment(JLabel.CENTER);

        JTextField field = new JTextField(defaultValue);
        textBox.add(label);
        textBox.add(field);
        add(textBox);
        return field;
    }


    public int getPort(){return Integer.parseInt(portText.getText());}
    public int getImageWidth(){return Integer.parseInt(widthText.getText());}
    public int getImageHeight(){return Integer.parseInt(heightText.getText());}
    public String getIp(){return ipText.getText();}


    private boolean isServerRunning(){
        return !(t == null || t.getState() == Thread.State.TERMINATED);
    }

    public void render(BufferedImage image) {
        if(currentImage != image && image != null) {
            currentImage = image;
            imagePanel.setImage(currentImage);
            imagePanel.repaint();
        }

    }

    public void setImageCache(LimitedSizeQueue<BufferedImage> imageCache) {
        this.imageCache = imageCache;
        if(faceRecogModule != null) faceRecogModule.setImageCache(imageCache);
    }
}
