package com.multithreaded.downloaderUI;

import com.multithreaded.downloader.DownloadManager;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Represents the main panel for managing and displaying download tasks.
 * <p>
 * This panel includes:
 * <ul>
 *     <li>URL input area</li>
 *     <li>Start, Stop, and Cancel buttons</li>
 *     <li>Download progress display</li>
 *     <li>Console log output</li>
 * </ul>
 * It uses a {@code ConcurrentHashMap} to track download tasks and updates the overall speed periodically.
 */
public class DownloadPanel extends JPanel {

    private final DownloadManager downloadManager;          // Manages all downloads
    private final JPanel progressPanel;
    private final List<JPanel> downloadItems;               // Tracks download panels
    private final JLabel overallSpeedLabel;
    private final JLabel tasksLabel;
    private final JTextArea console;
    private final ConcurrentHashMap<String, DownloadTask> tasks;  // Map of download tasks

    /**
     * Constructs the {@code DownloadPanel} with all UI components.
     * <p>
     * Initializes the layout, buttons, progress panel, console log, and speed update timer.
     */
    public DownloadPanel() {
        downloadManager = new DownloadManager();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(40, 40, 40));

        downloadItems = new ArrayList<>();
        tasks = new ConcurrentHashMap<>();

        overallSpeedLabel = new JLabel("Overall Speed: 0 KB/s");
        overallSpeedLabel.setFont(new Font("Arial", Font.BOLD, 14));
        overallSpeedLabel.setForeground(Color.GREEN);

        tasksLabel = new JLabel("Tasks: 0");
        tasksLabel.setFont(new Font("Arial", Font.BOLD, 14));
        tasksLabel.setForeground(Color.CYAN);

        JTextArea urlInput = new JTextArea(4, 40);
        urlInput.setBorder(BorderFactory.createTitledBorder("Paste URLs Here"));
        urlInput.setBackground(new Color(50, 50, 50));
        urlInput.setForeground(Color.WHITE);
        urlInput.setCaretColor(Color.WHITE);
        urlInput.setLineWrap(true);
        urlInput.setWrapStyleWord(true);

        JScrollPane urlScroll = new JScrollPane(urlInput);
        urlScroll.setPreferredSize(new Dimension(580, 120));

        JButton startButton = createButton("Start All", new Color(34, 139, 34), e -> startAllDownloads(urlInput));
        JButton stopButton = createButton("Stop All", new Color(178, 34, 34), e -> stopAllDownloads());
        JButton cancelButton = createButton("Cancel All", new Color(255, 69, 0), e -> cancelAllDownloads());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBackground(new Color(40, 40, 40));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusPanel.add(overallSpeedLabel);
        statusPanel.add(tasksLabel);
        statusPanel.setBackground(new Color(40, 40, 40));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(statusPanel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);

        progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setBackground(new Color(45, 45, 45));

        JScrollPane progressScroll = new JScrollPane(progressPanel);
        progressScroll.setPreferredSize(new Dimension(580, 400));

        console = new JTextArea(8, 40);
        console.setBorder(BorderFactory.createTitledBorder("Console Log"));
        console.setEditable(false);
        console.setBackground(new Color(30, 30, 30));
        console.setForeground(Color.GREEN);
        console.setCaretColor(Color.WHITE);

        JScrollPane consoleScroll = new JScrollPane(console);
        consoleScroll.setPreferredSize(new Dimension(580, 150));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(urlScroll, BorderLayout.NORTH);
        topPanel.add(headerPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(progressScroll, BorderLayout.CENTER);
        add(consoleScroll, BorderLayout.SOUTH);
        Timer speedUpdateTimer = new Timer(1000, e -> updateOverallSpeed());
        speedUpdateTimer.start();
    }


    /**
     * Creates a styled button.
     *
     * @param text    The button label text.
     * @param bgColor The background color of the button.
     * @param action  The action listener for the button click event.
     * @return A styled {@code JButton}.
     */
    private JButton createButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.addActionListener(action);
        return button;
    }

    /**
     * Starts all downloads from the URL input.
     *
     * @param urlInput The text area containing URLs to download.
     */
    private void startAllDownloads(JTextArea urlInput) {
        String[] urls = urlInput.getText().split("\n");

        if (urls.length == 0 || urls[0].isEmpty()) {
            JOptionPane.showMessageDialog(this, "No URLs provided!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (String url : urls) {
            if (!url.trim().isEmpty()) {
                addDownload(url.trim());
            }
        }
    }


    /**
     * Adds a new download task with progress bar.
     *
     * @param url The URL to download.
     */
    private void addDownload(String url) {
        JPanel downloadItem = createDownloadItem(url);
        progressPanel.add(downloadItem);
        downloadItems.add(downloadItem);
        tasksLabel.setText("Tasks: " + downloadItems.size());

        JProgressBar progressBar = (JProgressBar) downloadItem.getComponent(1);
        JLabel speedLabel = (JLabel) ((JPanel) downloadItem.getComponent(2)).getComponent(0);

        downloadManager.addDownload(url, "D:/", progressBar, console);
        tasks.put(url, new DownloadTask(progressBar, speedLabel));

        console.append("Added download: " + url + "\n");

        progressPanel.revalidate();
        progressPanel.repaint();
    }

    /**
     * Creates a panel for each download.
     */
    private JPanel createDownloadItem(String url) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(new Color(50, 50, 50));
        itemPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

        JLabel nameLabel = new JLabel("⬇️ " + url);
        nameLabel.setForeground(Color.WHITE);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        JLabel speedLabel = new JLabel("Speed: 0 KB/s");
        speedLabel.setForeground(Color.YELLOW);

        JPanel speedPanel = new JPanel(new BorderLayout());
        speedPanel.add(speedLabel, BorderLayout.WEST);

        itemPanel.add(nameLabel, BorderLayout.NORTH);
        itemPanel.add(progressBar, BorderLayout.CENTER);
        itemPanel.add(speedPanel, BorderLayout.SOUTH);

        return itemPanel;
    }

    /**
     * Stops all downloads.
     */
    private void stopAllDownloads() {
        downloadManager.stopAllDownloads();
        console.append("All downloads stopped.\n");
    }

    /**
     * Cancels all downloads and clears UI.
     */
    private void cancelAllDownloads() {
        downloadManager.stopAllDownloads();
        downloadItems.clear();
        progressPanel.removeAll();
        progressPanel.revalidate();
        progressPanel.repaint();
        console.append("All downloads cancelled.\n");
    }

    /**
     * Updates the overall download speed.
     */
    private void updateOverallSpeed() {
        overallSpeedLabel.setText("Overall Speed: " + downloadManager.getTotalSpeed() + " KB/s");
    }

    /**
     * Represents a download task executed in the background.
     */
    private static class DownloadTask extends SwingWorker<Void, Void> {
        private final JProgressBar progressBar;
        private final JLabel speedLabel;
        private final Random random = new Random();
        private int speed = 0;

        public DownloadTask(JProgressBar progressBar, JLabel speedLabel) {
            this.progressBar = progressBar;
            this.speedLabel = speedLabel;
        }

        @Override
        protected Void doInBackground() {
            for (int i = 0; i <= 100; i++) {
                speed = random.nextInt(500) + 100;
                speedLabel.setText("Speed: " + speed + " KB/s");
                progressBar.setValue(i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                }
            }
            return null;
        }

        public int getSpeed() {
            return speed;
        }
    }
}
