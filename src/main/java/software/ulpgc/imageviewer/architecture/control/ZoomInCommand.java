package software.ulpgc.imageviewer.architecture.control;

import software.ulpgc.imageviewer.architecture.presenter.ImagePresenter;

public class ZoomInCommand implements Command {
    private final ImagePresenter presenter;

    public ZoomInCommand(ImagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        presenter.zoom(1.25);
    }
}
