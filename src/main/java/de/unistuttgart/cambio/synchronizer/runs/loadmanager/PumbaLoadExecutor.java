package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import de.unistuttgart.cambio.synchronizer.runs.FaultLoadDefinition;
import de.unistuttgart.cambio.synchronizer.runs.FaultLoadType;
import org.apache.commons.lang3.NotImplementedException;

/**
 * @author Lion Wagner
 */
public class PumbaLoadExecutor extends FaultLoadExecutor {

    protected PumbaLoadExecutor(FaultLoadDefinition loadDefinition) {
        super(loadDefinition, FaultLoadType.PUMBA);
    }

    @Override
    protected Process RunLoadInternal() {
        throw new NotImplementedException();
    }

    @Override
    protected void CancelLoadInternal() {
        throw new NotImplementedException();
    }
}
