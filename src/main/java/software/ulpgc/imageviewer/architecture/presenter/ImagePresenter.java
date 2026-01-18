package software.ulpgc.imageviewer.architecture.presenter;

import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.ImageDisplay;

import software.ulpgc.imageviewer.architecture.ui.ImageDisplay.*;

import java.util.function.Consumer;

import static java.lang.Math.abs;

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
        this.display.paint(new ImageDisplay.Paint(image.bitmap(), 0));
    }

    public void glideToPrev() {
        animate(0, display.width());
    }

    public void glideToNext() {
        animate(0, -display.width());
    }

    public Image image() {
        return image;
    }

    private void paintWhenShifted(int offset) {
        display.paint(new Paint(image.bitmap(), offset), secondaryPaint(offset));
    }

    private void paintWhenReleased(int offset) {
        animate(offset, targetOffset(offset));
    }

    private void animate(int start, int target) {
        animation.animate(75,
                t -> nextStep(start, target, t),
                () -> endTransition(target)
        );
    }

    private Paint secondaryPaint(int offset) {
        return offset < 0
                ? new Paint(image.next().bitmap(), display.width() + offset)
                : new Paint(image.previous().bitmap(), offset - display.width());
    }

    private void nextStep(int start, int target, double t) {
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
        if (isImageSwitching(offset)) image = offset < 0 ? image.next() : image.previous();
        display.paint(new Paint(image.bitmap(), 0));
    }

    public interface Animation {
        void animate(long duration, Consumer<Double> animationStep, Runnable onEnd);
    }
}
