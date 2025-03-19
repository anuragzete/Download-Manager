package com.multithreaded.downloaderUI;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JSeparator;
import javax.swing.BorderFactory;
import javax.swing.SwingConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.BorderLayout;

/**
 * The AboutPanel class represents the "About" section of the Multithreaded Downloader application.
 * <p>
 * This panel displays information about the application, including its name, version, description,
 * features, and license details. It uses a dark theme with styled labels, text areas, and a separator
 * for visual clarity.
 * </p>
 */
public class AboutPanel extends JPanel {

    /**
     * Constructs the AboutPanel with application information and layout configuration.
     * <p>
     * The panel includes:
     * <ul>
     *     <li>Application title and version</li>
     *     <li>Description of features</li>
     *     <li>License information</li>
     *     <li>Visual separator for clarity</li>
     * </ul>
     * </p>
     */
    public AboutPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(45, 45, 45));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header Section
        JLabel title = new JLabel("Multithreaded Downloader");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(new Color(0, 191, 255));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel version = new JLabel("Version: 1.0");
        version.setFont(new Font("Arial", Font.PLAIN, 18));
        version.setForeground(Color.LIGHT_GRAY);
        version.setHorizontalAlignment(SwingConstants.CENTER);

        // Description
        JTextArea description = new JTextArea("""
                The Multithreaded Downloader is a robust application designed to efficiently 
                download multiple files concurrently. It utilizes multithreading to boost 
                performance and offers intuitive controls to manage downloads effectively.

                Features include:
                - Simultaneous downloading of multiple files
                - Configurable download locations
                - Real-time progress monitoring
                - Options to start, stop, and cancel downloads individually or globally

                This project is open-source and distributed under the MIT License.
                """);
        description.setEditable(false);
        description.setForeground(Color.WHITE);
        description.setBackground(new Color(45, 45, 45));
        description.setFont(new Font("Arial", Font.PLAIN, 16));
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.GRAY);

        // License Info
        JTextArea licenseInfo = new JTextArea("""
                License: MIT License  
                Developed by: Anurag Zete  
                Contact: anuragzete27@outlook.com
                """);
        licenseInfo.setEditable(false);
        licenseInfo.setForeground(Color.LIGHT_GRAY);
        licenseInfo.setBackground(new Color(45, 45, 45));
        licenseInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        licenseInfo.setLineWrap(true);
        licenseInfo.setWrapStyleWord(true);
        licenseInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Layout
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(45, 45, 45));
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(version, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(description, BorderLayout.CENTER);
        add(separator, BorderLayout.SOUTH);
        add(licenseInfo, BorderLayout.SOUTH);
    }
}
