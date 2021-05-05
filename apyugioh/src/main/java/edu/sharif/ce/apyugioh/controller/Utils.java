package edu.sharif.ce.apyugioh.controller;

import org.jline.reader.LineReader;
import org.jline.utils.InfoCmp.Capability;
import picocli.CommandLine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class Utils {

    public static String firstUpperOnly(String text) {
        String[] words = text.replaceAll("[-_]+", " ").split("\\s+");
        StringBuilder output = new StringBuilder();
        boolean isFirstWord = true;
        for (String word : words) {
            if (!isFirstWord) {
                output.append(" ");
            } else {
                isFirstWord = false;
            }
            output.append(Character.toUpperCase(word.charAt(0)));
            output.append(word.substring(1).toLowerCase());
        }
        return output.toString();
    }

    public static String hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            System.out.println("can't encode password");
            System.exit(0);
        }
        return null;
    }

    public static void printError(String message) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|red " + message + "|@"));
    }

    public static void printSuccess(String message) {
        System.out.println(CommandLine.Help.Ansi.AUTO.string("@|green " + message + "|@"));
    }

    public static void clearScreen() {
        if (ProgramController.getReader().isReading()) {
            ProgramController.getReader().callWidget(LineReader.CLEAR_SCREEN);
        } else {
            ProgramController.getReader().getTerminal().puts(Capability.clear_screen);
        }
    }

    public static void printHorizontalCenter(String data) {
        int width = ProgramController.getReader().getTerminal().getWidth();
        System.out.println(width);
        String[] rows = data.split("\\r?\\n");
        StringBuilder output = new StringBuilder();
        for (String row : rows) {
            int counter = 1;
            for (; row.length() > counter * width; counter++) {
                output.append(row, (counter - 1) * width, counter * width);
                output.append(System.lineSeparator());
            }
            String marginSpaces = " ".repeat(width - (row.length() - (counter - 1) * width));
            output.append(marginSpaces);
            output.append(row, (counter - 1) * width, row.length());
            output.append(marginSpaces);
            output.append(System.lineSeparator());
        }
        System.out.print(output);
    }

    public static double getTerminalScale(double scale) {
        if (scale >= 1) {
            return Math.max((ProgramController.getReader().getTerminal().getWidth() / 200.0) * scale, 1);
        }
        return (ProgramController.getReader().getTerminal().getWidth() / 200.0) * scale;
    }

}
