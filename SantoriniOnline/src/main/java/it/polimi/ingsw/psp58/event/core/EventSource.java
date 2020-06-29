package it.polimi.ingsw.psp58.event.core;


import it.polimi.ingsw.psp58.event.gameEvents.GameEvent;
import javafx.application.Application;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alessandro Lisi
 * an observable objects that generates events notifying all the attached listeners
 */
public abstract class EventSource {

    private List<ViewListener> viewListeners;
    private List<ControllerListener> controllerListeners;

    public EventSource() {
        this.viewListeners = new ArrayList<>();
        this.controllerListeners = new ArrayList<>();
    }

    /**
     * @param type  the category of listeners that will be notified
     * @param event the event that will notify the listener
     */
    public synchronized void  notifyAllObserverByType(ListenerType type, GameEvent event) {

        switch (type){
            case VIEW:
                 for(ViewListener viewListener : viewListeners) {
                    if (viewListener instanceof Application){
                        Platform.runLater(() -> event.notifyHandler(viewListener));
                    } else {
                        event.notifyHandler(viewListener);
                    }
                 }
                 break;
            case CONTROLLER:
                for (ControllerListener controllerListener : controllerListeners){
                    event.notifyHandler(controllerListener);
                }
                break;
        }
    }

    /**
     * attaches a listener to this object by a category
     *
     * @param type     the category in which the listener will be registered
     * @param listener the listener that will be registered
     */
    public synchronized void attachListenerByType(ListenerType type, EventListener listener) {
        switch (type){
            case CONTROLLER:
                controllerListeners.add((ControllerListener) listener);
                break;
            case VIEW:
                viewListeners.add((ViewListener) listener);
                break;
        }
    }

    /**
     * detaches the listener from all the categories
     *
     * @param type the {@link ListenerType} of the listener passed as parameter
     * @param listener the listener that will be removed
     */
    public void detachListenerByType(ListenerType type, EventListener listener) {
        switch (type){
            case CONTROLLER:
                controllerListeners.remove((ControllerListener) listener);
                break;
            case VIEW:
                viewListeners.remove((ViewListener) listener);
                break;
        }
    }


}
