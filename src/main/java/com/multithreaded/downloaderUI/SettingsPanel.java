package com.multithreaded.downloaderUI;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Dimension;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Represents the settings panel for configuring the downloader.
 * <p>
 * This panel allows users to customize settings such as download location,
 * speed limit, timeout, retry attempts, and enable/disable parallel downloads
 * and notifications. It also offers light/dark theme switching.
 * </p>
 */
public class SettingsPanel extends JPanel {

    private String downloadDir = "downloads";
    private int speedLimit = 1024;
    private int timeout = 10;
    private int retryCount = 3;
    private boolean autoResume = true;
    private boolean notificationsEnabled = true;
    private boolean parallelMode = true;

    /**
     * Constructs the SettingsPanel with the provided parent JFrame.
     *
     * @param parent The parent JFrame of the panel, used for theme updates.
     */
    public SettingsPanel(JFrame parent) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Section 1: Download Location
        JPanel locationPanel = createTitledPanel("Download Location");
        JTextField locationField = new JTextField(downloadDir, 20);
        locationField.setFont(new Font("Arial", Font.PLAIN, 16));
        locationField.setEditable(false);

        JButton locationButton = new JButton("Change...");
        locationButton.setFont(new Font("Arial", Font.BOLD, 14));

        locationButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                downloadDir = fileChooser.getSelectedFile().getAbsolutePath();
                locationField.setText(downloadDir);
            }
        });

        locationPanel.add(locationField);
        locationPanel.add(locationButton);

        // Section 2: Numeric Fields for Speed, Timeout & Retry
        JPanel numericPanel = createTitledPanel("Download Options");
        numericPanel.setLayout(new GridLayout(3, 2, 15, 15));

        JTextField speedField = createNumericField(String.valueOf(speedLimit));
        JTextField timeoutField = createNumericField(String.valueOf(timeout));
        JTextField retryField = createNumericField(String.valueOf(retryCount));

        numericPanel.add(new JLabel("Speed Limit (KB/s):"));
        numericPanel.add(speedField);
        numericPanel.add(new JLabel("Timeout (seconds):"));
        numericPanel.add(timeoutField);
        numericPanel.add(new JLabel("Retry Attempts:"));
        numericPanel.add(retryField);

        // Section 3: Checkboxes
        JPanel checkboxPanel = createTitledPanel("Preferences");
        checkboxPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JCheckBox autoResumeCheckbox = new JCheckBox("Auto Resume Downloads", autoResume);
        JCheckBox notificationsCheckbox = new JCheckBox("Enable Notifications", notificationsEnabled);
        JCheckBox parallelModeCheckbox = new JCheckBox("Enable Parallel Mode", parallelMode);

        autoResumeCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));
        notificationsCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));
        parallelModeCheckbox.setFont(new Font("Arial", Font.PLAIN, 16));

        checkboxPanel.add(autoResumeCheckbox);
        checkboxPanel.add(notificationsCheckbox);
        checkboxPanel.add(parallelModeCheckbox);

        // Section 4: Theme Switch
        JPanel themePanel = createTitledPanel("Theme");
        themePanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JButton darkModeButton = new JButton("Dark Mode");
        JButton lightModeButton = new JButton("Light Mode");

        darkModeButton.setFont(new Font("Arial", Font.BOLD, 14));
        lightModeButton.setFont(new Font("Arial", Font.BOLD, 14));

        darkModeButton.addActionListener(e -> {
            FlatDarkLaf.setup();
            SwingUtilities.updateComponentTreeUI(parent);
        });

        lightModeButton.addActionListener(e -> {
            FlatLightLaf.setup();
            SwingUtilities.updateComponentTreeUI(parent);
        });

        themePanel.add(darkModeButton);
        themePanel.add(lightModeButton);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton applyButton = new JButton("Apply");
        JButton resetButton = new JButton("Reset to Default");

        applyButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));

        applyButton.addActionListener(e -> applySettings(
                parseInt(speedField.getText(), speedLimit),
                parseInt(timeoutField.getText(), timeout),
                parseInt(retryField.getText(), retryCount),
                autoResumeCheckbox.isSelected(),
                notificationsCheckbox.isSelected(),
                parallelModeCheckbox.isSelected()
        ));

        resetButton.addActionListener(e -> resetSettings(
                speedField, timeoutField, retryField,
                autoResumeCheckbox, notificationsCheckbox, parallelModeCheckbox
        ));

        buttonPanel.add(applyButton);
        buttonPanel.add(resetButton);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(locationPanel, gbc);

        gbc.gridy++;
        mainPanel.add(numericPanel, gbc);

        gbc.gridy++;
        mainPanel.add(checkboxPanel, gbc);

        gbc.gridy++;
        mainPanel.add(themePanel, gbc);

        gbc.gridy++;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Creates a numeric JTextField with validation.
     *
     * @param value The default value for the field.
     * @return A numeric JTextField with validation.
     */
    private JTextField createNumericField(String value) {
        JTextField field = new JTextField(value, 10);
        field.setFont(new Font("Arial", Font.PLAIN, 16));
        field.setPreferredSize(new Dimension(80, 35));

        // Validate numeric input
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                if (!Character.isDigit(c) && c != '\b' && c != '\n') {
                    evt.consume();
                }
            }
        });

        return field;
    }

    /**
     * Creates a titled panel with a specific title.
     *
     * @param title The title of the panel.
     * @return A JPanel with a border and title.
     */
    private JPanel createTitledPanel(String title) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14),
                Color.LIGHT_GRAY
        ));
        return panel;
    }

    /**
     * Parses an integer from a string, falling back to a default value on failure.
     */
    private int parseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Applies the settings to the downloader configuration.
     */
    private void applySettings(int speed, int timeout, int retry, boolean autoResume, boolean notifications, boolean parallel) {
        this.speedLimit = speed;
        this.timeout = timeout;
        this.retryCount = retry;
        this.autoResume = autoResume;
        this.notificationsEnabled = notifications;
        this.parallelMode = parallel;

        saveSettings();
        JOptionPane.showMessageDialog(this, "Settings Applied Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Resets the downloader configuration settings.
     */
    private void resetSettings(JTextField speedField, JTextField timeoutField, JTextField retryField,
                               JCheckBox autoResumeCheckbox, JCheckBox notificationsCheckbox, JCheckBox parallelModeCheckbox) {
        speedField.setText("1024");
        timeoutField.setText("10");
        retryField.setText("3");

        autoResumeCheckbox.setSelected(true);
        notificationsCheckbox.setSelected(true);
        parallelModeCheckbox.setSelected(true);
    }

    /**
     * Saves the current settings to a configuration file.
     */
    private void saveSettings() {
        try (FileWriter writer = new FileWriter(new File("settings.config"))) {
            writer.write("DownloadDir=" + downloadDir + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
