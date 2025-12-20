package software.ulpgc.imageviewer.architecture.model;

public record Canvas(int width, int height) {

    public static Canvas ofSize(int width, int height) {
        return new Canvas(width, height);
    }

    public Canvas fit(int width, int height) {
        return Canvas.ofSize((int) (width * ratio(width, height)),
                             (int) (height * ratio(width, height)));
    }

    private double ratio(int width, int height) {
        return height >= width ? this.height / (double) height : this.width / (double) width;
    }
}