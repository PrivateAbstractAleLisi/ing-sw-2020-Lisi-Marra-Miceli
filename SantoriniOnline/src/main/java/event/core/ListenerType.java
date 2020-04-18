package event.core;

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