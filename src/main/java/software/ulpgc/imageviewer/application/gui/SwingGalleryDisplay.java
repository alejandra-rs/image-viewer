package software.ulpgc.imageviewer.application.gui;

import org.jdesktop.swingx.JXList;
import software.ulpgc.imageviewer.architecture.model.Canvas;
import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class SwingGalleryDisplay extends JPanel implements GalleryDisplay {

    private final DefaultListModel<Image> model = new DefaultListModel<>();
    private final JXList list;
    private ActionListener listener;

    private final Map<Image, ImageIcon> icons = new HashMap<>();

    public SwingGalleryDisplay() {
        this.setLayout(new BorderLayout());

        list = new JXList(model);
        configureListHorizontalWrap();
        configureListBehavior();

        this.add(scrollPaneWithImages());
    }

    @Override
    public void paint(Image... images) {
        model.clear();
        Arrays.stream(images).forEach(model::addElement);
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    private void configureListHorizontalWrap() {
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);
   }

    private void configureListBehavior() {
        list.setCellRenderer((_, cell, _, _, _) -> render((Image) cell));
        list.addMouseListener(mouseAdapter());
    }

    private JScrollPane scrollPaneWithImages() {
        JScrollPane scrollPane = new JScrollPane(list);
        setScrollingPoliciesOn(scrollPane);
        setAdaptativeBorderOn(scrollPane);
        return scrollPane;
    }

    private static void setScrollingPoliciesOn(JScrollPane scrollPane) {
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
    }

    private void setAdaptativeBorderOn(JScrollPane scrollPane) {
        JViewport viewport = scrollPane.getViewport();
        viewport.addComponentListener(resizableBorderOn(viewport));
    }

    private ComponentAdapter resizableBorderOn(JViewport viewport) {
        return new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                list.doLayout();
                list.setBorder(BorderFactory.createEmptyBorder(0, padding(viewport), 0, padding(viewport)));
                list.revalidate(); list.repaint();
            }
        };
    }

    private int padding(JViewport viewport) {
        return viewport.getExtentSize().width % list.getCellBounds(0, 0).width / 2;
    }

    private JPanel render(Image image) {
        return panelWithBorderFor(galleryImageWith(image));
    }

    private MouseAdapter mouseAdapter() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Image img = model.get(list.locationToIndex(e.getPoint()));
                listener.actionPerformed(new ActionEvent(img, ActionEvent.ACTION_PERFORMED, "imageClick"));
            }
        };
    }

    private JPanel panelWithBorderFor(Component component) {
        JPanel border = new JPanel();
        border.add(component);
        border.setOpaque(false);
        border.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return border;
    }

    private JLabel galleryImageWith(Image img) {
        ImageIcon icon = icons.computeIfAbsent(img, i -> new ImageIcon(scale(i.bitmap())));
        return labelWith(icon);
    }

    private JLabel labelWith(ImageIcon icon) {
        JLabel label = new JLabel(icon);
        label.setPreferredSize(new Dimension(100, 100));
        label.setOpaque(false);
        return label;
    }

    private java.awt.Image scale(byte[] bitmap) {
        return scale(new ImageIcon(bitmap).getImage());
    }

    private java.awt.Image scale(java.awt.Image image) {
        Canvas canvas = new Canvas(100, 100).fit(image.getWidth(null), image.getHeight(null));
        return image.getScaledInstance(canvas.width(), canvas.height(), java.awt.Image.SCALE_SMOOTH);
    }
}