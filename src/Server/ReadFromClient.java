package Server;

import java.io.DataInputStream;
import java.io.IOException;

public class ReadFromClient implements Runnable{
    private final int playerID;
    private final DataInputStream dataIn;
    private double p1x, p1y, p2x, p2y;

    public ReadFromClient(int pid, DataInputStream in){
        playerID = pid;
        dataIn = in;

        System.out.println("ReadFromClient: #" + playerID + " Runnable created");
    }

    @Override
    public void run() {
        try {
            while (true){
                if(playerID == 1) {
                    p1x = dataIn.readDouble();
                    p1y = dataIn.readDouble();
                } else {
                    p2x = dataIn.readDouble();
                    p2y = dataIn.readDouble();
                }
            }
        } catch (IOException ex) {
            System.out.println("IOException from RFC run().");
        }
    }

    public double getP1x() {
        return p1x;
    }

    public double getP1y() {
        return p1y;
    }

    public double getP2x() {
        return p2x;
    }

    public double getP2y() {
        return p2y;
    }
}