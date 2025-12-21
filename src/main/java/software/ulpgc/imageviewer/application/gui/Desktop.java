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
import static java.awt.FlowLayout.CENTER;
import static java.awt.Image.SCALE_SMOOTH;

public class Desktop extends JFrame {
    private final Map<String, Command> commands;
    private final JPanel cards = new JPanel(new CardLayout());

    public static Desktop create() {
        return new Desktop();
    }

    private Desktop() throws HeadlessException {
        this.commands = new HashMap<>();
        this.setTitle("Image Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
    }

    public Desktop generateUi(SwingImageDisplay imageDisplay, SwingGalleryDisplay galleryDisplay) {
        cards.add(galleryDisplay, "gallery");
        cards.add(viewerWith(imageDisplay), "viewer");

        this.getContentPane().add(cards, BorderLayout.CENTER);
        commands.get("gallery").execute();

        return this;
    }

    private JPanel viewerWith(SwingImageDisplay imageDisplay) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(imageDisplay, BorderLayout.CENTER);
        panel.add(toolbar(), SOUTH);
        return panel;
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
            Image image = (Image) (e.getSource());
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

    private static java.awt.Image scaledImageIn(URL resource) {
        return scale(new ImageIcon(resource).getImage());
    }

    private static java.awt.Image scale(java.awt.Image image) {
        return image.getScaledInstance(16, 16, SCALE_SMOOTH);
    }
}


