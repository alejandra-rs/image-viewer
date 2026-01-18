package software.ulpgc.imageviewer.architecture.model;

import static java.lang.Math.max;

public record Canvas(int width, int height) {

    public static Canvas ofSize(int width, int height) {
        return new Canvas(width, height);
    }

    public Canvas fit(int width, int height) {
        if (width <= this.width && height <= this.height) return new Canvas(width, height);
        return ratio(width, height) > ratio(this.width, this.height) ?
                Canvas.ofSize(this.width, height * this.width / width) :
                Canvas.ofSize(width * this.height / height, this.height);
    }

    public Canvas scale(double factor) {
        return new Canvas((int) (width * factor), (int) (height * factor));
    }

    public Canvas offsetLimit(int containerWidth, int containerHeight) {
        return new Canvas(max(0, (width - containerWidth) / 2), max(0, (height - containerHeight) / 2));
    }

    private double ratio(int width, int height) {
        return width / (double) height;
    }
}