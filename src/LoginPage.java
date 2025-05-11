import java.awt.*;
import javax.swing.*;

public class LoginPage extends UIBase {
    public LoginPage() {
        super("Automated Purchase Order Management System");
    }

    @Override
    protected void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        JLabel title = new JLabel("Automated Purchase Order Management System", SwingConstants.CENTER);
        title.setFont(headerFont);
        title.setForeground(primaryColor);
        JLabel subtitle = new JLabel("Login to your Account to get started or manage your inventory",
                                     SwingConstants.CENTER);
        subtitle.setFont(subtitleFont);
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        JPanel form = new JPanel();
        form.setBackground(panelColor);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel icon = new JLabel(new ImageIcon("src/resources/user_icon.png"));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        userField.setFont(inputFont);
        userField.setText("Username");

        JPasswordField passField = new JPasswordField();
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        passField.setFont(inputFont);
        passField.setText("Password");

        JButton loginBtn = new JButton("Login");
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setFont(buttonFont);
        loginBtn.setBackground(primaryColor);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFocusPainted(false);
        loginBtn.setPreferredSize(new Dimension(0, 40));

        form.add(icon);
        form.add(Box.createRigidArea(new Dimension(0, 20)));
        form.add(userField);
        form.add(Box.createRigidArea(new Dimension(0, 10)));
        form.add(passField);
        form.add(Box.createRigidArea(new Dimension(0, 20)));
        form.add(loginBtn);

        JPanel links = new JPanel();
        links.setBackground(Color.WHITE);
        links.setLayout(new BoxLayout(links, BoxLayout.Y_AXIS));
        JLabel forgot = new JLabel("Forgot Username or Password?");
        forgot.setFont(subtitleFont);
        forgot.setForeground(primaryColor);
        forgot.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel create = new JLabel("Create an Account");
        create.setFont(subtitleFont);
        create.setForeground(primaryColor);
        create.setAlignmentX(Component.CENTER_ALIGNMENT);
        links.add(forgot);
        links.add(create);

        root.add(header, BorderLayout.NORTH);
        root.add(form,   BorderLayout.CENTER);
        root.add(links,  BorderLayout.SOUTH);

        setContentPane(root);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPage::new);
    }
}
