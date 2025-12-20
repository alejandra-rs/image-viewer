package software.ulpgc.imageviewer.application.gui;

import software.ulpgc.imageviewer.architecture.control.Command;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static java.awt.BorderLayout.SOUTH;
import static java.awt.FlowLayout.CENTER;
import static java.awt.Image.SCALE_SMOOTH;

public class Desktop extends JFrame {
    private final Map<String, Command> commands;

    public static Desktop create(SwingImageDisplay imageDisplay) throws IOException {
        return new Desktop(imageDisplay);
    }

    private Desktop(SwingImageDisplay imageDisplay) throws HeadlessException {
        this.commands = new HashMap<>();
        this.setTitle("Image Viewer");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(800, 600);
        this.setLayout(new BorderLayout());
        this.createToolbar();
        this.setLocationRelativeTo(null);
        this.getContentPane().add(imageDisplay);

    }

    private void createToolbar() {
        this.getContentPane().add(toolbar(), SOUTH);
    }

    private JPanel toolbar() {
        JPanel panel = new JPanel(new FlowLayout(CENTER));
        panel.add(buttonWith("/prev.png","prev"));
        panel.add(buttonWith("/next.png","next"));
        return panel;
    }

    private JButton buttonWith(String resourcePath, String name) {
        JButton button = buttonWith(Desktop.class.getResource(resourcePath));
        button.addActionListener(_ -> commands.get(name).execute());
        return button;
    }

    public Desktop put(String name, Command command) {
        commands.put(name, command);
        return this;
    }

    private static JButton buttonWith(URL resource) {
        return buttonWith(new ImageIcon(resource));
    }

    private static JButton buttonWith(ImageIcon imageIcon) {
        return buttonWith(imageIcon.getImage().getScaledInstance(20, 20, SCALE_SMOOTH));
    }

    private static JButton buttonWith(Image image) {
        return new JButton(new ImageIcon(image));
    }
}


