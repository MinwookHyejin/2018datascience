package com.company.client;

import com.company.client.rendering.ImagePanel;
import com.company.etc.PrintModule;
import com.company.server.CCTVClientModule;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;

/**
 * Created by SangwooSong on 5/10/18.
 */
public class CCTVPanel extends JPanel {
    CCTVClientModule cctvModule;
    ImagePanel imagePanel;
    JTextField ipText;
    JTextField portText;
    JTextField widthText;
    JTextField heightText;

    BufferedImage currentImage;
    public JButton button_openServer;
    public JButton button_closeServer;
    public Thread t;


    final FaceRecogPanel faceRecogPanel;
    public CCTVPanel(FaceRecogPanel faceRecogPanel) {
        // JFrame which holds JPanel
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        settingButton();
        settingTextField();
        settingImagePanel();
        setVisible(true);
        this.faceRecogPanel = faceRecogPanel;
    }

    private void settingImagePanel(){
        imagePanel = new ImagePanel();
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new GridLayout(1,1));
        jPanel.add(imagePanel);
        imagePanel.setImagefromLocal("icon_cctv.png");
        add(jPanel);
    }


    private void settingButton(){
        button_openServer = new JButton("CCTV 연결");
        button_closeServer = new JButton("CCTV 연결 종료");

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
                            cctvModule = new CCTVClientModule(getIp(), getPort(), getImageWidth(), getImageHeight());
                            cctvModule.setOnCCTVEventListener(new CCTVClientModule.OnCCTVEventListener() {
                                @Override
                                public void onConnectionOpened() {
                                    PrintModule.print("cctv 서버 컨넥션 열림");
                                    button_openServer.setText("cctv 연결됨");
                                    faceRecogPanel.setImageCache(cctvModule.getImageCache());
                                }

                                @Override
                                public void onImageRecived(final BufferedImage image) {
                                    render(image);
                                }
                                @Override
                                public void onConnectionClosed() {
                                    t = null;
                                    PrintModule.print("cctv 서버 컨넥션 닫힘");
                                    button_openServer.setText("cctv 연결 해제");
                                }
                                @Override
                                public void onConnectionTimedOut() {
                                    button_openServer.setText("cctv 서버 연결하기(타임 아웃)");
                                }
                            });
                            cctvModule.openConnection();
                        }
                    });
                    t.start();
                    PrintModule.print("cctv서버 실행");
                    button_openServer.setText("cctv 연결 시도 중");
                }else{
                    PrintModule.print("cctv서버가 이미 실행중입니다");
                }
            }
        });

        button_closeServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if( isServerRunning() ){//t != null && t.getState() != Thread.State.TERMINATED
                    if(cctvModule != null){
                        cctvModule.requestFinish();
                        PrintModule.print("cctv종료 요청");
                    }else{
                        t.interrupt();
                        PrintModule.print("cctv 스레드 강제 종료");
                    }
                }
            }
        });
    }

    public void settingTextField(){
        ipText = generteTextField("ip addr", "127.0.0.1");//"124.197.181.80");
        portText = generteTextField("port", "2000");//"8000");
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
}
