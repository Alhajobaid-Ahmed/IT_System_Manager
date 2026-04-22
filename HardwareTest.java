import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
        JButton btnPing = new JButton("🌐 Network Ping Test");
        JButton btnBack = new JButton("⬅️ Back to Main Menu");

        // button styling
        styleButton(btnScreen);
        styleButton(btnMouse);
        styleButton(btnKey);
        styleButton(btnSound);
        styleButton(btnPing);
        styleButton(btnBack, Color.RED);

        // screen test
        btnScreen.addActionListener(e -> runScreenTest());

        // mouse test
        btnMouse.addActionListener(e -> runMouseTest());

        // keyboard test
        btnKey.addActionListener(e -> runKeyboardTest());

        // voice test (beep sound)
        btnSound.addActionListener(e -> {
            new Thread(() -> {
                try {
                    playDiagnosticTone();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error playing sound: " + ex.getMessage());
                }
            }).start();
            JOptionPane.showMessageDialog(this, "Did you hear the beep? If yes, speakers are OK!");
        });

        // network test (ping)
        btnPing.addActionListener(e -> {
            new Thread(() -> {
                try {
                    // عمل Ping لسيرفر جوجل 4 مرات
                    String output = runPingTest("8.8.8.8");
                    JOptionPane.showMessageDialog(this, "Network Status:\n" + output);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ping Failed!");
                }
            }).start();
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
        add(btnPing);
        add(btnBack);
    }

    // mouse test function
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

    // ping test function
    private String runPingTest(String host) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("ping", "-n", "4", host);
        Process p = pb.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            if (line.contains("Average") || line.contains("Lost")) {
                sb.append(line.trim()).append("\n");
            }
        }
        return sb.length() > 0 ? sb.toString() : "Connection Error!";
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

    // screen test function (cycles through colors)
    private void runScreenTest() {
        JFrame f = new JFrame();
        f.setUndecorated(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        JPanel p = new JPanel();
        Color[] testColors = { Color.RED, Color.GREEN, Color.BLUE, Color.WHITE, Color.BLACK };
        final int[] colorIndex = { 0 };
        p.setBackground(testColors[colorIndex[0]]);
        p.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                colorIndex[0]++;

                if (colorIndex[0] < testColors.length) {
                    p.setBackground(testColors[colorIndex[0]]);
                } else {
                    f.dispose();
                }
            }
        });
        f.add(p);
        f.setCursor(f.getToolkit().createCustomCursor(
                new java.awt.image.BufferedImage(1, 1, java.awt.image.BufferedImage.TYPE_INT_ARGB), new Point(0, 0),
                "blank cursor"));
        f.setVisible(true);
    }

    // function to play a diagnostic beep sound
    private void playDiagnosticTone() throws Exception {
        float sampleRate = 8000f;
        int durationMs = 1000;
        int hz = 800;

        byte[] buf = new byte[1];
        javax.sound.sampled.AudioFormat af = new javax.sound.sampled.AudioFormat(sampleRate, 8, 1, true, false);
        try (javax.sound.sampled.SourceDataLine sdl = javax.sound.sampled.AudioSystem.getSourceDataLine(af)) {
            sdl.open(af);
            sdl.start();
            for (int i = 0; i < (sampleRate * durationMs / 1000); i++) {
                double angle = i / (sampleRate / hz) * 2.0 * Math.PI;
                buf[0] = (byte) (Math.sin(angle) * 127.0);
                sdl.write(buf, 0, 1);
            }
            sdl.drain();
            sdl.stop();
        }
    }

    // button styling helper
    private void styleButton(JButton b) {
        styleButton(b, new Color(0, 255, 100));
    }

    // overloaded styling for back button
    private void styleButton(JButton b, Color edge) {
        b.setBackground(new Color(40, 40, 40));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(edge));
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 14));
    }
}