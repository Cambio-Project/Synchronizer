package de.unistuttgart.cambio.synchronizer.cli;

import picocli.CommandLine;

public abstract class BaseCommand implements Runnable{

    @CommandLine.Option(names = {"-l","--loadgenerators"})
    protected String[] loadgeneratorIPS;

    @CommandLine.Option(names = {"-h", "--help"}, usageHelp = true, description = "Display this help message.")
    private boolean helpRequested = false;
}
