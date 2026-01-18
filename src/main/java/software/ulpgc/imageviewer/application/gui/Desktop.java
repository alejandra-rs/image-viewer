package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.control.Command;
import software.ulpgc.imageviewer.architecture.presenter.ImagePresenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import software.ulpgc.imageviewer.architecture.model.Image;

import static java.awt.BorderLayout.*;
import static java.awt.Image.SCALE_SMOOTH;
import static software.ulpgc.imageviewer.application.gui.Desktop.ButtonFactory.buttonWith;

public class Desktop extends JFrame {
    private final Map<String, Command> commands;
    private final JPanel cards = new JPanel(new CardLayout());

    private Desktop() {
        this.commands = new HashMap<>();
        setWindowProperties();
    }

    public static Desktop create() {
        return new Desktop();
    }

    public Desktop generateUi(SwingImageDisplay imageDisplay, SwingGalleryDisplay galleryDisplay) {
        addCards(imageDisplay, galleryDisplay);
        commands.get("gallery").execute();
        return this;
    }

    public void switchToViewer(ImagePresenter presenter, Image image) {
        presenter.show(image);
        showCard("viewer");
    }
    private void setWindowProperties() {
        this.setTitle("Image Viewer");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
    }

    private void addCards(SwingImageDisplay imageDisplay, SwingGalleryDisplay galleryDisplay) {
        cards.add(galleryDisplay, "gallery");
        cards.add(viewerWith(imageDisplay), "viewer");
        this.getContentPane().add(cards);
    }

    private JPanel viewerWith(SwingImageDisplay imageDisplay) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageDisplay);
        panel.add(toolbar(), SOUTH);
        return panel;
    }

    private JPanel toolbar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(centerButtons());
        panel.add(zoomButtons(), EAST);
        panel.add(westDummy(), WEST);
        return panel;
    }

    private JPanel centerButtons() {
        JPanel panel = new JPanel();
        panel.add(buttonWith("prev.png", _ -> commands.get("prev").execute()));
        panel.add(buttonWith("home.png", _ -> showGallery()));
        panel.add(buttonWith("next.png", _ -> commands.get("next").execute()));
        return panel;
    }

    private JPanel zoomButtons() {
        JPanel panel = new JPanel();
        panel.add(buttonWith("zoom-in.png", _ -> commands.get("zoomIn").execute()));
        panel.add(buttonWith("zoom-out.png", _ -> commands.get("zoomOut").execute()));
        return panel;
    }

    private JPanel westDummy() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(zoomButtons().getPreferredSize());
        return panel;
    }

    private void showGallery() {
        showCard("gallery");
        commands.get("gallery").execute();
    }

    private void showCard(String name) {
        CardLayout layout = (CardLayout) cards.getLayout();
        layout.show(cards, name);
    }

    public Desktop put(String name, Command command) {
        commands.put(name, command);
        return this;
    }

    public static class ButtonFactory {

        public static JButton buttonWith(String resource, ActionListener listener) {
            JButton button = setButtonWithIcon(resource);
            button.addActionListener(listener);
            return button;
        }

        private static JButton setButtonWithIcon(String resource) {
            JButton button = new JButton();
            button.setIcon(iconIn("/icons/normal/" + resource));
            button.setPressedIcon(iconIn("/icons/dimmed/" + resource));
            setAppearance(button);
            return button;
        }

        private static void setAppearance(JButton button) {
            button.setBorderPainted(false);
            button.setContentAreaFilled(false);
            button.setPreferredSize(new Dimension(48, 28));
        }

        private static ImageIcon iconIn(String resourcePath) {
            return iconIn(Desktop.class.getResource(resourcePath));
        }

        private static ImageIcon iconIn(URL resource) {
            return new ImageIcon(scaledImageIn(resource));
        }

        private static java.awt.Image scaledImageIn(URL resource) {
            return scale(new ImageIcon(resource).getImage());
        }

        private static java.awt.Image scale(java.awt.Image image) {
            return image.getScaledInstance(16, 16, SCALE_SMOOTH);
        }

    }
}
