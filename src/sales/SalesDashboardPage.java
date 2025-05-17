package sales;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;
import models.User;
// Import the correct UIBase for the sales package
import sales.UIBase;
import admin.MyProfilePage; // Assuming MyProfilePage remains in the admin package

public class SalesDashboardPage extends UIBase {

    private User currentUser;

    public SalesDashboardPage() {
        super("Sales Dashboard");
    }

    public SalesDashboardPage(User user) {
        super("Sales Dashboard");
        this.currentUser = user;
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
        logoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel placeholder = new JLabel("OWSB", SwingConstants.CENTER);
        placeholder.setFont(new Font("Serif", Font.BOLD, 16));
        placeholder.setForeground(new Color(11, 61, 145));
        placeholder.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        logoPanel.add(placeholder, BorderLayout.CENTER);

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
        logoutPanel.setBackground(Color.WHITE);
        logoutPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(120, 120, 120));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        logoutBtn.setPreferredSize(new Dimension(120, 35));
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(120, 120, 120), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        logoutBtn.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                    SalesDashboardPage.this,
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
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setBackground(Color.WHITE);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        userPanel.setBackground(new Color(180, 180, 180));
        userPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 20));

        JLabel bell = new JLabel("🔔");
        bell.setFont(new Font("SansSerif", Font.PLAIN, 16));
        bell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(bell);

        String displayName = (currentUser != null && currentUser.getUsername() != null && !currentUser.getUsername().isEmpty())
                ? currentUser.getUsername()
                : "Username user";

        JLabel userLabel = new JLabel(displayName + " ▾");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        userLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userPanel.add(userLabel);

        topContainer.add(userPanel, BorderLayout.NORTH);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        JLabel title = new JLabel("Sales Manager Dashboard");
        title.setFont(new Font("Serif", Font.BOLD, 28));
        title.setForeground(new Color(11, 61, 145));

        headerPanel.add(title);
        topContainer.add(headerPanel, BorderLayout.SOUTH);

        userLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToMyProfile();
            }
        });

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
                "Manage<br>Suppliers",
                "Manage<br>Items",
                "View<br>Purchase<br>Requisitions",
                "View<br>Purchase<br>Orders"
        };

        for (int i = 0; i < topRowCards.length; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            JPanel card = createCard(topRowCards[i]);

            if (i == 0) {
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        navigateToManageSuppliers();
                    }
                });
            }

            if (i == 1) {
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        navigateToManageItems();
                    }
                });
            }

            if (i == 2) {
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        navigateToPurchaseRequisitions();
                    }
                });
            }

            if (i == 3) {
                card.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        navigateToPurchaseOrders();
                    }
                });
            }

            content.add(card, gbc);
        }

        String[] bottomRowCards = {
                "Daily Sales<br>Entry",
                "Create<br>Purchase<br>Requisition",
                "View<br>System<br>Logs"
        };

        JPanel bottomRow = new JPanel();
        bottomRow.setBackground(Color.WHITE);

        JPanel card1 = createCard(bottomRowCards[0]);
        card1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToSalesDataEntry();
            }
        });
        JPanel card2 = createCard(bottomRowCards[1]);
        card2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToCreateRequisition();
            }
        });
        JPanel card3 = createCard(bottomRowCards[2]);
        card3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToSystemLogs();
            }
        });

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

    private void navigateToManageSuppliers() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            ManageSuppliersPage manageSuppliersPage = new ManageSuppliersPage(currentUser);
            manageSuppliersPage.setVisible(true);
        });
    }

    private void navigateToManageItems() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            ManageItemsPage manageItemsPage = new ManageItemsPage(currentUser);
            manageItemsPage.setVisible(true);
        });
    }

    private void navigateToPurchaseRequisitions() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            ViewPurchaseRequisitionsPage requisitionsPage = new ViewPurchaseRequisitionsPage(currentUser);
            requisitionsPage.setVisible(true);
        });
    }

    private void navigateToPurchaseOrders() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            ViewPurchaseOrdersPage ordersPage = new ViewPurchaseOrdersPage(currentUser);
            ordersPage.setVisible(true);
        });
    }

    private void navigateToSalesDataEntry() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            SalesDataEntryPage page = new SalesDataEntryPage(currentUser);
            page.setVisible(true);
        });
    }

    private void navigateToCreateRequisition() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            CreateRequisitionPage page = new CreateRequisitionPage(currentUser);
            page.setVisible(true);
        });
    }

    private void navigateToSystemLogs() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            SalesSystemLogsPage page = new SalesSystemLogsPage(currentUser);
            page.setVisible(true);
        });
    }

    private void navigateToMyProfile() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            // Assuming MyProfilePage is in the admin package as per your file structure
            MyProfilePage profilePage = new MyProfilePage(currentUser);
            profilePage.setVisible(true);
        });
    }


    private JPanel createCard(final String text) {
        final JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(235, 245, 255));
        card.setBorder(new LineBorder(new Color(11, 61, 145), 1));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel("<html><div style='text-align:center;'>" + text.replace("\n", "<br>") + "</div></html>");
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
        SwingUtilities.invokeLater(() -> new SalesDashboardPage());
    }
}