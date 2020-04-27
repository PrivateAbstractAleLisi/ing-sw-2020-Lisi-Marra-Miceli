package networking;

import java.net.Socket;

public class PingThread implements Runnable {

    Socket client;
    @Override
    public void run() {
        startContinuosPing();
    }

    private void startContinuosPing() {

    }

}
