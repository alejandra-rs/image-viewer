package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.model.Canvas;
import software.ulpgc.imageviewer.architecture.ui.ImageDisplay;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SwingImageDisplay extends JPanel implements ImageDisplay {

    private Shift shift;
    private Released released;
    private Paint[] paints;

    public SwingImageDisplay() {
        MouseAdapter mouseAdapter = new MouseAdapter();
        this.addMouseListener(mouseAdapter);
        this.addMouseMotionListener(mouseAdapter);
    }

    @Override
    public int width() {
        return this.getWidth();
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

    private void paintBackground(Graphics g) {
        g.setColor(Color.GRAY);
        g.fillRect(0,0,this.getWidth(), this.getHeight());
    }

    private void paintForeground(Graphics g) {
        Arrays.stream(paints).forEach(p -> drawPaint(p, g));
    }

    private void drawPaint(Paint paint, Graphics g) {
        BufferedImage bitmap = toBufferedImage(paint.bitmap());
        Canvas canvas = fitToWindow(bitmap);
        g.drawImage(bitmap, x(canvas.width()) + paint.offset(), y(canvas.height()),
                    canvas.width(), canvas.height(), null);
    }

    private Canvas fitToWindow(BufferedImage bitmap) {
        return Canvas.ofSize(this.getWidth(), this.getHeight())
                .fit(bitmap.getWidth(), bitmap.getHeight());
    }

    private int x(int width) {
        return (this.getWidth() - width) / 2;
    }

    private int y(int height) {
        return (this.getHeight() - height) / 2;
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

    private class MouseAdapter implements MouseListener, MouseMotionListener {

        private int x;

        @Override
        public void mousePressed(MouseEvent e) {
            x =  e.getX();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            SwingImageDisplay.this.shift.offset(e.getX() - x);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            SwingImageDisplay.this.released.offset(e.getX() - x);
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

        @Override
        public void mouseMoved(MouseEvent e) {}

    }

}