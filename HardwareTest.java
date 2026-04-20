import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class HardwareTest extends JPanel {
    public HardwareTest(JFrame parentFrame, JPanel mainMenu) {
        // panel setup
        setLayout(new GridLayout(5, 1, 10, 10));
        setBackground(new Color(20, 20, 20));

        JButton btnScreen = new JButton("📺 Screen Color Test");
        JButton btnMouse = new JButton("🖱️ Mouse Buttons Test");
        JButton btnKey = new JButton("⌨️ Keyboard Keys Test");
        JButton btnSound = new JButton("🔊 Speaker Beep Test");
        JButton btnBack = new JButton("⬅️ Back to Main Menu");

        // button styling
        styleButton(btnScreen);
        styleButton(btnMouse);
        styleButton(btnKey);
        styleButton(btnSound);
        styleButton(btnBack, Color.RED);

        // screen test
        btnScreen.addActionListener(e -> runScreenTest());

        // mouse test
        btnMouse.addActionListener(e -> runMouseTest());

        // keyboard test
        btnKey.addActionListener(e -> runKeyboardTest());

        // voice test (beep sound)
        btnSound.addActionListener(e -> {
            Toolkit.getDefaultToolkit().beep(); // simple beep sound
            JOptionPane.showMessageDialog(this, "Did you hear the beep? If yes, speakers are OK!");
        });

        // return to main menu
        btnBack.addActionListener(e -> {
            parentFrame.remove(this);
            parentFrame.add(mainMenu);
            parentFrame.revalidate();
            parentFrame.repaint();
        });

        add(btnScreen);
        add(btnMouse);
        add(btnKey);
        add(btnSound);
        add(btnBack);
    }

    private void runMouseTest() {
        JFrame f = new JFrame("Click Test");
        f.setSize(300, 200);
        f.setLocationRelativeTo(null);
        JLabel label = new JLabel("Click Right / Left / Middle", SwingConstants.CENTER);
        f.add(label);
        f.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                String click = (e.getButton() == 1) ? "LEFT" : (e.getButton() == 3) ? "RIGHT" : "MIDDLE";
                label.setText("Detected: " + click);
            }
        });
        f.setVisible(true);
    }

    // keyboard test function (opens a new window)
    private void runKeyboardTest() {
        JFrame f = new JFrame("Key Test");
        f.setSize(300, 200);
        f.setLocationRelativeTo(null);
        JLabel label = new JLabel("Press any key on keyboard", SwingConstants.CENTER);
        f.add(label);
        f.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                label.setText("You pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
            }
        });
        f.setVisible(true);
    }

    private void runScreenTest() {
        JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel p = new JPanel();
        p.setBackground(Color.RED);
        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                f.dispose();
            }
        });
        f.add(p);
        f.setVisible(true);
    }

    private void styleButton(JButton b) {
        styleButton(b, new Color(0, 255, 100));
    }

    private void styleButton(JButton b, Color edge) {
        b.setBackground(new Color(40, 40, 40));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(edge));
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 14));
    }
}