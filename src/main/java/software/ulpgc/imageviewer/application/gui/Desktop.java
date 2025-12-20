package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.control.Command;
import software.ulpgc.imageviewer.architecture.presenter.GalleryPresenter;
import software.ulpgc.imageviewer.architecture.presenter.ImagePresenter;
import software.ulpgc.imageviewer.architecture.ui.GalleryDisplay;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.awt.BorderLayout.*;
import static java.awt.FlowLayout.CENTER;
import static java.awt.Image.SCALE_SMOOTH;

public class Desktop extends JFrame {
    private final Map<String, Command> commands;
    private final JPanel cards = new JPanel(new CardLayout());

    public static Desktop create(SwingImageDisplay imageDisplay, SwingGalleryDisplay galleryDisplay) throws IOException {
        return new Desktop(imageDisplay, galleryDisplay);
    }

    private Desktop(SwingImageDisplay imageDisplay, SwingGalleryDisplay galleryDisplay) throws HeadlessException {
        this.commands = new HashMap<>();
        this.setTitle("Image Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());

        cards.add(imageDisplay, "viewer");
        cards.add(galleryDisplay, "gallery");

        this.setLocationRelativeTo(null);
        this.getContentPane().add(cards, BorderLayout.CENTER);
        this.createToolbar();
    }

    private void createToolbar() {
        this.getContentPane().add(toolbar(), SOUTH);
    }

    private JPanel toolbar() {
        JPanel panel = new JPanel(new FlowLayout(CENTER));
        panel.add(buttonWith("prev.png","prev"));
        panel.add(homeButton());
        panel.add(buttonWith("next.png","next"));
        return panel;
    }

    public ActionListener viewer(ImagePresenter presenter) {
        return e -> {
            software.ulpgc.imageviewer.architecture.model.Image image = (software.ulpgc.imageviewer.architecture.model.Image)((JButton) (e.getSource())).getClientProperty("image");
            presenter.show(image);
            showCard("viewer");
        };
    }

    private JButton homeButton() {
        JButton button = buttonWith("home.png");
        button.addActionListener(_ -> {showCard("gallery"); commands.get("gallery").execute();});
        return button;
    }

    private void showCard(String name) {
        CardLayout layout = (CardLayout) cards.getLayout();
        layout.show(cards, name);
    }

    private JButton buttonWith(String resourceName, String name) {
        JButton button = buttonWith(resourceName);
        button.addActionListener(_ -> commands.get(name).execute());
        return button;
    }

    public Desktop put(String name, Command command) {
        commands.put(name, command);
        return this;
    }

    private static JButton buttonWith(String resource) {
        JButton button = new JButton();
        button.setIcon(iconIn("/icons/normal/" + resource));
        button.setPressedIcon(iconIn("/icons/dimmed/" + resource));
        setAppearance(button);
        return button;
    }

    private static void setAppearance(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
    }

    private static ImageIcon iconIn(String resourcePath) {
        return iconIn(Desktop.class.getResource(resourcePath));
    }

    private static ImageIcon iconIn(URL resource) {
        return new ImageIcon(scaledImageIn(resource));
    }

    private static Image scaledImageIn(URL resource) {
        return scale(new ImageIcon(resource).getImage());
    }

    private static Image scale(Image image) {
        return image.getScaledInstance(16, 16, SCALE_SMOOTH);
    }
}


