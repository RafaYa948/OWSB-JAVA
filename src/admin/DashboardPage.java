package admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicButtonUI;

public class DashboardPage extends UIBase {
    
    public DashboardPage() {
        super("Admin Dashboard");
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel navPanel = createSidebar();
        root.add(navPanel, BorderLayout.WEST);

        JPanel topContainer = createTopBar();
        root.add(topContainer, BorderLayout.NORTH);

        JPanel content = createContentPanel();
        root.add(content, BorderLayout.CENTER);

        setContentPane(root);
    }

    private JPanel createSidebar() {
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setPreferredSize(new Dimension(200, APP_WINDOW_HEIGHT));
        navPanel.setBackground(Color.WHITE);
        navPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel logoLabel = new JLabel(new ImageIcon("logo.png"), SwingConstants.CENTER);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        navPanel.add(logoPanel, BorderLayout.NORTH);
        
        JPanel menuPanel = new JPanel();
        menuPanel.setBackground(Color.WHITE);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBackground(new Color(240, 240, 240));
        dashboardPanel.setMaximumSize(new Dimension(200, 50));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        dashboardPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel dashLabel = new JLabel("Dashboard");
        dashLabel.setFont(new Font("Serif", Font.BOLD, 16));
        dashboardPanel.add(dashLabel, BorderLayout.CENTER);
        
        menuPanel.add(dashboardPanel);
        
        dashboardPanel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                dashboardPanel.setBackground(new Color(230, 230, 230));
            }
            
            public void mouseExited(MouseEvent evt) {
                dashboardPanel.setBackground(new Color(240, 240, 240));
            }
        });
        
        menuPanel.add(Box.createVerticalGlue());
        navPanel.add(menuPanel, BorderLayout.CENTER);
        
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBackground(new Color(100, 100, 100));
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        final JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(120, 120, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutBtn.setPreferredSize(new Dimension(150, 35));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setContentAreaFilled(true);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        logoutBtn.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = c.getWidth();
                int height = c.getHeight();
                
                g2.setColor(logoutBtn.getBackground());
                g2.fillRoundRect(0, 0, width, height, 8, 8);
                
                super.paint(g2, c);
                g2.dispose();
            }
        });
        
        logoutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        logoutPanel.add(logoutBtn);
        navPanel.add(logoutPanel, BorderLayout.SOUTH);
        
        return navPanel;
    }

    private JPanel createTopBar() {
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.WHITE);
        
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setBackground(new Color(180, 180, 180));
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));
        
        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(bell);
        
        JLabel userLabel = new JLabel("Username user ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);
        
        topContainer.add(userPanel, BorderLayout.NORTH);
        
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(11, 61, 145));
        
        JLabel subtitle = new JLabel("Inventory Overview");
        subtitle.setFont(new Font("Serif", Font.PLAIN, 18));
        subtitle.setHorizontalAlignment(SwingConstants.RIGHT);
        
        headerPanel.add(title, BorderLayout.WEST);
        headerPanel.add(subtitle, BorderLayout.EAST);
        
        topContainer.add(headerPanel, BorderLayout.SOUTH);
        
        return topContainer;
    }
    private JPanel createContentPanel() {
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(Color.WHITE);
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        String[] topRowCards = {
            "Manage<br>Users",
            "Manage<br>Items",
            "View<br>Purchase<br>Requisitions",
            "View<br>Purchase<br>Orders"
        };
        
        for (int i = 0; i < topRowCards.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            content.add(createCard(topRowCards[i]), gbc);
        }
        
        String[] bottomRowCards = {
            "View<br>Stock<br>Reports",
            "View<br>Financial<br>Reports",
            "View<br>System<br>Logs"
        };
        
        JPanel bottomRow = new JPanel();
        bottomRow.setBackground(Color.WHITE);
        
        JPanel card1 = createCard(bottomRowCards[0]);
        JPanel card2 = createCard(bottomRowCards[1]);
        JPanel card3 = createCard(bottomRowCards[2]);
        
        bottomRow.setLayout(new BoxLayout(bottomRow, BoxLayout.X_AXIS));
        
        bottomRow.add(Box.createHorizontalGlue());
        bottomRow.add(card1);
        bottomRow.add(Box.createRigidArea(new Dimension(20, 0)));
        bottomRow.add(card2);
        bottomRow.add(Box.createRigidArea(new Dimension(20, 0)));
        bottomRow.add(card3);
        bottomRow.add(Box.createHorizontalGlue());
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 4;
        content.add(bottomRow, gbc);
        
        contentWrapper.add(content, BorderLayout.CENTER);
        return contentWrapper;
    }
    
    private JPanel createCard(final String text) {
        final JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(235, 245, 255));
        card.setBorder(new LineBorder(new Color(11, 61, 145), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel label = new JLabel("<html><div style='text-align:center;'>" + text + "</div></html>");
        label.setFont(new Font("Serif", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        card.add(label, BorderLayout.CENTER);
        
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                card.setBackground(new Color(220, 235, 250));
            }
            
            public void mouseExited(MouseEvent evt) {
                card.setBackground(new Color(235, 245, 255));
            }
        });
        
        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new DashboardPage();
            }
        });
    }
}