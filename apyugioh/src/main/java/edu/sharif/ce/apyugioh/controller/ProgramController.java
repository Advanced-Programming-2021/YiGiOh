package edu.sharif.ce.apyugioh.controller;

import edu.sharif.ce.apyugioh.model.MenuState;
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
import picocli.CommandLine.Help.Ansi;
import picocli.shell.jline3.PicocliCommands;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Supplier;

public class ProgramController {

    private static ProgramController instance;
    private static MenuState state;
    private static LineReader reader;

    static {
        instance = new ProgramController();
        state = MenuState.LOGIN;
    }

    public static ProgramController getInstance() {
        return instance;
    }

    public static MenuState getState() {
        return state;
    }

    public static void setState(MenuState state) {
        ProgramController.state = state;
    }

    public static String getPromptTitle() {
        return Ansi.AUTO.string("@|yellow " + Utils.firstUpperOnly(ProgramController.getState().name()) + " Menu>|@");
    }

    public void initialize() {
        DatabaseController.init();
        parseCommands();
    }

    public void parseCommands() {
        try {
            Supplier<Path> workDir = () -> Paths.get(System.getProperty("user.dir"));
            Builtins builtins = new Builtins(workDir, null, null);
            CliCommands commands = new CliCommands();
            PicocliCommands.PicocliCommandsFactory factory = new PicocliCommands.PicocliCommandsFactory();
            CommandLine cmd = new CommandLine(commands, factory).setCaseInsensitiveEnumValuesAllowed(true)
                    .setAbbreviatedOptionsAllowed(true);
            PicocliCommands picocliCommands = new PicocliCommands(cmd);
            Terminal terminal = TerminalBuilder.builder().build();
            Parser parser = new DefaultParser();
            SystemRegistry systemRegistry = new SystemRegistryImpl(parser, terminal, workDir, null);
            systemRegistry.setCommandRegistries(picocliCommands);
            systemRegistry.register("help", picocliCommands);
            reader = LineReaderBuilder.builder().terminal(terminal).completer(getCompleter(systemRegistry))
                    .parser(parser).variable(LineReader.LIST_MAX, 50).build();
            builtins.setLineReader(reader);
            factory.setTerminal(terminal);
            while (true) {
                if (getCommand(systemRegistry, reader)) return;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            AnsiConsole.systemUninstall();
        }
    }

    private Completer getCompleter(SystemRegistry systemRegistry) {
        Completer completer = systemRegistry.completer();
        ArgumentCompleter menuCompleter = new ArgumentCompleter(new StringsCompleter("menu"),
                new StringsCompleter("enter"), new EnumCompleter(MenuState.class));
        return new AggregateCompleter(completer, menuCompleter);
    }

    private boolean getCommand(SystemRegistry systemRegistry, LineReader reader) {
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

    public LineReader getReader() {
        return reader;
    }

    @CommandLine.Command(name = "", description = {"Yu-Gi-Oh! Duel Links"},
            subcommands = {MenuCommand.class, UserCommand.class, ProfileCommand.class, ScoreboardCommand.class,
                    PicocliCommands.ClearScreen.class, CommandLine.HelpCommand.class})
    class CliCommands implements Runnable {
        PrintWriter out;

        CliCommands() {
        }

        public void run() {
            out.println(new CommandLine(this).getUsageMessage());
        }
    }
}
