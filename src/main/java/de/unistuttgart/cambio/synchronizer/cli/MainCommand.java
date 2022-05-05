package de.unistuttgart.cambio.synchronizer.cli;

import de.datev.httploadgenerator.profile.definitions.RunSettingsDto;
import de.datev.httploadgenerator.runner.cli.DirectorCommand;
import de.unistuttgart.cambio.synchronizer.runs.IScenarioRunsManagerAware;
import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.io.File;

/**
 * @author Lion Wagner
 */
@CommandLine.Command(name = "CambioSynchronizer",
        header = "Run in command mode.",
        showDefaultValues = true,
        customSynopsis = "@|bold java -jar CambioSynchronizer.jar [@|yellow <options>|@...]",
        description = "Runs the Synchronizer in command mode. Work and fault profiles given by " +
                "the arguments will be executed synchronously. Scenarios will be prioitized over ChaosToolkit and HTTPLoadgenerator profiles." +
                "If there is no profile path and no script path, HttpLoadgenerator settings will be ignored completely."
)
public class MainCommand extends DirectorCommand implements IScenarioRunsManagerAware {

    @Option(names = {"-f", "--chaos-profile", "--fault-profile"},
            paramLabel = "FAULT_PROFILE",
            description = "Location of the chaos toolkit  @|yellow f|@ault profile that should be executed synchronously.")
    protected File faultProfile;

    @Option(names = {"-c", "--scenario", "--cambio-scenario"},
            paramLabel = "SCENARIO",
            description = "Location of the cambio s@|yellow c|@enario that should be executed synchronously" +
                    "@|yellow Warning:|@ using this options disables the usage of explicit ChaosToolkit and HTTPLoadgenerator profiles.")
    protected File scenarioFile;


    @Override
    public void run() {
        if (this.profilePath != null || this.scriptPath != null) { //user tries to run a
            super.run();
        } else {
            runDirectorOnSettings(null);
        }
    }

    @Override
    protected void runDirectorOnSettings(RunSettingsDto settingsDto) {
        if (scenarioFile != null) {

        } else {
            if (settingsDto == null && faultProfile == null) {
                throw new IllegalArgumentException("at least one profile has to be provided.");
            }
            SCENARIO_RUNS_MANAGER.startRun(settingsDto, faultProfile);
        }

    }

    public void setFaultProfile(File faultProfile) {
        this.faultProfile = faultProfile;
    }

    public void setScenarioFile(File scenarioFile) {
        this.scenarioFile = scenarioFile;
    }
}
