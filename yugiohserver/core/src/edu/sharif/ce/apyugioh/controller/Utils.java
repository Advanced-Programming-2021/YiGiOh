package edu.sharif.ce.apyugioh.controller;

import com.badlogic.gdx.utils.Base64Coder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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
            return String.valueOf(Base64Coder.encode(hash));
        } catch (Exception e) {
            System.out.println("can't encode password");
            System.exit(0);
        }
        return null;
    }

    public static void printError(String message) {
        System.out.println(message);
    }

    public static void printSuccess(String message) {
        System.out.println(message);
    }

    public static void printInfo(String message) {
        System.out.println(message);
    }

    public static void printSideBySide(String first, String second) {
        String[] firstLines = first.lines().toArray(String[]::new);
        String[] secondLines = second.lines().toArray(String[]::new);
        int counter = 0;
        while (counter < firstLines.length && counter < secondLines.length) {
            System.out.println(firstLines[counter] + " " + secondLines[counter]);
            counter++;
        }
        if (firstLines.length > secondLines.length) {
            while (counter < firstLines.length) {
                System.out.println(firstLines[counter]);
                counter++;
            }
        } else {
            while (counter < secondLines.length) {
                System.out.println(secondLines[counter]);
                counter++;
            }
        }
    }

    public static boolean almostEqual(float a, float b) {
        return Math.abs(a - b) < 0.1f;
    }

    public static boolean almostEqual(float a, float b, float precision) {
        return Math.abs(a - b) < precision;
    }

}
