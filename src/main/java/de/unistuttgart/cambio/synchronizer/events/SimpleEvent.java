package de.unistuttgart.cambio.synchronizer.events;

/**
 * @author Lion Wagner
 */
public class SimpleEvent extends  Event<Runnable>{

    public void invoke() {
        listeners.forEach(Runnable::run);
    }

}
