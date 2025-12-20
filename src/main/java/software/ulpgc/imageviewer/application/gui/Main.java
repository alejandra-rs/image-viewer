package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.application.FileImageStore;
import software.ulpgc.imageviewer.architecture.control.GalleryCommand;
import software.ulpgc.imageviewer.architecture.control.NextCommand;
import software.ulpgc.imageviewer.architecture.control.PrevCommand;
import software.ulpgc.imageviewer.architecture.io.ImageStore;
import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.model.ImageProvider;
import software.ulpgc.imageviewer.architecture.presenter.GalleryPresenter;
import software.ulpgc.imageviewer.architecture.presenter.ImagePresenter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Main {
    private static File root;

    public static void main(String[] args) throws IOException {
        root = new File("images");

        ImageStore store = new FileImageStore(root);
        ImageProvider imageProvider = ImageProvider.with(store.images());
        SwingImageDisplay imageDisplay = new SwingImageDisplay();
        ImagePresenter imagePresenter = new ImagePresenter(imageDisplay);
        SwingGalleryDisplay swingGalleryDisplay = new SwingGalleryDisplay();

        imagePresenter.show(imageProvider.first(Main::readImage));
        Image[] images = imageProvider.all(Main::readImage);

        Desktop desktop = Desktop.create()
                .put("next", new NextCommand(imagePresenter))
                .put("prev", new PrevCommand(imagePresenter))
                .put("gallery", new GalleryCommand(new GalleryPresenter(swingGalleryDisplay, images)));

        swingGalleryDisplay.setListener(desktop.viewer(imagePresenter));
        desktop.generateUi(imageDisplay, swingGalleryDisplay).setVisible(true);
    }

    private static byte[] readImage(String id) {
        try {
            return Files.readAllBytes(new File(root, id).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
