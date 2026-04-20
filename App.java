import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import javax.swing.*;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("IT-Systemelektroniker Professional Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 800);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(new Color(10, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // main title
        JLabel title = new JLabel(" SYSTEM INVENTORY & HEALTH", SwingConstants.CENTER);
        title.setForeground(new Color(0, 255, 150));
        title.setFont(new Font("Consolas", Font.BOLD, 22));
        panel.add(title, BorderLayout.NORTH);

        // console area
        JTextArea console = new JTextArea();
        console.setBackground(new Color(20, 20, 20));
        console.setForeground(new Color(0, 255, 50));
        console.setFont(new Font("Monospaced", Font.PLAIN, 13));
        console.setEditable(false);
        console.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(console);
        panel.add(scroll, BorderLayout.CENTER);

        // buttons panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnFullScan = createStyledButton("FULL SYSTEM SCAN");
        JButton btnClear = createStyledButton("CLEAR LOG");
        JButton btnHardwareTest = createStyledButton("HARDWARE TESTS");
        btnPanel.add(btnFullScan);
        btnPanel.add(btnClear);
        btnPanel.add(btnHardwareTest);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // button actions
        btnFullScan.addActionListener(e -> {
            console.setText(">>> STARTING GLOBAL SYSTEM SCAN...\n");
            new Thread(() -> { // run scan in background to keep UI responsive
                try {
                    updateLog(console, "[+] Fetching PC Name & IP...");
                    String host = InetAddress.getLocalHost().getHostName();
                    String ip = InetAddress.getLocalHost().getHostAddress();

                    updateLog(console, "[+] Extracting BIOS Serial Number...");
                    String sn = getCmdOutput("wmic bios get serialnumber");

                    updateLog(console, "[+] Identifying Processor...");
                    String cpu = getCmdOutput("wmic cpu get name");

                    updateLog(console, "[+] Analyzing Memory (RAM)...");
                    String ram = getCmdOutput("wmic ComputerSystem get TotalPhysicalMemory");

                    updateLog(console, "[+] Scanning Storage (Hard Drives)...");
                    String disk = getCmdOutput("wmic logicaldisk get size,freespace,caption");

                    updateLog(console, "[+] Listing Connected USB/PnP Devices...");
                    String devices = getCmdOutput(
                            "wmic path Win32_PnPEntity where \"ConfigManagerErrorCode = 0\" get caption");

                    // display report in console
                    SwingUtilities.invokeLater(() -> {
                        console.append("\n================ REPORT ================\n");
                        console.append("PC NAME   : " + host + "\n");
                        console.append("LOCAL IP  : " + ip + "\n");
                        console.append("SERIAL NO : " + sn.replace("SerialNumber", "").trim() + "\n");
                        console.append("CPU       : " + cpu.replace("Name", "").trim() + "\n");

                        if (!ram.split("\n")[1].trim().isEmpty()) {
                            long ramBytes = Long.parseLong(ram.split("\n")[1].trim());
                            console.append("TOTAL RAM : " + (ramBytes / (1024 * 1024 * 1024)) + " GB\n");
                        }

                        console.append("\nSTORAGE DETAILS (Converted to GB):\n");
                        console.append(String.format("%-10s %-15s %-15s\n", "Drive", "Free Space", "Total Size"));
                        console.append("------------------------------------------\n");

                        String[] diskLines = disk.split("\n");
                        for (int i = 1; i < diskLines.length; i++) {
                            String[] parts = diskLines[i].trim().split("\\s+");
                            if (parts.length >= 3) {
                                try {
                                    String label = parts[0];
                                    long free = Long.parseLong(parts[1]) / (1024 * 1024 * 1024);
                                    long total = Long.parseLong(parts[2]) / (1024 * 1024 * 1024);

                                    console.append(
                                            String.format("%-10s %-15s %-15s\n", label, free + " GB", total + " GB"));
                                } catch (NumberFormatException | NullPointerException ex) {
                                }
                            }
                        }
                        console.append("\nCONNECTED DEVICES (Top 5):\n");
                        String[] devList = devices.split("\n");
                        for (int i = 1; i < Math.min(devList.length, 7); i++) {
                            console.append("- " + devList[i].trim() + "\n");
                        }
                        console.append("========================================\n");
                    });

                } catch (Exception ex) {
                    updateLog(console, "[!] ERROR: " + ex.getMessage());
                }
            }).start();
        });
        btnHardwareTest.addActionListener(e -> {
            HardwareTest testScreen = new HardwareTest(frame, panel);
            frame.remove(panel);
            frame.add(testScreen);
            frame.revalidate();
            frame.repaint();
        });
        btnClear.addActionListener(e -> console.setText(""));

        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void updateLog(JTextArea area, String msg) {
        SwingUtilities.invokeLater(() -> area.append(msg + "\n"));
    }

    private static String getCmdOutput(String cmd) throws Exception {
        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", cmd);
        builder.redirectErrorStream(true);
        Process p = builder.start();
        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder out = new StringBuilder();
        String l;
        while ((l = r.readLine()) != null) {
            if (!l.trim().isEmpty())
                out.append(l).append("\n");
        }
        return out.toString();
    }

    private static JButton createStyledButton(String text) {
        JButton b = new JButton(text);
        b.setFocusPainted(false);
        b.setBackground(new Color(40, 40, 40));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Arial", Font.BOLD, 12));
        b.setBorder(BorderFactory.createLineBorder(new Color(0, 255, 150), 1));
        b.setPreferredSize(new Dimension(0, 40));
        return b;
    }
}