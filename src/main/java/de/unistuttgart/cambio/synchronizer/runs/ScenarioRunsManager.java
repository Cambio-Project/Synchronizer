package de.unistuttgart.cambio.synchronizer.runs;

import de.datev.httploadgenerator.profile.definitions.RunSettingsDto;
import de.unistuttgart.cambio.synchronizer.runs.loadmanager.LoadProcessState;
import de.unistuttgart.cambio.synchronizer.scenarios.Scenario;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Lion Wagner
 */
public class ScenarioRunsManager {

    private final static ScenarioRunsManager instance = new ScenarioRunsManager();


    public static ScenarioRunsManager getInstance() {
        return instance;
    }

    private Set<ScenarioRunDirector> directors = new HashSet<>();

    public void startRun(RunSettingsDto runSettingsDto, File ctkFile) {
        var director = new ScenarioRunDirector(runSettingsDto, ctkFile);
        _startRun(director);
    }

    public void startRun(File ctkFile) {
        var director = new ScenarioRunDirector(ctkFile);
        _startRun(director);
    }

    public void startRun(RunSettingsDto runSettingsDto) {
        var director = new ScenarioRunDirector(runSettingsDto);
        _startRun(director);
    }

    public void startRun(Scenario scenario) {
        var director = new ScenarioRunDirector(scenario);
        _startRun(director);
    }

    private void _startRun(ScenarioRunDirector director) {
        if (director.getGeneralLoadProcessState() != LoadProcessState.NOT_STARTED) {
            throw new IllegalStateException("Director was already started.");
        }
        directors.add(director);
        director.addRunIDListener(uuid -> {
            //Nothing to implement yet
        });
        director.start();
    }


    private void joinAll(){
        directors.forEach(director -> {
            try {
                director.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }


}
