package software.ulpgc.imageviewer.architecture.control;

import software.ulpgc.imageviewer.architecture.presenter.ImagePresenter;

public class ZoomOutCommand implements Command {
    private final ImagePresenter presenter;

    public ZoomOutCommand(ImagePresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void execute() {
        presenter.zoom(0.75);
    }
}
