package rasterops;

import imagedata.Image;
import io.vavr.collection.Stream;
import org.jetbrains.annotations.NotNull;

public class LineRasterizerDDA<PixelType> implements LineRasterizer<PixelType> {
    @NotNull
    @Override
    public Image<PixelType> rasterize(
            @NotNull Image<PixelType> background,
            double x1, double y1, double x2, double y2,
            @NotNull PixelType value) {
        final double ix1 = (x1 + 1) * background.getWidth() / 2;
        final double iy1 = (-y1 + 1) * background.getHeight() / 2;
        final double ix2 = (x2 + 1) * background.getWidth() / 2;
        final double iy2 = (-y2 + 1) * background.getHeight() / 2;
        final double dx = ix2 - ix1, dy = iy2 - iy1;
        final double steps = (Math.abs(dx) > Math.abs(dy)) ? Math.abs(dx) : Math.abs(dy);
        final double ddx = dx / steps, ddy = dy / steps;
        return Stream.rangeClosed(0, (int) steps)
                     .foldLeft(background,
                             (currentImage, i) -> currentImage.withValue(
                                     (int) (ix1 + i * ddx),
                                     (int) (iy1 + i * ddy), value)
                     );
    }
}

