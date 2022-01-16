package Server;

import java.io.DataOutputStream;
import java.io.IOException;

public class WriteToClient implements Runnable {
    private final ReadFromClient p1ReadRunnable;
    private final ReadFromClient p2ReadRunnable;

    private final int playerID;
    private final DataOutputStream dataOut;

    public WriteToClient(int playerID, DataOutputStream dataOut, ReadFromClient p1ReadRunnable, ReadFromClient p2ReadRunnable){
        this.playerID = playerID;
        this.dataOut = dataOut;
        this.p1ReadRunnable = p1ReadRunnable;
        this.p2ReadRunnable = p2ReadRunnable;

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
                    dataOut.writeDouble(p2ReadRunnable.getP2x());
                    dataOut.writeDouble(p2ReadRunnable.getP2y());
                    dataOut.flush();
                } else {
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
