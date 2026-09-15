package main;

import gui.MainFrame;
import gui.UITheme;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        UITheme.applyGlobalStyle();
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
