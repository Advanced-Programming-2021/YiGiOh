package edu.sharif.ce.apyugioh.view;

import picocli.CommandLine.Help.Ansi;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageToASCII {

    private final String imageName;
    private final StringBuilder output;
    private final float scale;
    private final boolean isTrue;

    public ImageToASCII(String imageName, double scale, boolean isTrue) {
        this.imageName = imageName;
        this.scale = (float) scale;
        this.isTrue = isTrue;
        output = new StringBuilder();
    }

    public ImageToASCII(String imageName) {
        this(imageName, 1, true);
    }

    public ImageToASCII(String imageName, boolean isTrue) {
        this(imageName, 1, isTrue);
    }

    public ImageToASCII(String imageName, double scale) {
        this(imageName, scale, true);
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
                int pixelColor = cardImage.getRGB(width + i, height + j);
                if (getAlpha(pixelColor) < 5) continue;
                counter[2 * (i) / count][(j) / count]++;
            }
        }
        int upLeftEmpty = counter[0][0] == 0 ? 8 : 0;
        int upRightEmpty = counter[0][1] == 0 ? 4 : 0;
        int downLeftEmpty = counter[1][0] == 0 ? 2 : 0;
        int downRightEmpty = counter[1][1] == 0 ? 1 : 0;
        int total = upLeftEmpty + upRightEmpty + downLeftEmpty + downRightEmpty;
        if (count == 1) return arts[0];
        return arts[total];
    }

    private int averageRGBColor(BufferedImage cardImage, int width, int height, int count) {
        List<Integer> rgbValues = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            for (int j = 0; j < 2 * count; j++) {
                int pixelColor = cardImage.getRGB(width + i, height + j);
                if (getAlpha(pixelColor) < 5) continue;
                rgbValues.add(pixelColor);
            }
        }
        if (!rgbValues.isEmpty()) {
            Collections.sort(rgbValues);
            int mean = rgbValues.get(rgbValues.size() / 2), meanRed = getRed(mean), meanGreen = getGreen(mean), meanBlue = getBlue(mean);
            int diff = isTrue ? 256 : 30;
            rgbValues.removeIf(e -> Math.abs(getRed(e) - meanRed) > diff || Math.abs(getGreen(e) - meanGreen) > diff ||
                    Math.abs(getBlue(e) - meanBlue) > diff);
            int averageRed = (int) rgbValues.stream().mapToInt(this::getRed).average().getAsDouble();
            int averageGreen = (int) rgbValues.stream().mapToInt(this::getGreen).average().getAsDouble();
            int averageBlue = (int) rgbValues.stream().mapToInt(this::getBlue).average().getAsDouble();
            return 16 + (int) (36 * Math.round(averageRed / 51.0) + 6 * Math.round(averageGreen / 51.0) +
                    Math.round(averageBlue / 51.0));
        }
        return 16;
    }

    private String colorize(String text, int val) {
        return Ansi.AUTO.string("@|fg(" + val + ") " + text + "|@");
    }

    private int getAlpha(int rgb) {
        return (rgb >> 24) & 0xff;
    }

    private int getRed(int rgb) {
        return (rgb >> 16) & 0xFF;
    }

    private int getGreen(int rgb) {
        return (rgb >> 8) & 0xFF;
    }

    private int getBlue(int rgb) {
        return rgb & 0xFF;
    }

}
