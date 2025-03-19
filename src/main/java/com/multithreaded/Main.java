package com.multithreaded;

import com.multithreaded.downloaderUI.MainApp;

import javax.swing.SwingUtilities;

/**
 * The entry point of the Multithreaded Downloader application.
 * <p>
 * This class initializes the GUI by launching the {@link MainApp} on the
 * Event Dispatch Thread (EDT) using {@link SwingUtilities#invokeLater}.
 * Running the GUI on the EDT ensures thread safety and responsiveness.
 * </p>
 */
public class Main {

    /**
     * The main method that starts the application.
     *
     * @param args Command-line arguments (not used in this application)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainApp::new);
    }
}
