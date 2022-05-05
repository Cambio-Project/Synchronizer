package de.unistuttgart.cambio.synchronizer.runs.output;

import de.unistuttgart.cambio.synchronizer.runs.loadmanager.IOutputAnalyserListener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

abstract class OutputAnalyser {

    protected final Set<IOutputAnalyserListener> listeners= new HashSet<>();

    public OutputAnalyser(IOutputAnalyserListener... listeners) {
        Arrays.stream(listeners).forEach(this::addListener);
    }

    public final void addListener(Iterable<IOutputAnalyserListener> listeners) {
        listeners.forEach(this::addListener);
    }

    public final void addListener(IOutputAnalyserListener listener) {
        listeners.add(listener);
    }

    public final void removeListener(IOutputAnalyserListener listener) {
        listeners.remove(listener);
    }

}
