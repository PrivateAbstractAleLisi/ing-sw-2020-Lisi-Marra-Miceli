package event.core;


import event.gameEvents.GameEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Alessandro Lisi
 * an observable objects that generates events notifying all the attached listeners
 */
public abstract class EventSource {

    private Map<ListenerType, List<EventListener>> listeners; //all

    public EventSource() {
        this.listeners = new HashMap<ListenerType, List<EventListener>>();
        for (ListenerType li : ListenerType.values()) {
            listeners.put(li, new ArrayList<EventListener>()); //riempi la mappa 2d
        }
    }

    /**
     * @param type  the category of listeners that will be notified
     * @param event notified by
     */
    public void notifyAllObserverByType(ListenerType type, GameEvent event) {
//        System.out.println("DEBUG: ready to notify:");
//        System.out.println("DEBUG: DESCRIPTION: " + event.getEventDescription());

        List<EventListener> observersFiltered = listeners.get(type);
        for (EventListener observerGameObject : observersFiltered) {

            //perform the right cast
//            if (event instanceof WelcomeGameEvent) {
//                WelcomeGameEvent cast = (WelcomeGameEvent) event;
//                observerGameObject.handleEvent(cast); //handles the event
//            } else if (event instanceof ConnectionRequestGameEvent) {
//                ConnectionRequestGameEvent cast = (ConnectionRequestGameEvent) event;
//                observerGameObject.handleEvent(cast); //handles the event
//            }
            event.notifyHandler(observerGameObject);
        }
    }

    /**
     * attaches a listener to this object by a category
     *
     * @param type     the category in which the listener will be registered
     * @param listener the listener that will be registered
     */
    public void attachListenerByType(ListenerType type, EventListener listener) {
        List<EventListener> users = listeners.get(type);
        users.add(listener);
//        listeners.get(type).add(listener);
//        System.out.println("DEBUG: listener added to event manager" + type + listener.toString());

    }

    /**
     * detaches the listener from all the categories
     *
     * @param listener the listener that will be removed
     */
    public void detachByType(EventListener listener) {
        for (ListenerType type : listeners.keySet()) {
            listeners.get(type).remove(listener);
        }
    }

    /**
     * detaches the listener from a single category
     *
     * @param type     the category from which the listener will be detached
     * @param listener the listener that will be detached
     */
    public void detachListenerByType(ListenerType type, EventListener listener) {
        listeners.get(type).remove(listener);
    }

}
