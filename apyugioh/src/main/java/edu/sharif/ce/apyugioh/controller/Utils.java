package edu.sharif.ce.apyugioh.controller;

import picocli.CommandLine;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class Utils {

    public static String firstUpperOnly(String text) {
        String[] words = text.replaceAll("_", " ").split("\\s+");
        StringBuilder output = new StringBuilder();
        for (String word : words) {
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

}
