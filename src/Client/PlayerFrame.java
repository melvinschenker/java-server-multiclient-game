package Client;

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
            rfsRunnable.waitForStartMsg();

        } catch (IOException ex) {
            System.out.println("IOException from connectToServer().");
        }
    }

    private class DrawingComponent extends JComponent {
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            enemy.drawSprite(g2d);
            me.drawSprite(g2d);
        }
    }

    public class ReadFromServer implements Runnable{
        private DataInputStream dataIn;

        public ReadFromServer(DataInputStream in){
            dataIn = in;
            System.out.println("ReadFromServer Runnable created");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if(enemy != null) {
                        enemy.setX(dataIn.readDouble());
                        enemy.setY(dataIn.readDouble());
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from RFS run(),");
            }
        }

        public void waitForStartMsg() {
            try {
                String startMsg = dataIn.readUTF();
                System.out.println("Message from server: " + startMsg);

                // Starting threads after both player connected
                Thread readThread = new Thread(rfsRunnable);
                Thread writeThread = new Thread(wtsRunnable);
                readThread.start();
                writeThread.start();

            } catch (IOException ex) {
                System.out.println("IOException from waitForStartMsg().");
            }
        }
    }

    public class WriteToServer implements Runnable{
        private DataOutputStream dataOut;

        public WriteToServer(DataOutputStream out){
            dataOut = out;
            System.out.println("ReadFromServer Runnable created");
        }

        @Override
        public void run() {
            try {
                while (true){
                    if (me != null) {
                        dataOut.writeDouble(me.getX());
                        dataOut.writeDouble(me.getY());
                        dataOut.flush();
                    }
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedException from WTS run().");
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from WTS run().");
            }
        }
    }

    public static void main(String[] args) {
        PlayerFrame pf = new PlayerFrame(640, 480);
        pf.connectToServer();
        pf.setUpGUI();
    }
}
