package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import software.ulpgc.imageviewer.architecture.model.Canvas;

import static java.awt.RenderingHints.*;
import static java.util.Collections.synchronizedSet;

public class SwingGalleryDisplay extends JPanel implements GalleryDisplay {

    private final Map<Integer, BufferedImage> thumbnails = new HashMap<>();
    private final ExecutorService loaderService = Executors.newFixedThreadPool(4);
    private final Set<Integer> pending = synchronizedSet(new HashSet<>());

    private List<Paint> paints = new ArrayList<>();
    private Scroll scrollListener;
    private Click clickListener;
    private Resize resizeListener;

    public SwingGalleryDisplay() {
        MouseAdapter mouseAdapter = new MouseAdapter();
        this.addMouseListener(mouseAdapter);
        this.addComponentListener(mouseAdapter);
        this.addMouseWheelListener(mouseAdapter);
    }

    @Override
    public void paint(List<Paint> paints) {
        this.paints = paints;
        this.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paints.forEach(paint -> render(g, paint));
    }

    private void render(Graphics g, Paint paint) {
        if (thumbnails.containsKey(paint.index())) drawImage(g, thumbnails.get(paint.index()), paint);
        else drawEmpty(g, paint);
    }

    private void drawEmpty(Graphics g, Paint paint) {
        g.setColor(Color.GRAY);
        g.fillRect(paint.x(), paint.y(), paint.width(), paint.height());
        submitLoad(paint);
    }

    private void submitLoad(Paint paint) {
        if (pending.add(paint.index())) loaderService.submit(new LoadTask(paint));
    }

    private void drawImage(Graphics g, BufferedImage image, Paint paint) {
        int xOffset = (paint.width() - image.getWidth()) / 2;
        int yOffset = (paint.height() - image.getHeight()) / 2;
        g.drawImage(image, paint.x() + xOffset, paint.y() + yOffset, null);
    }

    @Override
    public void on(Scroll scroll) {
        this.scrollListener = scroll;
    }

    @Override
    public void on(Click click) {
        this.clickListener = click;
    }

    @Override
    public void on(Resize resize) {
        this.resizeListener = resize;
    }

    @Override
    public int width() {
        return getWidth();
    }

    @Override
    public int height() {
        return getHeight();
    }

    private class MouseAdapter implements MouseListener, MouseWheelListener, ComponentListener {

        @Override
        public void mousePressed(MouseEvent e) {
            clickListener.at(e.getX(), e.getY());
        }

        @Override
        public void componentResized(ComponentEvent e) {
            resizeListener.resized();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            scrollListener.offset(e.getWheelRotation() * 50);
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentShown(ComponentEvent e) {}

        @Override
        public void componentHidden(ComponentEvent e) {}
    }

    private class LoadTask implements Runnable {

        private final Paint paint;

        private LoadTask(Paint paint) {
            this.paint = paint;
        }

        @Override
        public void run() {
            try {
                processAndCache();
            } catch (Exception e) {
                pending.remove(paint.index());
            }
        }

        private void processAndCache() throws IOException {
            BufferedImage scaled = scale(ImageIO.read(new ByteArrayInputStream(paint.loader().get())));
            SwingUtilities.invokeLater(() -> updateCache(scaled));
        }

        private BufferedImage scale(BufferedImage raw) {
            Canvas fit = new Canvas(paint.width(), paint.height()).fit(raw.getWidth(), raw.getHeight());
            return scale(raw, fit.width(), fit.height());
        }

        private static BufferedImage scale(BufferedImage src, int w, int h) {
            BufferedImage res = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = res.createGraphics();
            g.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(src, 0, 0, w, h, null);
            g.dispose();
            return res;
        }

        private void updateCache(BufferedImage img) {
            thumbnails.put(paint.index(), img);
            pending.remove(paint.index());
            repaint(paint.x(), paint.y(), paint.width(), paint.height());
        }
    }
}