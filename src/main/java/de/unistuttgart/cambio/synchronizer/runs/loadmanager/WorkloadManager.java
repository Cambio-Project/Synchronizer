package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import de.datev.httploadgenerator.persistence.AbstractResultPersister;
import de.datev.httploadgenerator.persistence.AggregateResultCSVWriter;
import de.datev.httploadgenerator.persistence.PerRequestTypeResultCSVWriter;
import de.datev.httploadgenerator.persistence.influxdb.InfluxDbWriter;
import de.datev.httploadgenerator.runner.Director;
import de.datev.httploadgenerator.runner.ProfileCreator;
import de.unistuttgart.cambio.synchronizer.events.SimpleEvent;
import de.unistuttgart.cambio.synchronizer.runs.WorkloadDefinition;
import de.unistuttgart.cambio.synchronizer.runs.output.HttpLoadgeneratorOutputAnalyser;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorkloadManager extends LoadManager {
    private final Director httpLoadgeneratorDirector;
    private final HttpLoadgeneratorOutputAnalyser outputAnalyiser;
    private final WorkloadDefinition workloadDefinition;
    private final List<AbstractResultPersister> persisters = new ArrayList<>();
    private File aggregateResults;
    private File perRequestTypeResults;
    private final SimpleEvent onWarmupFinished = new SimpleEvent();

    public WorkloadManager(WorkloadDefinition workloadDefinition) {
        super();
        this.workloadDefinition = workloadDefinition;
        this.outputAnalyiser = new HttpLoadgeneratorOutputAnalyser();

        Director tmpDirector = null;
        if (workloadDefinition.hasLoadDefinition()) {
            if (SETTINGS.getLoadgeneratorIPs().length == 0) {
                throw new NoLoadgeneratorException();
            } else {
                tmpDirector = new Director(SETTINGS.getLoadgeneratorIPs(), outputAnalyiser);
            }
        }
        this.httpLoadgeneratorDirector = tmpDirector;
        outputAnalyiser.onWarmupFinished.subscribe(this::invokeWarmupListener);
    }

    /**
     * Creates and registers the persisters for the run related to this
     */
    private void createPersisters() {

        //connect InfluxDB if connection string is given
        if (!StringUtils.isBlank(SETTINGS.getInfluxDBURL())) {
            var influxdbPersister = new InfluxDbWriter(SETTINGS.getInfluxDBURL(),
                    "manual execution: " + ProfileCreator.humanReadableDescriptionForVirtualUserProfile(workloadDefinition.getRunSettingsDto().getRequestProfile()));
            persisters.add(influxdbPersister);
        }

        aggregateResults = Paths.get(getDefaultOutputDir().getAbsolutePath(), "aggregateResults.csv").toFile();
        perRequestTypeResults = Paths.get(getDefaultOutputDir().getAbsolutePath(), "perRequestTypeResults.csv").toFile();

        try {
            var aggregateResultPersister = new AggregateResultCSVWriter(aggregateResults.getAbsolutePath());
            var perRequestTypeResultPersister = new PerRequestTypeResultCSVWriter(perRequestTypeResults.getAbsolutePath());
            persisters.add(aggregateResultPersister);
            persisters.add(perRequestTypeResultPersister);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Collection<File> getResults() {
        return new ArrayList<>() {
            {
                add(aggregateResults);
                add(perRequestTypeResults);
            }
        };
    }

    @Override
    public void startLoad() {
        setState(LoadProcessState.STARTING);
        createPersisters();
        if (httpLoadgeneratorDirector != null) {
            if (!workloadDefinition.hasLoadDefinition()) {
                invokeWarmupListener();
            }
            httpLoadgeneratorDirector.process(workloadDefinition.getRunSettingsDto(), persisters);
        }
    }

    @Override
    public synchronized void cancelLoad() {
        if (httpLoadgeneratorDirector != null) {
            httpLoadgeneratorDirector.cancel();
        }
        setState(LoadProcessState.CANCELED);
        try {
            for (AbstractResultPersister persister : persisters) {
                persister.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void addOnWarmupListener(Runnable onWarmupFinished) {
        this.onWarmupFinished.subscribe(onWarmupFinished);
    }

    private void invokeWarmupListener() {
        this.onWarmupFinished.invoke();
    }

}
