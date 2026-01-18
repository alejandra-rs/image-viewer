package software.ulpgc.imageviewer.architecture.presenter;

import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.ImageDisplay;

import software.ulpgc.imageviewer.architecture.ui.ImageDisplay.*;

import java.util.function.Consumer;

import static java.lang.Math.abs;
import static java.lang.Math.max;

public class ImagePresenter {
    private final ImageDisplay display;
    private final Animation animation;
    private Image image;

    public ImagePresenter(ImageDisplay display, Animation animation) {
        this.display = display;
        this.animation = animation;
        this.display.on((ImageDisplay.Shift) this::paintWhenShifted);
        this.display.on((ImageDisplay.Released) this::paintWhenReleased);
    }

    public void show(Image image) {
        this.image = image;
        display.zoom(1.0);
        this.display.paint(new Paint(image.bitmap(), 0));
    }

    public void glideToPrev() {
        animate(0, display.width(), display.zoom());
    }

    public void glideToNext() {
        animate(0, -display.width(), display.zoom());
    }

    public void zoom(double factor) {
        display.zoom(display.zoom() * factor);
    }

    public Image image() {
        return image;
    }

    private void paintWhenShifted(int offset) {
        display.paint(new Paint(image.bitmap(), offset), secondaryPaint(offset));
    }

    private void paintWhenReleased(int offset) {
        if (offset == 0) return;
        animate(offset, targetOffset(offset), display.zoom());
    }

    private void animate(int startPos, int targetPos, double startZoom) {
        animation.animate(75,
                t -> nextStep(startPos, targetPos, startZoom, t),
                () -> endTransition(targetPos)
        );
    }

    private Paint secondaryPaint(int offset) {
        int imageGap =  max((int)(display.width() * display.zoom()), display.width());
        return offset < 0
                ? new Paint(image.next().bitmap(), offset + imageGap)
                : new Paint(image.previous().bitmap(), offset - imageGap);
    }

    private void nextStep(int start, int target, double startZoom, double t) {
        display.zoom(startZoom + (1.0 - startZoom) * t);
        int currentPos = (int) (start + (target - start) * t);
        display.paint(new Paint(image.bitmap(), currentPos), secondaryPaint(currentPos));
    }

    private int targetOffset(int offset) {
        return isImageSwitching(offset) ? (offset < 0 ? -display.width() : display.width()) : 0;
    }

    private boolean isImageSwitching(int offset) {
        return abs(offset) * 2 > display.width();
    }

    private void endTransition(int offset) {
        if (isImageSwitching(offset)) adaptToSwitching(offset);
        display.paint(new Paint(image.bitmap(), 0));
    }

    private void adaptToSwitching(int offset) {
        image = offset < 0 ? image.next() : image.previous();
        display.zoom(1.0);
    }

    public interface Animation {
        void animate(long duration, Consumer<Double> animationStep, Runnable onEnd);
    }
}
