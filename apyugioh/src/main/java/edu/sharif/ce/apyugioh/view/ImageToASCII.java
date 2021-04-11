package edu.sharif.ce.apyugioh.view;

import picocli.CommandLine.Help.Ansi;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageToASCII {

    private final String imageName;
    private final StringBuilder output;
    private final float scale;

    public ImageToASCII(String imageName, float scale) {
        this.imageName = imageName;
        this.scale = scale;
        output = new StringBuilder();
    }

    public ImageToASCII(String imageName) {
        this(imageName, 1);
    }

    public String getASCII() {
        try {
            BufferedImage cardImage = ImageIO.read(new File("assets/" + imageName + ".png"));
            int count = Math.round(getCount(cardImage) / scale);
            for (int i = 0; i <= cardImage.getHeight() - 2 * count; i += 2 * count) {
                for (int j = 0; j <= cardImage.getWidth() - count; j += count) {
                    double greyscaleValue = avgVal(cardImage, j, i, count);
                    int rgbValue = avgColorValue(cardImage, j, i, count);
                    output.append(colorize(greyscaleChar(greyscaleValue), rgbValue));
                }
                output.append(System.lineSeparator());
            }
            return output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int getCount(BufferedImage cardImage) {
        int maxDim = cardImage.getWidth();
        if (cardImage.getHeight() > maxDim) maxDim = cardImage.getHeight();
        return maxDim / 60;
    }

    private double avgVal(BufferedImage cardImage, int width, int height, int count) {
        double value = 0, counter = 0;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 2 * count; j++) {
                Color pixcol = new Color(cardImage.getRGB(width + i, height + j));
                if (pixcol.getRed() < 10 && pixcol.getGreen() < 10 && pixcol.getBlue() < 10) continue;
                value += (((pixcol.getRed() * 0.2126) + (pixcol.getBlue() * 0.0722) + (pixcol
                        .getGreen() * 0.7152)));
                counter++;
            }
        }
        if (counter > 0) return value / counter;
        return 0;
    }

    private int avgColorValue(BufferedImage cardImage, int width, int height, int count) {
        int redValue = 0, greenValue = 0, blueValue = 0, counter = 0;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 2 * count; j++) {
                Color pixcol = new Color(cardImage.getRGB(width + i, height + j));
                if (pixcol.getRed() < 10 && pixcol.getGreen() < 10 && pixcol.getBlue() < 10) continue;
                if (pixcol.getRed() > 235 && pixcol.getGreen() > 235 && pixcol.getBlue() > 235) continue;
                redValue += pixcol.getRed();
                greenValue += pixcol.getGreen();
                blueValue += pixcol.getBlue();
                counter++;
            }
        }
        if (counter > 0) {
            redValue /= counter;
            greenValue /= counter;
            blueValue /= counter;
            return 16 + (int) (36 * Math.round(redValue / 51.0) + 6 * Math.round(greenValue / 51.0) + Math.round(blueValue / 51.0));
        }
        return 16;
    }

    private char greyscaleChar(double g) {
        String greyscale = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ";
        return greyscale.charAt((int) Math.round(g * 69 / 255));
    }

    private String colorize(char text, int val) {
        return Ansi.AUTO.string("@|fg(" + val + ") " + text + "|@");
    }
}
