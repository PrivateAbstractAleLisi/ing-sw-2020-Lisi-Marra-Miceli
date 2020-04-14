package placeholders;

import controller.Lobby;
import event.core.EventListener;
import event.core.ListenerType;
import event.gameEvents.GameEvent;
import event.gameEvents.lobby.*;
import event.gameEvents.prematch.*;
import view.VirtualView;

/**
 * DEBUG CLASS, USED ONLY FOR TESTING PURPOSE: public attribute
 */
public class OpenVirtualServer extends VirtualServer implements EventListener {
    public VirtualView virtualView;

    //todo AFTER DEBUG: remove username
    public String username;

    public OpenVirtualServer() {
        virtualView = new VirtualView(new Lobby(), this);
    }

    public OpenVirtualServer(Lobby lobby) {
        this.virtualView = new VirtualView(lobby, this);
    }

    //TO CONTROLLER

    @Override
    public void handleEvent(VC_ConnectionRequestGameEvent event) {
        this.username=event.getUsername();
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_RoomSizeResponseGameEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_ChallengerCardsChosenEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_PlayerCardChosenEvent event) {
        virtualView.handleEvent(event);
    }

    @Override
    public void handleEvent(VC_ChallengerChosenFirstPlayerEvent event) {
        virtualView.handleEvent(event);
    }


    //    TO VIEW

    @Override
    public void handleEvent(CV_RoomSizeRequestGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_RoomUpdateGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_ChallengerChosenEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_ChallengerChooseFirstPlayerRequestEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_CardChoiceRequestGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_WaitGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }

    @Override
    public void handleEvent(CV_ConnectionRejectedErrorGameEvent event) {
        notifyAllObserverByType(ListenerType.VIEW, event);
    }


    //    NOT IMPLMENTED
    @Override
    public void handleEvent(CC_ConnectionRequestGameEvent event) {
        System.err.println("CC_Connection has been called ");
    }


    @Override
    public void handleEvent(GameEvent event) {
        System.err.println("generic <handleEvent> has been called ");
    }
}
