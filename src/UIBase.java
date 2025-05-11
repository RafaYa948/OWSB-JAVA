import java.awt.*;
import javax.swing.*;

public abstract class UIBase extends JFrame {
    protected Color primaryColor   = new Color(11, 61, 145);
    protected Color panelColor     = new Color(227, 242, 253);
    protected Font  headerFont     = new Font("Serif", Font.BOLD, 24);
    protected Font  subtitleFont   = new Font("Serif", Font.PLAIN, 14);
    protected Font  inputFont      = new Font("SansSerif", Font.ITALIC, 14);
    protected Font  buttonFont     = new Font("SansSerif", Font.BOLD, 14);

    public UIBase(String title) {
        super(title);
        setSize(400, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }

    protected abstract void initUI();
}
