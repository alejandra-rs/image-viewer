package software.ulpgc.imageviewer.architecture.ui;

import java.util.List;
import java.util.function.Supplier;

public interface GalleryDisplay {
    void paint(List<Paint> paints);

    void on(Scroll scroll);
    void on(Click click);
    void on(Resize resize);

    int width();
    int height();

    interface Scroll {
        void offset(int value);
    }

    interface Click {
        void at(int x, int y);
    }

    interface Resize {
        void resized();
    }

    record Paint(Supplier<byte[]> loader, int x, int y, int width, int height, int index) {}
}
