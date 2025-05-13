package admin;

import java.awt.*;
import javax.swing.*;

public class WindowManager {
    private final int width;
    private final int height;
    private final boolean resizable;
    private final double scale;

    private static final double REFERENCE_WIDTH = 960.0;
    private static final double REFERENCE_HEIGHT = 540.0;

    public WindowManager() {
        this( (int) REFERENCE_WIDTH, (int) REFERENCE_HEIGHT, false);
    }

    public WindowManager(int width, int height, boolean resizable) {
        this.width = width;
        this.height = height;
        this.resizable = resizable;
        this.scale = Math.min(width / REFERENCE_WIDTH, height / REFERENCE_HEIGHT);
    }

    public void initWindow(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setSize(this.width, this.height);
        frame.setResizable(this.resizable);
        frame.setLocationRelativeTo(null);
        applyScaling(frame.getContentPane());
        frame.setVisible(true);
    }

    private void applyScaling(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton button) { 
                scaleButton(button);
            } else if (c instanceof JPanel panel) { 
                applyScaling(panel);
            } else if (c instanceof Container cont) { 
                applyScaling(cont);
            }
        }
    }

    public void scaleButton(JButton b) {
        Font currentFont = b.getFont();
        if (currentFont == null && b.getParent() != null) { 
            currentFont = b.getParent().getFont();
        }
        if (currentFont == null) { 
            currentFont = new Font("SansSerif", Font.PLAIN, 14);
        }

        Font scaledFont = currentFont.deriveFont((float)(currentFont.getSize() * this.scale * 1.2));
        b.setFont(scaledFont);

        int targetWidth = 240; 
        int targetHeight = 45; 

        int bw = (int)(targetWidth * this.scale);
        int bh = (int)(targetHeight * this.scale);
        b.setPreferredSize(new Dimension(bw, bh));
    }
}
