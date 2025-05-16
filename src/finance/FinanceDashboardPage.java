package finance;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import java.awt.*;
import admin.UIBase;
import finance.FinanceDashboardPage;
import models.User;
public class FinanceDashboardPage extends UIBase {

    private final User currentUser;

    public FinanceDashboardPage(User user) {
        super("Finance Dashboard");
        this.currentUser = user;
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel navPanel = createSidebar();
        root.add(navPanel, BorderLayout.WEST);

        JPanel topPanel = createTopBar();
        root.add(topPanel, BorderLayout.NORTH);

        JPanel content = createGridContent();
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
        logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel logo = new JLabel("Finance Dept", SwingConstants.CENTER);
        logo.setFont(new Font("Serif", Font.BOLD, 16));
        logo.setForeground(primaryColor);
        logoPanel.add(logo, BorderLayout.CENTER);

        navPanel.add(logoPanel, BorderLayout.NORTH);

        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoutPanel.setBackground(Color.WHITE);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(120, 120, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutBtn.setPreferredSize(new Dimension(120, 35));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                FinanceDashboardPage.this,
                "Are you sure you want to log out?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (response == JOptionPane.YES_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        logoutPanel.add(logoutBtn);
        navPanel.add(logoutPanel, BorderLayout.SOUTH);

        return navPanel;
    }

    private JPanel createTopBar() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setBackground(new Color(180, 180, 180));
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));

        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userPanel.add(bell);

        String displayName = (currentUser != null && currentUser.getUsername() != null && !currentUser.getUsername().isEmpty())
        ? currentUser.getUsername()
        : "User";
JLabel userLabel = new JLabel(displayName + " ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);
        userLabel.addMouseListener(new MouseAdapter() {
    @Override
    public void mouseClicked(MouseEvent e) {
        dispose();
        new admin.MyProfilePage(currentUser).setVisible(true);
    }
});

        topPanel.add(userPanel, BorderLayout.NORTH);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel title = new JLabel("Finance Dashboard");
        title.setFont(headerFont);
        title.setForeground(primaryColor);
        headerPanel.add(title);

        topPanel.add(headerPanel, BorderLayout.SOUTH);

        return topPanel;
    }

    private JPanel createGridContent() {
        JPanel contentPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(createCard("Approve/\nModify\nPurchase Orders", () -> {
    dispose();
    new finance.FinancePurchaseOrdersPage(currentUser).setVisible(true);
}));

        contentPanel.add(createCard("Process\nPayments", () -> {
        dispose();
        new finance.FinanceProcessPaymentsPage(currentUser).setVisible(true);
})); 
        contentPanel.add(createCard("Generate\nFinancial\nReports", () -> {
    dispose();
    new finance.FinanceGenerateReportsPage(currentUser).setVisible(true);
}));

        contentPanel.add(createCard("View\nPurchase\nRequisitions", () -> {
            dispose();
            new finance.FinanceViewPurchaseRequisitionsPage(currentUser).setVisible(true);
        }));

        contentPanel.add(createCard("View\nPurchase\nOrders", () -> {
            dispose();
            new FinanceViewPurchaseOrdersPage(currentUser).setVisible(true);
        }));

        contentPanel.add(createCard("View\nSystem\nLogs", () -> {
    dispose();
    new finance.FinanceSystemLogsPage(currentUser).setVisible(true);
}));

        return contentPanel;
    }

    private JPanel createCard(String labelText, Runnable onClick) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(235, 245, 255));
        card.setBorder(BorderFactory.createLineBorder(primaryColor));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel("<html><div style='text-align:center;'>" + labelText.replace("\n", "<br>") + "</div></html>");
        label.setFont(new Font("Serif", Font.BOLD, 16));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        card.add(label, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(220, 235, 250));
            }

            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(235, 245, 255));
            }

            public void mouseClicked(MouseEvent e) {
                
                onClick.run();
                
            }
        });

        return card;
    }
}
