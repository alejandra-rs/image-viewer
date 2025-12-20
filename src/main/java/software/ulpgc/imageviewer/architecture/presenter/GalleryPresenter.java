package software.ulpgc.imageviewer.architecture.presenter;

import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;

public class GalleryPresenter {
    private final GalleryDisplay display;
    private final Image[] images;

    public GalleryPresenter(GalleryDisplay display, Image[] images) {
        this.display = display;
        this.images = images;
    }

    public void showAll() {
        this.display.paint(images);
    }
}
