package networking.client;

import event.gameEvents.lobby.VC_ConnectionRequestGameEvent;

public class ClientMain {

    public static void main(String[] args) {

        new SantoriniClient().begin();
    }
}
