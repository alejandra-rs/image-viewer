package software.ulpgc.imageviewer.architecture.control;

import software.ulpgc.imageviewer.architecture.presenter.GalleryPresenter;

public class GalleryCommand implements Command {

    private final GalleryPresenter presenter;

    public GalleryCommand(GalleryPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        presenter.showAll();
    }
}
