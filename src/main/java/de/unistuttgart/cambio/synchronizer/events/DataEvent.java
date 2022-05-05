package de.unistuttgart.cambio.synchronizer.events;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Lion Wagner
 */
public class DataEvent<T> extends Event<Consumer<T>> {

    public void invoke(T data) {
        listeners.forEach(listener -> listener.accept(data));
    }

}
