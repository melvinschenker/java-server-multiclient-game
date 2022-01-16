package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private final int maxPlayers;

    private Socket p1Socket;
    private Socket p2Socket;
    private ReadFromClient p1ReadRunnable;
    private ReadFromClient p2ReadRunnable;
    private WriteToClient p1WriteRunnable;
    private WriteToClient p2WriteRunnable;

    private double p1x, p1y, p2x, p2y;

    public GameServer() {
        System.out.println("=== GAME SERVER ===");
        numPlayers = 0;
        maxPlayers = 2;

        p1x = 100;
        p1y = 400;
        p2x = 490;
        p2y = 400;


        try {
            ss = new ServerSocket(5555);
        } catch (IOException ex) {
            System.out.println("IOException from GameServer");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");

            while (numPlayers < maxPlayers){
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                numPlayers++;
                out.writeInt(numPlayers);
                System.out.println("Player #" + numPlayers + " has connected.");

                ReadFromClient rfc = new ReadFromClient(numPlayers, in);
                WriteToClient wtc = new WriteToClient(numPlayers, out);

                if(numPlayers == 1) {
                    p1Socket = s;
                    p1ReadRunnable = rfc;
                    p1WriteRunnable = wtc;
                } else {
                    p2Socket = s;
                    p2ReadRunnable = rfc;
                    p2WriteRunnable = wtc;

                    p1WriteRunnable.sendStartMsg();
                    p2WriteRunnable.sendStartMsg();

                    // Threads for reading
                    Thread readThread1 = new Thread(p1ReadRunnable);
                    Thread readThread2 = new Thread(p2ReadRunnable);
                    readThread1.start();
                    readThread2.start();

                    // Threads for reading
                    Thread writeThread1 = new Thread(p1WriteRunnable);
                    Thread writeThread2 = new Thread(p2WriteRunnable);
                    writeThread1.start();
                    writeThread2.start();
                }
            }
            System.out.println("No longer accepting connections.");

        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections()");
        }
    }

    private class WriteToClient implements Runnable {

        private int playerID;
        private DataOutputStream dataOut;

        public WriteToClient(int pid, DataOutputStream out){
            playerID = pid;
            dataOut = out;

            System.out.println("WriteFromClient: #" + playerID + " Runnable created");
        }

        public void sendStartMsg() {
            try {
                dataOut.writeUTF("We now have 2 players.");
            } catch (IOException ex) {
                System.out.println("IOException from sendStartMsg().");
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (playerID == 1) {
                        //dataOut.writeDouble(p2x);
                        //dataOut.writeDouble(p2y);
                        dataOut.writeDouble(p2ReadRunnable.getP2x());
                        dataOut.writeDouble(p2ReadRunnable.getP2y());
                        dataOut.flush();
                    } else {
                        //dataOut.writeDouble(p1x);
                        //dataOut.writeDouble(p1y);
                        dataOut.writeDouble(p1ReadRunnable.getP1x());
                        dataOut.writeDouble(p1ReadRunnable.getP1y());
                        dataOut.flush();
                    }

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException ex) {
                        System.out.println("InterruptedException from WTC run().");
                    }
                }
            } catch (IOException ex) {
                System.out.println("IOException from WTC run().");
            }
        }
}

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
    }
}
