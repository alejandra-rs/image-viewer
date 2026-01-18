package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.model.Canvas;
import software.ulpgc.imageviewer.architecture.ui.ImageDisplay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.lang.Math.*;

public class SwingImageDisplay extends JPanel implements ImageDisplay {

    private Shift shift;
    private Released released;
    private Paint[] paints;
    private double zoom = 1.0;
    private int panX = 0, panY = 0;

    public SwingImageDisplay() {
        MouseAdapter mouseAdapter = new MouseAdapter();
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
        this.addMouseWheelListener(mouseAdapter);
    }

    @Override
    public void paint(Paint... paints) {
        this.paints = paints;
        this.repaint();
    }

    @Override
    public void paint(Graphics g) {
        paintBackground(g);
        paintForeground(g);
    }

    @Override
    public void on(Shift shift) {
        this.shift = shift;
    }

    @Override
    public void on(Released released) {
        this.released = released;
    }

    @Override
    public void zoom(double factor) {
        zoom = max(0.25, min(factor, 5.0));
        if (this.zoom <= 1.0) {this.panX = 0; this.panY = 0; }
        repaint();
    }

    @Override
    public double zoom() {
        return zoom;
    }

    @Override
    public int width() {
        return this.getWidth();
    }

    private void paintBackground(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(0,0,this.getWidth(), this.getHeight());
    }

    private void paintForeground(Graphics g) {
        Arrays.stream(paints).forEach(p -> drawPaint(p, g));
    }

    private void drawPaint(Paint paint, Graphics g) {
        BufferedImage bitmap = toBufferedImage(paint.bitmap());
        Canvas canvas = fitToWindow(bitmap).scale(zoom);
        g.drawImage(bitmap,
                    x(canvas.width(), paint.offset()),
                    y(canvas.height()),
                    canvas.width(), canvas.height(), null);
    }

    private Canvas fitToWindow(BufferedImage bitmap) {
        return Canvas.ofSize(this.getWidth(), this.getHeight())
                .fit(bitmap.getWidth(), bitmap.getHeight());
    }

    private int x(int width, int offset) {
        return (this.getWidth() - width) / 2 + offset + panX;
    }

    private int y(int height) {
        return (this.getHeight() - height) / 2 +  panY;
    }

    private final Map<Integer, BufferedImage> images = new HashMap<>();
    private BufferedImage toBufferedImage(byte[] bitmap) {
        return images.computeIfAbsent(Arrays.hashCode(bitmap), _ -> read(bitmap));
    }

    private BufferedImage read(byte[] bitmap) {
        try (InputStream is = new ByteArrayInputStream(bitmap)) {
            return ImageIO.read(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private class MouseAdapter implements MouseListener, MouseMotionListener, MouseWheelListener {
        private int lastX, lastY, startX;

        @Override
        public void mousePressed(MouseEvent e) {
            lastX = e.getX(); lastY = e.getY(); startX = e.getX();
        }

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            zoom(zoom * (e.getWheelRotation() > 0 ? 0.75 : 1.25));
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            released.offset(e.getX() - startX);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (isWidthZoomed()) pan(e.getX() - lastX, e.getY() - lastY, e.getX());
            else slide(e.getX());
            lastX = e.getX(); lastY = e.getY();
        }

        private boolean isWidthZoomed() {
            if (paints.length == 0) return false;
            return zoomedImage().width() > getWidth();
        }

        private Canvas zoomedImage() {
            return fitToWindow(toBufferedImage(paints[0].bitmap())).scale(zoom);
        }

        private void pan(int dx, int dy, int x) {
            panX(dx); panY(dy);
            startX = x;
            repaint();
        }

        private void slide(int x) {
            shift.offset(x - startX);
            repaint();
        }

        private void panX(int dx) {
            panX = clamp(panX + dx, limit().width());
        }

        private void panY(int dy) {
            panY = clamp(panY + dy, limit().height());
        }

        private int clamp(int currentOffset, int limit) {
            return min(limit, max(-limit, currentOffset));
        }

        private Canvas limit() { return zoomedImage().offsetLimit(getWidth(), getHeight()); }

        @Override public void mouseClicked(MouseEvent e) {}
        @Override public void mouseMoved(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }

}