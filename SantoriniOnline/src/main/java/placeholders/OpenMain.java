package placeholders;

import controller.Lobby;
import model.exception.InvalidMovementException;
import view.VirtualView;

import static event.core.ListenerType.VIEW;

/**
 * DEBUG CLASS, USED ONLY FOR TESTING PURPOSE
 */
public class OpenMain {
    public static void main(String[] args) throws CloneNotSupportedException, InvalidMovementException {

        Lobby lobby = new Lobby();

        VirtualServer openServer1 = new OpenVirtualServer(lobby);
        OpenCLIView CLIView1 = new OpenCLIView();
        CLIView1.virtualServer = openServer1;
        CLIView1.virtualServer.attachListenerByType(VIEW, CLIView1);
        CLIView1.attachListenerByType(VIEW, CLIView1.virtualServer);

//        openServer1.username = "Pippo";
        VirtualView openVirtualView1 = openServer1.virtualView;
        openVirtualView1.userIP = "1234";
        openVirtualView1.userPort = 1234;
//        openVirtualView1.username = openServer1.username;
//        CC_ConnectionRequestGameEvent request1 = new CC_ConnectionRequestGameEvent("", "", 1234, openVirtualView1, openServer1.username);
        CLIView1.start();

//        AVVIO CLIENT2
        VirtualServer openServer2 = new OpenVirtualServer(lobby);

        OpenCLIView CLIView2 = new OpenCLIView();
        CLIView2.virtualServer = openServer2;
        CLIView2.virtualServer.attachListenerByType(VIEW, CLIView2);
        CLIView2.attachListenerByType(VIEW, CLIView2.virtualServer);

//        openServer2.username = "Pluto";
        VirtualView openVirtualView2 = openServer2.virtualView;
        openVirtualView2.userIP = "4321";
        openVirtualView2.userPort = 1234;
//        openVirtualView2.username = openServer2.username;
//        CC_ConnectionRequestGameEvent request2 = new CC_ConnectionRequestGameEvent("", "", 1234, openVirtualView2, openServer2.username);
        CLIView2.start();

//        lobby.handleEvent(request1);
//        VC_RoomSizeResponseGameEvent roomSizeResponse = new VC_RoomSizeResponseGameEvent("", 2);
//        lobby.handleEvent(roomSizeResponse);
//        lobby.handleEvent(request2);

    }
}
