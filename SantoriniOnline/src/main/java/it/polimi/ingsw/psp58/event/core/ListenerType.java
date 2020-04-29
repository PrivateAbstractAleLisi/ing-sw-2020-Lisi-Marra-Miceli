package it.polimi.ingsw.psp58.event.core;

/**
 * @author Alessandro Lisi
 * different categories of listeners that get notified by an EventSource
 */
public enum ListenerType {
    GENERIC,
    VIEW,
    CONTROLLER,
    DEBUG,
    ERROR
}