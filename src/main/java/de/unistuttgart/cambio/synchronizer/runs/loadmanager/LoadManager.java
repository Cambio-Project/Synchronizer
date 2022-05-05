package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import de.unistuttgart.cambio.synchronizer.state.ISettingsAware;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class LoadManager implements ISettingsAware {
    private LoadProcessState state = LoadProcessState.NOT_STARTED;
    private UUID runID;
    private File defaultOutputDir;


    protected Set<OnLoadFinishListener> finishListener = new HashSet<>();

    public abstract Collection<File> getResults();

    public abstract void startLoad();

    public abstract void cancelLoad();

    public void registerOnLoadFinishedListener(OnLoadFinishListener listener) {
        finishListener.add(listener);
    }

    public void notifyFinished() {
        finishListener.forEach(OnLoadFinishListener::onLoadFinished);
    }

    public synchronized void setState(LoadProcessState state) {
        this.state = state;
    }

    public final LoadProcessState getLoadProcessState() {
        return state;
    }

    public void setRunID(UUID runID) {
        this.runID = runID;
    }

    public UUID getRunID() {
        return runID;
    }

    public void setDefaultOutputDir(File defaultOutputDir) {
        this.defaultOutputDir = defaultOutputDir;
    }

    public File getDefaultOutputDir() {
        return defaultOutputDir;
    }
}
