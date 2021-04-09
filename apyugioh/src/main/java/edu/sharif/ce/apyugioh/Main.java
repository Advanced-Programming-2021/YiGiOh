package edu.sharif.ce.apyugioh;


import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.view.command.MenuCommand;
import edu.sharif.ce.apyugioh.view.command.ProfileCommand;
import edu.sharif.ce.apyugioh.view.command.ScoreboardCommand;
import edu.sharif.ce.apyugioh.view.command.UserCommand;
import org.fusesource.jansi.AnsiConsole;
import org.jline.console.SystemRegistry;
import org.jline.console.impl.Builtins;
import org.jline.console.impl.SystemRegistryImpl;
import org.jline.reader.*;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.shell.jline3.PicocliCommands;
import picocli.shell.jline3.PicocliCommands.PicocliCommandsFactory;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class Main {

    public static void main(String[] args) {
        System.out.println("salam sadegh");
        ProgramController.getInstance().initialize();
        AnsiConsole.systemInstall();
        try {
            Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
            Builtins builtins = new Builtins(workDir, null, null);
            CliCommands commands = new CliCommands();

            PicocliCommandsFactory factory = new PicocliCommandsFactory();

            CommandLine cmd = new CommandLine(commands, factory);
            cmd.setCaseInsensitiveEnumValuesAllowed(true);
            PicocliCommands picocliCommands = new PicocliCommands(cmd);

            Parser parser = new DefaultParser();
            try (Terminal terminal = TerminalBuilder.builder().build()) {
                SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, workDir, null);
                systemRegistry.setCommandRegistries(picocliCommands);
                systemRegistry.register("help", picocliCommands);

                LineReader reader = LineReaderBuilder.builder()
                        .terminal(terminal)
                        .completer(systemRegistry.completer())
                        .parser(parser)
                        .variable(LineReader.LIST_MAX, 50)   // max tab completion candidates
                        .build();
                builtins.setLineReader(reader);
                factory.setTerminal(terminal);

                String line;
                while (true) {
                    try {
                        systemRegistry.cleanUp();
                        line = reader.readLine(ProgramController.getPromptTitle(), null, (MaskingCallback) null,
                                null);
                        systemRegistry.execute(line);
                    } catch (UserInterruptException ignored) {
                    } catch (EndOfFileException e) {
                        return;
                    } catch (Exception e) {
                        //systemRegistry.trace(e);
                        Utils.printError("invalid command");
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    @Command(name = "", description = {"Yu-Gi-Oh! Duel Links"},
            subcommands = {MenuCommand.class, UserCommand.class, ProfileCommand.class, ScoreboardCommand.class,
                    PicocliCommands.ClearScreen.class, CommandLine.HelpCommand.class})
    static class CliCommands implements Runnable {
        PrintWriter out;

        CliCommands() {
        }

        public void run() {
            out.println(new CommandLine(this).getUsageMessage());
        }
    }

}
