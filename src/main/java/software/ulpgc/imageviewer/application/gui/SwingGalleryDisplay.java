package software.ulpgc.imageviewer.application.gui;

import net.miginfocom.swing.MigLayout;
import software.ulpgc.imageviewer.architecture.model.Canvas;
import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class SwingGalleryDisplay extends JPanel implements GalleryDisplay {

    private final JPanel imageGrid;
    private ActionListener listener;

    public SwingGalleryDisplay() {
        this.setLayout(new BorderLayout());
        imageGrid = new JPanel(new WrapLayout(FlowLayout.CENTER, 10, 10));

        JScrollPane scrollPane = new JScrollPane(imageGrid);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        scrollPane.getViewport().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                imageGrid.revalidate();
            } });

        this.add(scrollPane);
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void paint(Image... images) {
        imageGrid.removeAll();
        Arrays.stream(images).forEach(i -> imageGrid.add(createThumbnailButton(i)));
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

        button.setPreferredSize(new Dimension(100, 100));
        button.setMinimumSize(new Dimension(100, 100));
        button.setMaximumSize(new Dimension(100, 100));

        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        return button;
    }


    private java.awt.Image scale(byte[] bitmap) {
        return scale(new ImageIcon(bitmap).getImage());
    }

    private java.awt.Image scale(java.awt.Image image) {
        Canvas canvas = new Canvas(100, 100).fit(image.getWidth(null), image.getHeight(null));
        return image.getScaledInstance(canvas.width(), canvas.height(), java.awt.Image.SCALE_SMOOTH);
    }

    public static class WrapLayout extends FlowLayout {
        public WrapLayout() {
            super();
        }

        public WrapLayout(int align) {
            super(align);
        }

        public WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension minimum = layoutSize(target, false);
            minimum.width -= (getHgap() + 1);
            return minimum;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                // Use parent width when inside a JScrollPane
                Container container = target;
                if (container.getParent() instanceof JViewport) {
                    container = container.getParent();
                }

                int targetWidth = container.getSize().width;

                if (targetWidth == 0) {
                    targetWidth = Integer.MAX_VALUE;
                }

                int hgap = getHgap();
                int vgap = getVgap();
                Insets insets = target.getInsets();
                int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

                Dimension dim = new Dimension(0, 0);
                int rowWidth = 0;
                int rowHeight = 0;

                int nmembers = target.getComponentCount();

                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);

                    if (m.isVisible()) {
                        Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                        if (rowWidth + d.width > maxWidth) {
                            dim.width = Math.max(dim.width, rowWidth);
                            dim.height += rowHeight + vgap;
                            rowWidth = 0;
                            rowHeight = 0;
                        }

                        rowWidth += d.width + hgap;
                        rowHeight = Math.max(rowHeight, d.height);
                    }
                }

                dim.width = Math.max(dim.width, rowWidth);
                dim.height += rowHeight;

                dim.width += insets.left + insets.right + hgap * 2;
                dim.height += insets.top + insets.bottom + vgap * 2;

                return dim;
            }
        }
    }
}