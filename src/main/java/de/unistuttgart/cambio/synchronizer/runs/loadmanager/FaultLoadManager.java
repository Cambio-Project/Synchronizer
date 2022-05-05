package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import de.unistuttgart.cambio.synchronizer.runs.FaultLoadDefinition;

import java.io.File;
import java.util.Collection;

public class FaultLoadManager extends LoadManager {

    private final FaultLoadDefinition faultLoadDefinition;
    private FaultLoadExecutor executor;


    public FaultLoadManager(FaultLoadDefinition faultLoadDefinition) {
        super();
        this.faultLoadDefinition = faultLoadDefinition;
    }

    @Override
    public Collection<File> getResults() {
        return null;
    }

    @Override
    public void startLoad() {
        if (!faultLoadDefinition.hasLoadDefinition()) {
            notifyFinished();
            return;
        }
        switch (faultLoadDefinition.getFaultLoadType()) {
            case CTK:
                executor = new CTKLoadExecutor(faultLoadDefinition);
                break;
            case PUMBA:
                executor = new PumbaLoadExecutor(faultLoadDefinition);
                break;
            case NONE:
                notifyFinished();
                return;
        }

        executor.RunLoad();

    }

    @Override
    public synchronized void cancelLoad() {
        if (executor != null) executor.CancelLoad();
    }
}
