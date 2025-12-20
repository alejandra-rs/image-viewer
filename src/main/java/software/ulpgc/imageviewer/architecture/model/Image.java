package software.ulpgc.imageviewer.architecture.model;

public interface Image {
    String id();
    byte[] bitmap();
    Image next();
    Image previous();
}
