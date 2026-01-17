package software.ulpgc.imageviewer.architecture.presenter;

import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay.Paint;

import java.util.List;
import java.util.stream.IntStream;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class GalleryPresenter {
    private static final int ThumbnailSize = 100;
    private static final int CellSize = 110;

    private final GalleryDisplay display;
    private final List<Image> images;
    private final SelectionListener listener;
    private int scrollY = 0;

    public GalleryPresenter(GalleryDisplay display, List<Image> images, SelectionListener listener) {
        this.display = display;
        this.images = images;
        this.listener = listener;
        this.display.on(this::onScroll);
        this.display.on(this::refresh);
        this.display.on(this::onClick);
    }

    public interface SelectionListener { void onSelect(Image image); }

    public void show() { refresh(); }

    private void onScroll(int value) {
        scrollY = max(0, min(scrollY + value, maxScroll()));
        refresh();
    }

    private void onClick(int x, int y) {
        int index = ((y + scrollY) / CellSize) * nColumns() + ((x - xOffset()) / CellSize);
        if (isValid(index)) listener.onSelect(images.get(index));
    }

    private void refresh() {
        if (display.width() == 0) return;
        display.paint(visiblePaints());
    }

    private List<Paint> visiblePaints() {
        return IntStream.rangeClosed(startIndex(), endIndex())
                .mapToObj(this::createPaint)
                .toList();
    }

    private Paint createPaint(int i) {
        return new Paint(
                images.get(i)::bitmap,
                xOffset() + (i % nColumns()) * CellSize + (CellSize - ThumbnailSize) / 2,
                ((i / nColumns()) * CellSize) - scrollY,
                ThumbnailSize, ThumbnailSize, i
        );
    }

    private int maxScroll() {
        int cols = nColumns();
        int rows = (int) Math.ceil((double) images.size() / cols);
        return max(0, (rows * CellSize) - display.height());
    }

    private int startIndex() { return (scrollY / CellSize) * nColumns(); }

    private int endIndex() { return min(images.size() - 1, startIndex() + ((display.height() / CellSize) + 2) * nColumns()); }

    private int nColumns() { return max(1, display.width() / CellSize); }

    private int xOffset() { return (display.width() - (nColumns() * CellSize)) / 2; }

    private boolean isValid(int i) { return i >= 0 && i < images.size(); }
}