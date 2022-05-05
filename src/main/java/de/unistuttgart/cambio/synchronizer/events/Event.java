package de.unistuttgart.cambio.synchronizer.events;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Lion Wagner
 */
class Event<T> {

    protected final Set<T> listeners = Collections.synchronizedSet(new HashSet<>());

    public synchronized boolean subscribe(T listener) {
        return listeners.add(listener);
    }

    public synchronized boolean unsubscribe(T listener) {
        return listeners.remove(listener);
    }


}
