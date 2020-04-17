package networking.client;

import event.gameEvents.lobby.VC_ConnectionRequestGameEvent;

public class ClientMain {

    public static void main(String[] args) {
        System.out.println(VC_ConnectionRequestGameEvent.class.toString());
        new SantoriniClient().begin();
    }
}
