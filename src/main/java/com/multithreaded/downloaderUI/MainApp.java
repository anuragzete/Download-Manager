package com.multithreaded.downloaderUI;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;

import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * The main JFrame class for the Multithreaded Downloader application.
 * <p>
 * This class creates the GUI, including a custom title bar, sidebar, and content panels.
 * It uses the FlatLaf library for modern dark-themed styling.
 * </p>
 */
public class MainApp extends JFrame {

    private final Map<String, JButton> sidebarButtons;
    private JPanel sidebar;
    private JPanel contentPanel;
    private boolean sidebarExpanded = true;
    private final int SIDEBAR_WIDTH = 150;
    private final int COLLAPSED_WIDTH = 50;
    private Point mouseClickPoint;

    /**
     * Constructs the main application window.
     * <p>
     * Initializes the GUI, creates panels, adds components, and sets up the application layout.
     * </p>
     */
    public MainApp() {
        super("Download Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setUndecorated(true);
        FlatDarkLaf.setup();

        sidebarButtons = new HashMap<>();

        setLayout(new BorderLayout());

        add(createCustomTitleBar(), BorderLayout.NORTH);

        // Sidebar and Content Panel
        sidebar = createSidebar();
        contentPanel = new JPanel(new CardLayout());

        // Add panels
        DownloadPanel homePanel = new DownloadPanel();
        SettingsPanel settingsPanel = new SettingsPanel(this);
        AboutPanel aboutPanel = new AboutPanel();

        contentPanel.add(homePanel, "Home");
        contentPanel.add(settingsPanel, "Settings");
        contentPanel.add(aboutPanel, "About");

        add(sidebar, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        showPanel("Home");

        setVisible(true);
    }

    /**
     * Creates the custom dark title bar with FlatLaf icons.
     *
     * @return The custom title bar panel
     */
    private JPanel createCustomTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(30, 30, 30));
        titleBar.setPreferredSize(new Dimension(getWidth(), 40));

        // Title label
        JLabel titleLabel = new JLabel("  Multithreaded Downloader");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Window control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.setBackground(new Color(30, 30, 30));

        JButton minimizeButton = createTitleBarButton(new FlatSVGIcon("icons/minimize.svg", 16, 16));
        JButton maximizeButton = createTitleBarButton(new FlatSVGIcon("icons/fullscreen.svg", 16, 16));
        JButton closeButton = createTitleBarButton(new FlatSVGIcon("icons/close.svg", 16, 16));

        minimizeButton.addActionListener(e -> setState(JFrame.ICONIFIED));
        maximizeButton.addActionListener(e -> setExtendedState(getExtendedState() ^ JFrame.MAXIMIZED_BOTH));
        closeButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(minimizeButton);
        buttonPanel.add(maximizeButton);
        buttonPanel.add(closeButton);

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(buttonPanel, BorderLayout.EAST);

        //  window dragging
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseClickPoint = e.getPoint();
            }
        });

        titleBar.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen() - mouseClickPoint.x;
                int y = e.getYOnScreen() - mouseClickPoint.y;
                setLocation(x, y);
            }
        });

        return titleBar;
    }

    /**
     * Creates a stylish title bar button with FlatLaf icons.
     *
     * @param icon The SVG icon for the button
     * @return A styled JButton with hover effects
     */
    private JButton createTitleBarButton(FlatSVGIcon icon) {
        JButton button = new JButton(icon);
        button.setPreferredSize(new Dimension(45, 30));
        button.setBackground(new Color(40, 40, 40));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder());

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 70, 70));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(40, 40, 40));
            }
        });

        return button;
    }

    /**
     * Creates the sidebar with navigation buttons and a hamburger toggle.
     *
     * @return The sidebar panel
     */
    private JPanel createSidebar() {
        sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(SIDEBAR_WIDTH, getHeight()));   // Expanded size
        sidebar.setBackground(new Color(40, 40, 40));

        JButton hamburgerButton = createSidebarButton("Menu", new FlatSVGIcon("icons/menu.svg", 24, 24));
        hamburgerButton.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 50));

        hamburgerButton.addActionListener(e -> toggleSidebar());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(40, 40, 40));
        topPanel.add(hamburgerButton);
        topPanel.add(Box.createVerticalStrut(10));

        JButton homeButton = createSidebarButton("Home", new FlatSVGIcon("icons/home.svg", 24, 24));
        topPanel.add(homeButton);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBackground(new Color(40, 40, 40));

        JButton settingsButton = createSidebarButton("Settings", new FlatSVGIcon("icons/settings.svg", 24, 24));
        JButton aboutButton = createSidebarButton("About", new FlatSVGIcon("icons/about.svg", 24, 24));

        bottomPanel.add(Box.createVerticalGlue());
        bottomPanel.add(settingsButton);
        bottomPanel.add(Box.createVerticalStrut(10));
        bottomPanel.add(aboutButton);
        bottomPanel.add(Box.createVerticalStrut(20));

        sidebar.add(topPanel, BorderLayout.NORTH);
        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        homeButton.addActionListener(e -> showPanel("Home"));
        settingsButton.addActionListener(e -> showPanel("Settings"));
        aboutButton.addActionListener(e -> showPanel("About"));

        return sidebar;
    }

    /**
     * Creates a sidebar button with FlatLaf icons.
     *
     * @param label The button label
     * @param icon The icon for the button
     * @return A styled JButton
     */
    private JButton createSidebarButton(String label, FlatSVGIcon icon) {
        JButton button = new JButton(sidebarExpanded ? label : "");
        button.setIcon(icon);
        button.setPreferredSize(new Dimension(SIDEBAR_WIDTH, 50));
        button.setBackground(new Color(40, 40, 40));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        sidebarButtons.put(label, button);
        return button;
    }

    /**
     * Toggles the sidebar between expanded and collapsed states.
     */
    private void toggleSidebar() {
        sidebarExpanded = !sidebarExpanded;
        sidebar.setPreferredSize(new Dimension(
                sidebarExpanded ? SIDEBAR_WIDTH : COLLAPSED_WIDTH, getHeight()
        ));
        sidebar.revalidate();
        sidebar.repaint();
    }

    /**
     * Displays the selected content panel.
     *
     * @param panelName The name of the panel to display
     */
    private void showPanel(String panelName) {
        CardLayout layout = (CardLayout) contentPanel.getLayout();
        layout.show(contentPanel, panelName);
    }
}
