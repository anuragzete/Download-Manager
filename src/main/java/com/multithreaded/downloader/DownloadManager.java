package com.multithreaded.downloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.JTextArea;


/**
 * Manages file downloads using multithreading.
 * <p>
 * This class handles concurrent file downloads by utilizing a thread pool,
 * tracking download speeds, and supporting cancellation of individual or all downloads.
 * </p>
 */
public class DownloadManager {

    private final ExecutorService executor;                          // Thread pool for downloads
    private final ConcurrentHashMap<String, Future<?>> downloadTasks; // Ongoing downloads
    private final ConcurrentHashMap<String, Long> downloadSpeeds;
    private final ConcurrentHashMap<String, Long> bytesDownloaded;

    /**
     * Constructs a DownloadManager with a fixed thread pool and initializes tracking maps.
     * <p>
     * The manager uses a thread pool with 5 threads for concurrent downloading and
     * tracks download speeds and bytes downloaded.
     * </p>
     */
    public DownloadManager() {
        executor = Executors.newFixedThreadPool(5);
        downloadTasks = new ConcurrentHashMap<>();
        downloadSpeeds = new ConcurrentHashMap<>();
        bytesDownloaded = new ConcurrentHashMap<>();

        startSpeedMonitor();
    }

    /**
     * Starts a scheduled task to monitor download speeds every second.
     * <p>
     * It calculates the difference in downloaded bytes to determine the current speed
     * and stores it in the {@code downloadSpeeds} map.
     * </p>
     */
    private void startSpeedMonitor() {
        ScheduledExecutorService speedExecutor = Executors.newSingleThreadScheduledExecutor();
        speedExecutor.scheduleAtFixedRate(() -> {
            for (String url : downloadTasks.keySet()) {
                long previousBytes = bytesDownloaded.getOrDefault(url, 0L);
                long currentBytes = bytesDownloaded.get(url);

                long speed = (currentBytes - previousBytes) / 1024;
                downloadSpeeds.put(url, speed);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    /**
     * Adds a new download task to the queue.
     * <p>
     * If the download for the specified URL is already in progress, it skips the duplicate request.
     * </p>
     *
     * @param url          The URL of the file to download
     * @param downloadDir  The directory to save the downloaded file
     * @param progressBar  The progress bar to update download progress
     * @param console      The console area to display download logs
     */
    public void addDownload(String url, String downloadDir, JProgressBar progressBar, JTextArea console) {
        if (downloadTasks.containsKey(url)) {
            console.append("Already downloading: " + url + "\n");
            return;
        }

        bytesDownloaded.put(url, 0L);
        downloadSpeeds.put(url, 0L);

        // Submit download task to executor
        Future<?> future = executor.submit(() -> {
            try {
                downloadFile(url, downloadDir, progressBar, console);
            } catch (IOException e) {
                SwingUtilities.invokeLater(() ->
                        console.append("Failed: " + url + "\n" + e.getMessage() + "\n"));
            }
        });

        downloadTasks.put(url, future);
    }

    /**
     * Downloads a file from the given URL with progress tracking.
     * <p>
     * It handles HTTP connections, reads the file in chunks, and updates the progress bar.
     * </p>
     *
     * @param urlStr       The URL of the file
     * @param downloadDir  The directory to save the file
     * @param progressBar  The progress bar for visual feedback
     * @param console      The console for displaying logs
     * @throws IOException If an I/O error occurs during downloading
     */
    private void downloadFile(String urlStr, String downloadDir, JProgressBar progressBar, JTextArea console) throws IOException {
        HttpURLConnection connection = null;
        InputStream in = null;
        FileOutputStream out = null;

        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int contentLength = connection.getContentLength();
            if (contentLength <= 0) {
                SwingUtilities.invokeLater(() -> console.append("⚠ Invalid content length: " + urlStr + "\n"));
                return;
            }

            // Extract and sanitize filename
            String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
            if (fileName.isEmpty()) {
                fileName = "downloaded_file";
            }
            fileName = fileName.replaceAll("[\\\\/:*?\"<>|&=]", "_");

            // Infer extension using FileUtils
            String contentType = connection.getContentType();
            if (!fileName.contains(".") && contentType != null) {
                String extension = FileUtils.getExtensionFromContentType(contentType);
                if (extension != null) {
                    fileName += "." + extension;
                }
            }

            final String finalFileName = fileName;

            File outputFile = new File(downloadDir, finalFileName);
            outputFile.getParentFile().mkdirs();

            in = connection.getInputStream();
            out = new FileOutputStream(outputFile);

            byte[] buffer = new byte[8192];
            int bytesRead;
            long totalBytesRead = 0;

            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                bytesDownloaded.put(urlStr, totalBytesRead);

                int percent = (int) ((totalBytesRead * 100) / contentLength);
                SwingUtilities.invokeLater(() -> progressBar.setValue(percent));
            }

            SwingUtilities.invokeLater(() -> console.append("Downloaded: " + finalFileName + "\n"));

        } catch (Exception e) {
            SwingUtilities.invokeLater(() ->
                    console.append("Error downloading: " + urlStr + "\n" + e.getMessage() + "\n"));

        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
            if (connection != null) connection.disconnect();
        }
    }

    /**
     * Stops a specific download by its URL.
     * <p>
     * The corresponding future task is cancelled, and tracking data is removed.
     * </p>
     *
     * @param url The URL of the download to stop
     */
    public void stopDownload(String url) {
        Future<?> future = downloadTasks.remove(url);
        if (future != null) {
            future.cancel(true);
            downloadSpeeds.remove(url);
            bytesDownloaded.remove(url);
        }
    }

    /**
     * Stops all active downloads.
     * <p>
     * Cancels all running future tasks and clears the download tracking maps.
     * </p>
     */
    public void stopAllDownloads() {
        for (Future<?> future : downloadTasks.values()) {
            future.cancel(true);
        }
        downloadTasks.clear();
        downloadSpeeds.clear();
        bytesDownloaded.clear();
    }

    /**
     * Returns the total download speed of all active downloads.
     * <p>
     * It sums the speeds of all currently active downloads.
     * </p>
     *
     * @return The total download speed in KB/s
     */
    public long getTotalSpeed() {
        return downloadSpeeds.values().stream().mapToLong(Long::longValue).sum();
    }
}
