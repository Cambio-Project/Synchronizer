package de.unistuttgart.cambio.synchronizer.runs.loadmanager;

import de.unistuttgart.cambio.synchronizer.runs.FaultLoadDefinition;
import de.unistuttgart.cambio.synchronizer.runs.FaultLoadType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lion Wagner
 */
public final class CTKLoadExecutor extends FaultLoadExecutor {

    protected CTKLoadExecutor(FaultLoadDefinition loadDefinition) {
        super(loadDefinition, FaultLoadType.CTK);
    }

    @Override
    protected Process RunLoadInternal() {
        try {
            validateExperiment();


            List<String> command = new ArrayList<>();
            command.add("chaos");
            command.add("run");
            command.add(loadDefinition.getData());

            Process process = new ProcessBuilder(command)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE).start(); //preferred way to start a process by Oracle


            return process;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void validateExperiment() throws IOException {
        List<String> command = new ArrayList<>();
        command.add("chaos");
        command.add("validate");
        command.add(loadDefinition.getData().split(" ")[0]); //TODO: define experiment as first parameter

        try {
            Process process = new ProcessBuilder(command)
                    .redirectOutput(ProcessBuilder.Redirect.PIPE)
                    .redirectError(ProcessBuilder.Redirect.PIPE).start(); //preferred way to start a process by Oracle
            int exitValue = process.waitFor();
            if (exitValue != 0) {

            }
        } catch (InterruptedException e) {
            throw new IllegalArgumentException("Validation of experiment failed",e);
        }

    }

    @Override
    protected void CancelLoadInternal() {
        if (process != null && process.isAlive())
            process.destroy();
    }
}
