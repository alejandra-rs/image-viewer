package software.ulpgc.imageviewer.application.gui;

import org.jdesktop.swingx.JXList;
import software.ulpgc.imageviewer.architecture.model.Canvas;
import software.ulpgc.imageviewer.architecture.model.Image;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class SwingGalleryDisplay extends JPanel implements GalleryDisplay {

    private final DefaultListModel<Image> model = new DefaultListModel<>();
    private final JXList list;
    private ActionListener listener;

    private final Map<Image, ImageIcon> thumbnailCache = new HashMap<>();

    public SwingGalleryDisplay() {
        this.setLayout(new BorderLayout());

        list = new JXList(model);
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        list.setVisibleRowCount(-1);

        list.setCellRenderer((_, value, _, _, _) -> {
            JButton button = createThumbnailButton((Image) value);

            JPanel wrapper = new JPanel(new GridBagLayout());
            wrapper.setOpaque(false);
            wrapper.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            wrapper.add(button);

            return wrapper;
        });

        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = list.locationToIndex(e.getPoint());
                if (index >= 0 && listener != null) {
                    Image img = model.get(index);
                    listener.actionPerformed(
                            new ActionEvent(img, ActionEvent.ACTION_PERFORMED, "thumbnailClick")
                    );
                }
            }
        });


        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);

        this.add(scrollPane);
    }

    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void paint(Image... images) {
        model.clear();
        Arrays.stream(images).forEach(model::addElement);
    }

    private JButton createThumbnailButton(Image img) {
        ImageIcon icon = thumbnailCache.computeIfAbsent(img, i ->
                new ImageIcon(scale(i.bitmap()))
        );

        JButton button = new JButton(icon);

        button.setPreferredSize(new Dimension(100, 100));
        button.setMinimumSize(new Dimension(100, 100));
        button.setMaximumSize(new Dimension(100, 100));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);

        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.putClientProperty("image", img);

        return button;
    }


    private java.awt.Image scale(byte[] bitmap) {
        return scale(new ImageIcon(bitmap).getImage());
    }

    private java.awt.Image scale(java.awt.Image image) {
        Canvas canvas = new Canvas(100, 100).fit(image.getWidth(null), image.getHeight(null));
        return image.getScaledInstance(canvas.width(), canvas.height(), java.awt.Image.SCALE_SMOOTH);
    }
}