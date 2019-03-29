package imagedata;


import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PresenterAWT<PixelType> implements Presenter<PixelType, Graphics> {

    @NotNull
    @Override

    public Graphics present(@NotNull Image<PixelType> image,
                            @NotNull Graphics device) {
        if (image instanceof ImageAWT) {
            BufferedImage img = ((ImageAWT) image).getImg();
            device.drawImage(img, 0, 0, null);
        } else {
            System.err.println("Cannot present image of type " + image.getClass());
        }
        return device;
    }

}
