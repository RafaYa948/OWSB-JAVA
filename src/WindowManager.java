import java.awt.*;
import javax.swing.*;

public class WindowManager {
    private final int width;
    private final int height;
    private final boolean resizable;
    private final double scale;

    public WindowManager() {
        this(1024, 650, false);
    }

    public WindowManager(int width, int height, boolean resizable) {
        this.width = width;
        this.height = height;
        this.resizable = resizable;
        this.scale = Math.min(width / 1024.0, height / 650.0);
    }

    public void initWindow(JFrame frame, String title) {
        frame.setTitle(title);
        frame.setSize(width, height);
        frame.setResizable(resizable);
        frame.setLocationRelativeTo(null);
        applyScaling(frame.getContentPane());
        frame.setVisible(true);
    }

    private void applyScaling(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton) {
                scaleButton((JButton)c);
            } else if (c instanceof Container) {
                applyScaling((Container)c);
            }
        }
    }

    public void scaleButton(JButton b) {
        Font f = b.getFont().deriveFont((float)(b.getFont().getSize() * scale * 1.2));
        b.setFont(f);
        int bw = (int)(180 * scale);
        int bh = (int)(50 * scale);
        b.setPreferredSize(new Dimension(bw, bh));
    }
}
