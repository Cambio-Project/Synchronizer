package de.unistuttgart.cambio.synchronizer;

import de.unistuttgart.cambio.synchronizer.cli.MainCommand;
import de.unistuttgart.cambio.synchronizer.cli.WebServerCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "synchronizer",
        subcommands = {WebServerCommand.class})
public class Main extends MainCommand {
    public static void main(String[] args) {
        var main = new Main();
        var line = new CommandLine(main);

        line.execute(args);
    }
}

