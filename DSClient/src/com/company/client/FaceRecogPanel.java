package com.company.client;

import com.company.TestCCTV;
import com.company.client.rendering.ImagePanel;
import com.company.etc.PrintModule;
import com.company.server.FaceRecogServerModule;
import com.company.server.LimitedSizeQueue;
import com.company.server.TCPModule;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;

/**
 * Created by SangwooSong on 5/10/18.
 */
public class FaceRecogPanel extends JPanel {
    TestCCTV testCCTV;
    FaceRecogServerModule faceRecogModule;
    ImagePanel imagePanel;
    JTextField ipText;
    JTextField portText;
    JTextField widthText;
    JTextField heightText;
    JLabel personIdText;
    JLabel personAgeText;
    JLabel personGenderText;
    JLabel personFaceScoreText;
    LimitedSizeQueue<BufferedImage> imageCache;

    BufferedImage currentImage;
    public JButton button_openServer;
    public JButton button_closeServer;
    public Thread t;

    public FaceRecogPanel() {
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
                            faceRecogModule = new FaceRecogServerModule(getPort(), getImageWidth(), getImageHeight());
                            faceRecogModule.setOnServerEventListener(new FaceRecogServerModule.OnServerEventListener() {
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
                                public void onResultRecived(BufferedImage image, TCPModule.ServerProtocol serverProtocol) {
                                    render(image);
                                    if(serverProtocol.isCriminal()){
                                        imagePanel.enableWarningFrame();
                                    }else{
                                        imagePanel.disableWarningFrame();
                                    }
                                    personIdText.setText(serverProtocol.person_id);
                                    personAgeText.setText(serverProtocol.person_age);
                                    personGenderText.setText(serverProtocol.person_gender);
                                    personFaceScoreText.setText(serverProtocol.person_face_score);
                                }

                                @Override
                                public void onConnectionClosed() {
                                    t = null;
                                    button_openServer.setText("서버 연결 끊김");
                                    PrintModule.print("서버 컨넥션 닫힘");
                                    faceRecogModule.openConnection();
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
                if(testCCTV == null){
                    testCCTV = new TestCCTV(getImageWidth(), getImageHeight()) {
                        @Override
                        public void onImageLoaded(BufferedImage bufferedImage) {
                            render(bufferedImage);
                        }
                    };
                }
                if(imagePanel.isWarningFrameEnabled()){
                    imagePanel.disableWarningFrame();
                }else{
                    imagePanel.enableWarningFrame();
                }
                if( !testCCTV.isRunning() ) {
                    testCCTV.start();
                    button_closeServer.setText("웹캠 끄기");
                }else{
                    testCCTV.end();
                    button_closeServer.setText("웹캠 켜기");
                }
            }
        });
    }

    public void settingTextField(){
        //ipText = generteTextField("ip addr", "124.197.181.80");
        portText = generteTextField("port", "15000");
        widthText = generteTextField("width", "400");
        heightText = generteTextField("height", "400");
        personIdText = generteLabel("Person ID : ", "0");
        personFaceScoreText = generteLabel("Face Score : ", "0");
        personAgeText = generteLabel("Age : ", "0");
        personGenderText = generteLabel("Gender : ", "0");
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
    public JLabel generteLabel(String labelString, String defaultString){
        JPanel textBox = new JPanel();
        textBox.setLayout(new GridLayout(1,2));
        JLabel label = new JLabel(labelString);
        label.setHorizontalAlignment(JLabel.CENTER);
        JLabel label2 = new JLabel(defaultString);
        label2.setHorizontalAlignment(JLabel.CENTER);
        textBox.add(label);
        textBox.add(label2);
        add(textBox);
        return label2;
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
