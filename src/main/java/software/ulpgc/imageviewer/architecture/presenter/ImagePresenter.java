package software.ulpgc.imageviewer.architecture.presenter;

import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.ImageDisplay;

import software.ulpgc.imageviewer.architecture.ui.ImageDisplay.*;

public class ImagePresenter {
    private final ImageDisplay display;
    private Image image;

    public ImagePresenter(ImageDisplay display) {
        this.display = display;
        this.display.on((ImageDisplay.Shift) this::paintWhenShifted);
        this.display.on((ImageDisplay.Released) this::paintWhenReleased);
    }

    private void paintWhenShifted(int offset) {
        display.paint(
                new Paint(image.bitmap(), offset),
                offset < 0 ? new Paint(image.next().bitmap(), display.width() + offset)
                           : new Paint(image.previous().bitmap(), offset - display.width())
        );
    }

    private void paintWhenReleased(int offset) {
        if (Math.abs(offset) * 2 > display.width()) image = offset < 0 ? image.next() : image.previous();
        display.paint(new ImageDisplay.Paint(image.bitmap(), 0));
    }

    public void show(Image image) {
        this.image = image;
        this.display.paint(new ImageDisplay.Paint(image.bitmap(), 0));
    }

    public Image image() {
        return image;
    }
}
