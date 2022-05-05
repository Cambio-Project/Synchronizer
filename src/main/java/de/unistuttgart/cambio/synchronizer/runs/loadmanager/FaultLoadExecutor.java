package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import de.unistuttgart.cambio.synchronizer.events.SimpleEvent;
import de.unistuttgart.cambio.synchronizer.runs.FaultLoadDefinition;
import de.unistuttgart.cambio.synchronizer.runs.FaultLoadType;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * @author Lion Wagner
 */
public abstract class FaultLoadExecutor {

    public SimpleEvent LoadStarted = new SimpleEvent();
    public SimpleEvent LoadCanceled = new SimpleEvent();
    public SimpleEvent LoadFinished = new SimpleEvent();

    protected final FaultLoadDefinition loadDefinition;
    protected Process process;

    protected FaultLoadExecutor(FaultLoadDefinition loadDefinition, FaultLoadType type) {
        Objects.requireNonNull(loadDefinition);
        if (loadDefinition.getFaultLoadType() != type) {
            throw new IllegalArgumentException(String.format("This FaultLoadExecutor only accepts FaultLoadDefinitions of type %s", type.toString()));
        }
        this.loadDefinition = loadDefinition;
    }

    public final void RunLoad() {
        LoadStarted.invoke();
        process = RunLoadInternal();
        LoadFinished.invoke();
    }

    public final void CancelLoad() {
        CancelLoadInternal();
        LoadCanceled.invoke();
    }

    protected abstract Process RunLoadInternal();

    protected abstract void CancelLoadInternal();

    public final OutputStream getOutputStream(){
        if(process !=null){
            return process.getOutputStream();
        }
        return null;
    }

    public final InputStream getErrorStream(){
        if(process !=null){
            return process.getErrorStream();
        }
        return null;
    }
}
