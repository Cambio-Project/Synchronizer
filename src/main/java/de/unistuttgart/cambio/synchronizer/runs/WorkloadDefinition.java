package de.unistuttgart.cambio.synchronizer.runs;

import de.datev.httploadgenerator.profile.definitions.RunSettingsDto;

public class WorkloadDefinition extends LoadDefinition {

    private final RunSettingsDto runSettingsDto;

    public WorkloadDefinition(RunSettingsDto runSettingsDto) {
        this.runSettingsDto = runSettingsDto;
    }

    public RunSettingsDto getRunSettingsDto() {
        return runSettingsDto;
    }

    @Override
    public boolean hasLoadDefinition() {
        return runSettingsDto != null;
    }
}
