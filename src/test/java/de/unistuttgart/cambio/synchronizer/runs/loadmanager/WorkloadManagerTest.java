package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import de.datev.httploadgenerator.runner.ProfileCreator;
import de.unistuttgart.cambio.synchronizer.runs.WorkloadDefinition;
import de.unistuttgart.cambio.synchronizer.state.ISettingsAware;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import static de.unistuttgart.cambio.synchronizer.test.TestUtils.*;

@Slf4j
class WorkloadManagerTest implements ISettingsAware {

    WorkloadManager wm;


    @BeforeAll
    static void beforeAll() {
        var outputDir = new File("./TestResults/");
        SETTINGS.setFilesystemOutputRoot(outputDir);
        outputDir.mkdirs();
    }

    @AfterAll
    static void tearDown() throws IOException {
        FileUtils.deleteDirectory(SETTINGS.getFilesystemOutputRoot().getAbsoluteFile());
    }

    @Test
    void createsResults() throws IOException {

        SETTINGS.setLoadgeneratorIPs(new String[]{"127.0.0.1"});

        wm = new WorkloadManager(new WorkloadDefinition(null));//create workload manager with an empty workload
        var outputdir = Paths.get(SETTINGS.getFilesystemOutputRoot().getAbsolutePath(), "createsResultsTest").toFile();
        outputdir.mkdirs();
        wm.setDefaultOutputDir(outputdir);

        wm.startLoad();
        Assertions.assertTrue(Arrays.stream(Objects.requireNonNull(outputdir.listFiles())).anyMatch(file -> file.getName().equals("aggregateResults.csv")));
        Assertions.assertTrue(Arrays.stream(Objects.requireNonNull(outputdir.listFiles())).anyMatch(file -> file.getName().equals("perRequestTypeResults.csv")));
        wm.cancelLoad();

    }

    @Test
    void testRun() throws Exception {
//        new ProcessBuilder("docker restart c502904fd917".split(" ")).start().waitFor();


        val IP = grabLocalIP();
        SETTINGS.setLoadgeneratorIPs(new String[]{IP});

        var settingsDto = createExampleRunSettingsDto();
        var workloadDefinition = new WorkloadDefinition(settingsDto);
        var workloadManager = new WorkloadManager(workloadDefinition);

        var outputdir = Paths.get(SETTINGS.getFilesystemOutputRoot().getAbsolutePath(), "testRun").toFile();
        outputdir.mkdirs();
        workloadManager.setDefaultOutputDir(outputdir);

        workloadManager.startLoad();

    }

    @Test
    void throwsNoLoadgeneratorExceptionOnEmpty() {
        SETTINGS.setLoadgeneratorIPs(new String[]{});
        Assertions.assertThrows(NoLoadgeneratorException.class,
                () -> new WorkloadManager(new WorkloadDefinition(createRunSettingsDto())));
    }

    @Test
    void throwsUncheckedIOExceptionOnWrongLoadgeneratorIP() {
        SETTINGS.setLoadgeneratorIPs(new String[]{"254.144.186.254"});
        Assertions.assertThrows(UncheckedIOException.class, () -> new WorkloadManager(new WorkloadDefinition(createRunSettingsDto())));
        SETTINGS.setLoadgeneratorIPs(new String[]{"ase5asdfas"});
        Assertions.assertThrows(UncheckedIOException.class, () -> new WorkloadManager(new WorkloadDefinition(createRunSettingsDto())));
        SETTINGS.setLoadgeneratorIPs(new String[]{"256.256.256.256"});
        Assertions.assertThrows(UncheckedIOException.class, () -> new WorkloadManager(new WorkloadDefinition(createRunSettingsDto())));
    }


}