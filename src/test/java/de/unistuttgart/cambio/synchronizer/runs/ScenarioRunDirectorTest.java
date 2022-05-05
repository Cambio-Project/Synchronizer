package de.unistuttgart.cambio.synchronizer.runs;

import de.datev.httploadgenerator.runner.IRunnerConstants;
import de.unistuttgart.cambio.synchronizer.runs.loadmanager.LoadManager;
import de.unistuttgart.cambio.synchronizer.runs.loadmanager.WorkloadManager;
import de.unistuttgart.cambio.synchronizer.state.ISettingsAware;
import de.unistuttgart.cambio.synchronizer.test.TestUtils;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Files;

class ScenarioRunDirectorTest implements ISettingsAware {

    private ScenarioRunDirector scenarioRunDirector;
    private static ServerSocket mockSocket; //mocking loadgenerator server

    @BeforeAll
    static void beforeAll() {
        SETTINGS.setFilesystemOutputRoot(new File("./TestResults/"));
        SETTINGS.setLoadgeneratorIPs(new String[]{"127.0.0.1"});
        try {
            mockSocket = new ServerSocket(IRunnerConstants.DEFAULT_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            try {
                while (true) {
                    val s = mockSocket.accept();
                    val stream = s.getInputStream();
                    InputStreamReader reader = new InputStreamReader(stream);
                    try {
                        while (reader.ready()) {
                            reader.read();
                        }
                    } catch (Exception e) {
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @BeforeEach
    void setUp() {
        val testDto = TestUtils.createRunSettingsDto();
        scenarioRunDirector = new ScenarioRunDirector(testDto);
    }

    @Test
    public void createsOutputFolderCorrectly() {
        scenarioRunDirector.start();
        Assertions.assertTrue(Files.isDirectory(SETTINGS.getFilesystemOutputRoot().toPath()));
        Assertions.assertTrue(SETTINGS.getFilesystemOutputRoot().listFiles().length > 0);
        scenarioRunDirector.cancelRun();
    }


    @AfterAll
    static void afterAll() throws IOException {
        FileUtils.deleteDirectory(SETTINGS.getFilesystemOutputRoot());
        mockSocket.close();
    }

}