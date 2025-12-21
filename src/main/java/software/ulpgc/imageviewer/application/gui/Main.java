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
import com.formdev.flatlaf.FlatLightLaf;

public class Main {

    private static final File root = new File("images");

    public static void main(String[] args) {
        FlatLightLaf.setup();

        ImageProvider imageProvider = ImageProvider.with(new FileImageStore(root).images());

        SwingImageDisplay imageDisplay = new SwingImageDisplay();
        SwingGalleryDisplay swingGalleryDisplay = new SwingGalleryDisplay();

        ImagePresenter imagePresenter = new ImagePresenter(imageDisplay);
        imagePresenter.show(imageProvider.first(Main::readImage));


        Desktop desktop = Desktop.create()
                .put("next", new NextCommand(imagePresenter))
                .put("prev", new PrevCommand(imagePresenter))
                .put("gallery", new GalleryCommand(new GalleryPresenter(swingGalleryDisplay, allImagesFrom(imageProvider))));

        swingGalleryDisplay.setListener(desktop.switchToViewer(imagePresenter));
        desktop.generateUi(imageDisplay, swingGalleryDisplay).setVisible(true);
    }

    private static Image[] allImagesFrom(ImageProvider imageProvider) {
        return imageProvider.all(Main::readImage);
    }

    private static byte[] readImage(String id) {
        try {
            return Files.readAllBytes(new File(root, id).toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
