package edu.sharif.ce.apyugioh;


import edu.sharif.ce.apyugioh.controller.MenuState;
import edu.sharif.ce.apyugioh.controller.ProgramController;
import edu.sharif.ce.apyugioh.controller.Utils;
import edu.sharif.ce.apyugioh.view.ImageToASCII;
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
import org.jline.reader.impl.completer.AggregateCompleter;
import org.jline.reader.impl.completer.ArgumentCompleter;
import org.jline.reader.impl.completer.EnumCompleter;
import org.jline.reader.impl.completer.StringsCompleter;
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
        parseCommands();
    }

    private static void parseCommands() {
        try {
            Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
            Builtins builtins = new Builtins(workDir, null, null);
            CliCommands commands = new CliCommands();
            PicocliCommandsFactory factory = new PicocliCommandsFactory();
            CommandLine cmd = new CommandLine(commands, factory).setCaseInsensitiveEnumValuesAllowed(true)
                    .setAbbreviatedOptionsAllowed(true);
            PicocliCommands picocliCommands = new PicocliCommands(cmd);
            Terminal terminal = TerminalBuilder.builder().build();
            Parser parser = new DefaultParser();
            SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, workDir, null);
            systemRegistry.setCommandRegistries(picocliCommands);
            systemRegistry.register("help", picocliCommands);
            LineReader reader = LineReaderBuilder.builder().terminal(terminal).completer(getCompleter(systemRegistry))
                    .parser(parser).variable(LineReader.LIST_MAX, 50).build();
            builtins.setLineReader(reader);
            factory.setTerminal(terminal);
            System.out.print(new ImageToASCII("characters/YamiYugi", (float) 2).getASCII());
            while (true) {
                if (getCommand(systemRegistry, reader)) return;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    private static Completer getCompleter(SystemRegistry systemRegistry) {
        Completer completer = systemRegistry.completer();
        ArgumentCompleter menuCompleter = new ArgumentCompleter(new StringsCompleter("menu"),
                new StringsCompleter("enter"), new EnumCompleter(MenuState.class));
        return new AggregateCompleter(completer, menuCompleter);
    }

    private static boolean getCommand(SystemRegistry systemRegistry, LineReader reader) {
        try {
            systemRegistry.cleanUp();
            String line = reader.readLine(ProgramController.getPromptTitle(), null, (MaskingCallback) null,
                    null);
            systemRegistry.execute(line);
        } catch (UserInterruptException ignored) {
        } catch (EndOfFileException e) {
            return true;
        } catch (Exception e) {
            Utils.printError("invalid command");
            //systemRegistry.trace(e);
        }
        return false;
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
