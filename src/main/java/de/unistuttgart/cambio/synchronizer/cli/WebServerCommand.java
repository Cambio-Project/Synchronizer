package de.unistuttgart.cambio.synchronizer.cli;

import picocli.CommandLine;

@CommandLine.Command(name = "webserver",
        header = "Run in webserver mode.",
        showDefaultValues = true,
        customSynopsis = "@|bold java -jar httploadgenerator.jar |@@|red director|@ [@|yellow <options>|@...]",
        description = "Runs the load generator in director mode. The director parses configuration files, "
                + "connects to one or multiple load generators, and writes the results to the result csv file."
)
public class WebServerCommand extends BaseCommand{

    @Override
    public void run() {
        System.out.println("Hello From WebServer");
    }
}
