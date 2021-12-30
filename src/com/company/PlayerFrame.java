package com.company;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class PlayerFrame extends JFrame {

    private int width, height;
    private Container contentPane;
    private PlayerSprite me;
    private DrawingComponent dc;
    private Timer animationTimer;
    private boolean up, down, left, right;

    public PlayerFrame(int width, int height) {
        this.width = width;
        this.height = height;
        up = false;
        down = false;
        left = false;
        right = false;
    }

    public void setUpGUI() {
        contentPane = this.getContentPane();
        this.setTitle("-----");
        contentPane.setPreferredSize(new Dimension(width, height));
        createSprites();

        dc = new DrawingComponent();
        contentPane.add(dc);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.pack();
        this.setVisible(true);

        setUpAnimationTimer();
        setUpKeyListener();
    }

    private void createSprites(){
        me = new PlayerSprite(100,400,50,Color.BLUE);
    }

    private void setUpAnimationTimer() {
        int interval = 10;
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double speed = 5;
                if(up){
                    me.moveV(-speed);
                }
                dc.repaint();
            }
        };
        animationTimer = new Timer(interval, al);
        animationTimer.start();
    }

    private void setUpKeyListener() {
        KeyListener kl = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                switch (keyCode){
                    case KeyEvent.VK_UP :
                        up = true;
                        break;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                int keyCode = e.getKeyCode();

                switch (keyCode){
                    case KeyEvent.VK_UP :
                        up = false;
                        break;
                }
            }
        };
        contentPane.addKeyListener(kl);
        contentPane.setFocusable(true);
    }

    private class DrawingComponent extends JComponent {
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            me.drawSprite(g2d);
        }
    }

    public static void main(String[] args) {
        PlayerFrame pf = new PlayerFrame(640, 480);
        pf.setUpGUI();
    }
}
