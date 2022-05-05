package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

public enum LoadProcessState {
    NOT_STARTED,
    STARTING,
    RUNNING,
    CANCELED,
    FAILED,
    FINISHED;

    @Override
    public String toString() {
        return this.name();
    }
}
