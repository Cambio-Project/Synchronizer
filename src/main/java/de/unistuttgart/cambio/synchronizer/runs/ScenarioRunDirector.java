package de.unistuttgart.cambio.synchronizer.runs;

import com.google.common.primitives.Longs;
import de.datev.httploadgenerator.profile.definitions.RunSettingsDto;
import de.unistuttgart.cambio.synchronizer.events.DataEvent;
import de.unistuttgart.cambio.synchronizer.runs.loadmanager.FaultLoadManager;
import de.unistuttgart.cambio.synchronizer.runs.loadmanager.LoadManager;
import de.unistuttgart.cambio.synchronizer.runs.loadmanager.LoadProcessState;
import de.unistuttgart.cambio.synchronizer.runs.loadmanager.WorkloadManager;
import de.unistuttgart.cambio.synchronizer.scenarios.Scenario;
import de.unistuttgart.cambio.synchronizer.state.ISettingsAware;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class handles the communication and synchronization between the workload and faultload managers
 */
@Slf4j
class ScenarioRunDirector extends Thread implements ISettingsAware {
    private DataEvent<UUID> generatedRunID_Event = new DataEvent<>();

    private final WorkloadManager workloadManager;
    private final FaultLoadManager faultloadManger;

    private UUID runID;
    private File outputDir;


    public ScenarioRunDirector(Scenario scenario) {
        //TODO: scenario Parsing
        this(new WorkloadDefinition(null), new FaultLoadDefinition(FaultLoadType.NONE));
    }

    public ScenarioRunDirector(RunSettingsDto runSettingsDto) {
        this(new WorkloadDefinition(runSettingsDto), new FaultLoadDefinition(FaultLoadType.NONE));
    }

    public ScenarioRunDirector(File ctkFile) {
        this(new WorkloadDefinition(null), new FaultLoadDefinition(FaultLoadType.CTK, ctkFile.getAbsolutePath()));
    }

    public ScenarioRunDirector(RunSettingsDto runSettingsDto, File ctkFile) {
        this(new WorkloadDefinition(runSettingsDto), new FaultLoadDefinition(FaultLoadType.CTK, ctkFile.getAbsolutePath()));
    }

    private ScenarioRunDirector(WorkloadDefinition wld, FaultLoadDefinition fld) {
        workloadManager = new WorkloadManager(wld);
        faultloadManger = new FaultLoadManager(fld);

        workloadManager.addOnWarmupListener(this::onWarmupFinished);
        workloadManager.registerOnLoadFinishedListener(this::onWorkloadFinished);
        faultloadManger.registerOnLoadFinishedListener(this::onFaultloadFinished);
    }

    private void logInfo(String string) {
        if (runID != null)
            log.info(String.format("%s: %s", runID, string));
        else
            log.info(String.format("not started Run of %s: %s", this.hashCode(), string));
    }

    private void logWarn(String string) {
        if (runID != null)
            log.warn(String.format("%s: %s", runID, string));
        else
            log.warn(String.format("not started Run of %s: %s", this.hashCode(), string));
    }

    @Override
    public void run() {
        super.run();
        runID = UUID.nameUUIDFromBytes(Longs.toByteArray(System.currentTimeMillis()));
        generatedRunID_Event.invoke(runID);
        logInfo("Starting Run");

        outputDir = new File(SETTINGS.getFilesystemOutputRoot(), runID.toString()).getAbsoluteFile();
        outputDir.mkdirs(); //prepare output dir

        workloadManager.setRunID(runID);
        faultloadManger.setRunID(runID);
        workloadManager.setDefaultOutputDir(outputDir);
        faultloadManger.setDefaultOutputDir(outputDir);

        //starts workload including warmup, should trigger onWarmupFinished once warmup is done
        workloadManager.startLoad();
    }

    private synchronized void onWarmupFinished() {
        faultloadManger.startLoad();
    }

    private void onWorkloadFinished() {
        onLoadFinished(this.workloadManager);
    }

    private void onFaultloadFinished() {
        onLoadFinished(this.faultloadManger);
    }

    private synchronized void onLoadFinished(LoadManager loadManager) {
        String name = loadManager.getClass().isAssignableFrom(WorkloadManager.class) ? "Workload" : "Faultload";
        String otherLoadType = name.equals("Workload") ? "Faultload" : "Workload";
        logInfo("Finished " + name);
        if (loadManager.getLoadProcessState() == LoadProcessState.RUNNING) {
            logWarn(name + " finished before " + otherLoadType);
        }
        checkForFinish();
    }

    private void checkForFinish() {
        if (workloadManager.getLoadProcessState() != LoadProcessState.RUNNING &&
                faultloadManger.getLoadProcessState() != LoadProcessState.RUNNING) {
            logInfo("Finished run");
            printState();
            collectResults();
        }
    }

    public void printState() {
        logInfo(String.format("[State] Workload: %s ; Faultload %s", workloadManager.getLoadProcessState(), faultloadManger.getLoadProcessState()));
        logInfo(String.format("[Combined State] %s ", getGeneralLoadProcessState()));
    }

    private void collectResults() {

        Collection<File> results = new LinkedList<>() {
        };
        results.addAll(workloadManager.getResults());
        results.addAll(faultloadManger.getResults());


        results.stream()
                .filter(file -> !file.getParentFile().equals(outputDir))
                .forEach(
                        file -> {
                            try {
                                Files.copy(file.toPath(), Paths.get(outputDir.getAbsolutePath(), file.getName()));
                            } catch (IOException e) {
                                log.error(String.format("Could not copy output file %s to destination %s", file.getAbsolutePath(), outputDir.getAbsolutePath()));
                            }
                        });
    }

    public synchronized LoadProcessState getGeneralLoadProcessState() {
        var state1 = workloadManager.getLoadProcessState();
        var state2 = faultloadManger.getLoadProcessState();
        if (state1.equals(LoadProcessState.NOT_STARTED) && state2.equals(LoadProcessState.NOT_STARTED))
            return LoadProcessState.NOT_STARTED;
        if (state1.equals(LoadProcessState.CANCELED) && state2.equals(LoadProcessState.CANCELED))
            return LoadProcessState.CANCELED;

        if (state1.equals(LoadProcessState.RUNNING) || state2.equals(LoadProcessState.RUNNING))
            return LoadProcessState.RUNNING;

        if (state1.equals(LoadProcessState.FAILED) || state2.equals(LoadProcessState.FAILED))
            return LoadProcessState.FAILED;

        return LoadProcessState.FINISHED;

    }

    public synchronized File getOutputDir() {
        return outputDir;
    }

    public synchronized UUID getRunID() {
        return runID;
    }

    public synchronized boolean addRunIDListener(Consumer<UUID> listener) {
        return generatedRunID_Event.subscribe(listener);
    }


    public synchronized void cancelRun() {
        workloadManager.cancelLoad();
        faultloadManger.cancelLoad();
    }
}
