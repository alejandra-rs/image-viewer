package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class SwingGalleryDisplay extends JPanel implements GalleryDisplay {

    private final JPanel imageGrid;
    private ActionListener listener;

    public SwingGalleryDisplay() {
        this.setLayout(new BorderLayout());
        imageGrid = new JPanel(new GridLayout(0, 1));
        this.add(new JScrollPane(imageGrid));
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void paint(Image... images) {
        imageGrid.removeAll();
        for (Image img : images) {
            JButton thumb = createThumbnailButton(img);
            imageGrid.add(thumb);
        }
        imageGrid.revalidate();
        imageGrid.repaint();
    }

    private final Map<Image, ImageIcon> thumbnailCache = new HashMap<>();
    private JButton createThumbnailButton(Image img) {
        ImageIcon icon = thumbnailCache.computeIfAbsent(img, i ->
                new ImageIcon(scale(i.bitmap()))
        );

        JButton button = new JButton(icon);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.putClientProperty("image", img);
        button.addActionListener(listener);
        return button;
    }


    private java.awt.Image scale(byte[] bitmap) {
        return new ImageIcon(bitmap).getImage()
                .getScaledInstance(100, 100, java.awt.Image.SCALE_SMOOTH);
    }
}
