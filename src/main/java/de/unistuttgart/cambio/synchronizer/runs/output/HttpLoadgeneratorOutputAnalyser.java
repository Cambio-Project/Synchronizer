package de.unistuttgart.cambio.synchronizer.runs.output;

import de.datev.httploadgenerator.runner.output.IOutputWrapper;
import de.unistuttgart.cambio.synchronizer.events.SimpleEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpLoadgeneratorOutputAnalyser extends OutputAnalyser implements IOutputWrapper {

    public SimpleEvent onWarmupFinished = new SimpleEvent();
    public SimpleEvent onLoadFinished = new SimpleEvent();


    public HttpLoadgeneratorOutputAnalyser() {
        super();
    }

    /**
     * Prints a new line to the output.
     *
     * @param message
     */
    @Override
    public void println(String message) {
        System.out.println(message);

        if (message.startsWith("Starting Measurement")) {
            onWarmupFinished.invoke();
        }
    }

    /**
     * Resets the wrapper.
     */
    @Override
    public void reset() {

    }
}
