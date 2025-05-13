package admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border; 

public abstract class UIBase extends JFrame {
    protected Color primaryColor   = new Color(11, 61, 145);
    protected Color panelColor     = new Color(227, 242, 253);
    protected Color inputBorderColor = Color.GRAY;
    protected Color placeholderColor = Color.GRAY;

    protected Font  baseHeaderFont     = new Font("Serif", Font.BOLD, 24);
    protected Font  baseSubtitleFont   = new Font("Serif", Font.PLAIN, 14);
    protected Font  baseInputFont      = new Font("SansSerif", Font.PLAIN, 14); 
    protected Font  baseButtonFont     = new Font("SansSerif", Font.BOLD, 14);

    protected Font  headerFont         = baseHeaderFont.deriveFont(Font.BOLD, 32f);
    protected Font  subtitleFont       = baseSubtitleFont.deriveFont(18f);
    protected Font  inputFont          = baseInputFont.deriveFont(16f); 
    protected Font  buttonTextFont     = baseButtonFont.deriveFont(Font.BOLD, 16f);
    protected Font  iconFont           = baseHeaderFont.deriveFont(Font.BOLD, 80f); 
    protected Font  smallLinkFont      = baseSubtitleFont.deriveFont(14f);


    protected static final Border INPUT_FIELD_BORDER = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.GRAY, 1), 
        BorderFactory.createEmptyBorder(5, 8, 5, 8) 
    );
    protected Border formPanelBorder = BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(primaryColor.darker(), 1),
        BorderFactory.createEmptyBorder(20, 40, 20, 40)
    );

    protected static final int APP_WINDOW_WIDTH = 960;
    protected static final int APP_WINDOW_HEIGHT = 540;

    protected WindowManager windowManager;
    protected String frameTitle;

    public UIBase(String title) {
        super(title);
        this.frameTitle = title;
        this.windowManager = new WindowManager(APP_WINDOW_WIDTH, APP_WINDOW_HEIGHT, false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI(); 

        this.windowManager.initWindow(this, this.frameTitle);
    }

    protected abstract void initUI();
}
