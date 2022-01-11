package com.company;

import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import javax.swing.*;

public class PlayerFrame extends JFrame {

    private int width, height;
    private Container contentPane;
    private PlayerSprite me;
    private PlayerSprite enemy;
    private DrawingComponent dc;
    private Timer animationTimer;
    private boolean up, down, left, right;
    private Socket socket;
    private int playerID;
    private ReadFromServer rfsRunnable;
    private WriteToServer wtsRunnable;

    public PlayerFrame(int w, int h) {
        width = w;
        height = h;
        up = false;
        down = false;
        left = false;
        right = false;
    }

    public void setUpGUI() {
        contentPane = this.getContentPane();
        this.setTitle("Player #" + playerID);
        contentPane.setPreferredSize(new Dimension(width, height));
        createSprites();

        dc = new DrawingComponent();
        contentPane.add(dc);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);


        addKeyListener(setUpKeyListener());
        setUpAnimationTimer();

        setFocusable(true);
    }

    private void createSprites(){
        System.out.println(playerID);
        if(playerID == 1) {
            me = new PlayerSprite(100,400,50,Color.BLUE);
            enemy = new PlayerSprite(490,400,50,Color.RED);
        } else if(playerID == 2) {
            enemy = new PlayerSprite(100,400,50,Color.BLUE);
            me = new PlayerSprite(490,400,50,Color.RED);
        }
    }

    private void setUpAnimationTimer() {
        int interval = 10;
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double speed = 5;
                if(up){
                    me.moveV(-speed);
                    dc.repaint();
                }
                if(left){
                    me.moveH(-speed);
                    dc.repaint();
                }
                if(down){
                    me.moveV(speed);
                    dc.repaint();
                }
                if(right){
                    me.moveH(speed);
                    dc.repaint();
                }
            }
        };
        animationTimer = new Timer(interval, al);
        animationTimer.start();
    }

    private KeyListener setUpKeyListener() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                switch (keyCode) {
                    case 87 -> //W
                            up = true;
                    case 65 -> //A
                            left = true;
                    case 83 -> //S
                            down = true;
                    case 68 -> //D
                            right = true;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();

                switch (keyCode) {
                    case 87 -> //W
                            up = false;
                    case 65 -> //A
                            left = false;
                    case 83 -> //S
                            down = false;
                    case 68 -> //D
                            right = false;
                }
            }
        };
    }

    private void connectToServer(){
        try {
            socket = new Socket("localhost", 5555);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            playerID = in.readInt();
            System.out.println("You are player #" + playerID);
            if(playerID == 1) {
                System.out.println("Wait for Player #2 to connect...");
            }
            rfsRunnable = new ReadFromServer(in);
            wtsRunnable = new WriteToServer(out);
        } catch (IOException ex) {
            System.out.println("IOException from connectToServer().");
        }
    }

    public class ReadFromServer implements Runnable{
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream dataIn){
            this.dataIn = dataIn;
            System.out.println("ReadFromServer Runnable created");
        }

        @Override
        public void run() {
        }
    }

    public class WriteToServer implements Runnable{
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out){
            this.dataOut = out;
            System.out.println("ReadFromServer Runnable created");
        }

        @Override
        public void run() {
        }
    }


    private class DrawingComponent extends JComponent {
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            me.drawSprite(g2d);
            enemy.drawSprite(g2d);
        }
    }

    public static void main(String[] args) {
        PlayerFrame pf = new PlayerFrame(640, 480);
        pf.connectToServer();
        pf.setUpGUI();
    }
}
