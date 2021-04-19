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
                    int rgbValue = averageRGBColor(cardImage, j, i, count);
                    output.append(colorize(pixelToBoxArt(cardImage, j, i, count), rgbValue));
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

    private String pixelToBoxArt(BufferedImage cardImage, int width, int height, int count) {
        int[][] counter = new int[2][2];
        String[] arts = new String[]{"█", "▛", "▜", "▀", "▙", "▌", "▚", "▘", "▟", "▞", "▐", "▝", "▄", "▖", "▗", " "};
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 2 * count; j++) {
                Color pixelColor = new Color(cardImage.getRGB(width + i, height + j), true);
                if (pixelColor.getAlpha() < 5) continue;
                counter[2 * (i) / count][(j) / count]++;
            }
        }
        int upLeftEmpty = counter[0][0] == 0 ? 8 : 0;
        int upRightEmpty = counter[0][1] == 0 ? 4 : 0;
        int downLeftEmpty = counter[1][0] == 0 ? 2 : 0;
        int downRightEmpty = counter[1][1] == 0 ? 1 : 0;
        int total = upLeftEmpty + upRightEmpty + downLeftEmpty + downRightEmpty;
        return arts[total];
    }

    private int averageRGBColor(BufferedImage cardImage, int width, int height, int count) {
        int redValue = 0, greenValue = 0, blueValue = 0, counter = 0;
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 2 * count; j++) {
                Color pixelColor = new Color(cardImage.getRGB(width + i, height + j), true);
                if (pixelColor.getAlpha() < 5) continue;
                redValue += pixelColor.getRed();
                greenValue += pixelColor.getGreen();
                blueValue += pixelColor.getBlue();
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

    private String colorize(String text, int val) {
        return Ansi.AUTO.string("@|fg(" + val + ") " + text + "|@");
    }
}
