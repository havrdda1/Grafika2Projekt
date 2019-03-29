package imageops;

import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Optional;
import java.util.function.Function;

public class ImageAWT<PixelType> implements Image<PixelType> {
    private final @NotNull BufferedImage img;
    private final @NotNull Function<PixelType, Integer> pixelType2Integer;
    private final @NotNull Function<Integer, PixelType> integer2PixelType;

    public ImageAWT(final @NotNull BufferedImage img,
                    final @NotNull Function<PixelType, Integer> pixelType2Integer,
                    final @NotNull Function<Integer, PixelType> integer2PixelType) {
        this.img = img;
        this.pixelType2Integer = pixelType2Integer;
        this.integer2PixelType = integer2PixelType;
    }

    @NotNull
    @Override
    public Optional<PixelType> getValue(int c, int r) {
        if (0 <= c && c < img.getWidth() && 0 <= r && r < img.getHeight())
            return Optional.of(integer2PixelType.apply(img.getRGB(c, r)));
        return Optional.empty();

    }

    @NotNull
    @Override
    public Image<PixelType> withValue(int c, int r, @NotNull PixelType value) {
        if (0 <= c && c < img.getWidth() && 0 <= r && r < img.getHeight()) {
            img.setRGB(c, r, pixelType2Integer.apply(value));
        }
        return this;
    }

    @NotNull
    @Override
    public Image<PixelType> cleared(@NotNull PixelType value) {
        Graphics gr = img.getGraphics();
        gr.setColor(new Color(pixelType2Integer.apply(value)));
        gr.fillRect(0, 0, img.getWidth(), img.getHeight());
        return this;
    }

    @Override
    public int getWidth() {
        return img.getWidth();
    }

    @Override
    public int getHeight() {
        return img.getHeight();
    }

    @NotNull BufferedImage getImg() {
        return img;
    }
}
