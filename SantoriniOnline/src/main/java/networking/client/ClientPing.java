package networking.client;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.TimeoutException;

public class ClientPing implements Runnable {

    private final InetAddress serverIP;
    private final int pingRate = 1000;
    private final int pingTimeout = 2000;
    public ClientPing(InetAddress serverIP) {
        this.serverIP = serverIP;
    }

    public void run() {
        try {
            continuousPing();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void continuousPing() throws InterruptedException, IOException, TimeoutException {
        while (true) {
            Thread.sleep(3000);
            if (!serverIP.isReachable(pingTimeout)) {
                throw new TimeoutException();
            }
            else {
                //System.out.println("ping ok");
            }

        }
    }
}
