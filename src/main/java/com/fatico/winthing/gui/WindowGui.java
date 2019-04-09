package com.fatico.winthing.gui;

import com.fatico.winthing.Application;
import com.fatico.winthing.logging.ConsoleLogger;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class WindowGui extends JFrame {
    private final String appName;
    private final String appVersion;
    private Map<Integer, Component> components = new HashMap<Integer, Component>();

    public enum Gui {
        TEXTAREA(0),
        SCROLLBAR(1),
        TRAYICON(2);

        public final int key;

        private Gui(int value) {
            key = value;
        }
    }

    public enum Actions {
        EXIT,
        EVENTS,
        CLOSE,
        QUIT
    }

    public WindowGui() {
        appName = Application.class.getPackage().getImplementationTitle();
        appVersion = Application.class.getPackage().getImplementationVersion();
    }

    public void initialize() throws AWTException {
        setTitle(appName + " " + appVersion);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setVisible(false);
        setLayout(new BorderLayout());
        setResizable(false);
        setPreferredSize(new Dimension(800, 530));
        setSize(getPreferredSize());

        JTextArea logs = new JTextArea();
        logs.setEditable(false);
        logs.setRows(25);
        components.put(Gui.TEXTAREA.key, logs);

        JScrollPane scrolls = new JScrollPane(logs);
        scrolls.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().add(scrolls, BorderLayout.NORTH);
        components.put(Gui.SCROLLBAR.key, scrolls);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btnClose = new JButton("Close");
        btnClose.setToolTipText("Close this window");
        btnClose.addMouseListener(new MouseEventListener(this, Actions.CLOSE));
        panel.add(btnClose);

        JButton btnQuit = new JButton("Quit");
        btnQuit.setToolTipText("Stop and terminate application");
        btnQuit.addMouseListener(new MouseEventListener(this, Actions.QUIT));
        panel.add(btnQuit);

        TrayIcon trayIcon = null;
        if (SystemTray.isSupported()) {
            PopupMenu popup = new PopupMenu();

            MenuItem menuTitle = new MenuItem(appName);
            menuTitle.setEnabled(false);
            popup.add(menuTitle);

            popup.addSeparator();

            MenuItem menuEvent = new MenuItem("Events");
            menuEvent.addActionListener(new MouseEventListener(this, Actions.EVENTS));
            popup.add(menuEvent);

            MenuItem menuExit = new MenuItem("Exit");
            menuExit.addActionListener(new MouseEventListener(this, Actions.EXIT));
            popup.add(menuExit);

            SystemTray tray = SystemTray.getSystemTray();

            URL url = getClass().getClassLoader().getResource("favicon-red.png");
            Image image = Toolkit.getDefaultToolkit().getImage(url);

            int trayWidth = tray.getTrayIconSize().width;
            int trayheight = tray.getTrayIconSize().height;
            Image scaled = image.getScaledInstance(trayWidth, trayheight, Image.SCALE_SMOOTH);

            trayIcon = new TrayIcon(scaled, appName + " " + appVersion, popup);
            trayIcon.addMouseListener(new MouseEventListener(this, Actions.EVENTS));
            tray.add(trayIcon);
        }
    }

    public void setIcon(boolean color) {
        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon[] icons = tray.getTrayIcons();
        if (icons.length > 0) {
            String name = color ? "favicon-green.png" : "favicon-red.png";

            URL url = getClass().getClassLoader().getResource(name);
            Image image = Toolkit.getDefaultToolkit().getImage(url);

            int trayWidth = tray.getTrayIconSize().width;
            int trayHeight = tray.getTrayIconSize().height;
            Image scaled = image.getScaledInstance(trayWidth, trayHeight, Image.SCALE_SMOOTH);
            icons[0].setImage(scaled);
        }
    }

    public void reload() {
        if (isVisible()) {
            JTextArea area = (JTextArea)components.get(Gui.TEXTAREA.key);
            area.setText(ConsoleLogger.getEvents());

            JScrollPane scroll = (JScrollPane)components.get(Gui.SCROLLBAR.key);
            scroll.getVerticalScrollBar().setValue(scroll.getVerticalScrollBar().getMaximum());
        }
    }

    public void open() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        reload();
    }

    public void close() {
        setVisible(false);
    }

    public void quit() {
        Application.quit();
    }

    @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
    public class MouseEventListener implements ActionListener, MouseListener {
        private Actions action;
        private WindowGui window;

        public MouseEventListener(WindowGui gui, Actions act) {
            window = gui;
            action = act;
        }

        @Override
        public void actionPerformed(ActionEvent event) {
            if (action == WindowGui.Actions.EXIT) {
                window.quit();
            }

            if (action == WindowGui.Actions.EVENTS) {
                window.open();
            }
        }

        @Override
        public void mouseClicked(MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                switch (action) {
                    default:
                        break;

                    case EVENTS:
                        window.open();
                        break;

                    case CLOSE:
                        window.close();
                        break;

                    case QUIT:
                        window.quit();
                        break;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent event) {
            // no code here
        }

        @Override
        public void mouseReleased(MouseEvent event) {
            // no code here
        }

        @Override
        public void mouseEntered(MouseEvent event) {
            // no code here
        }

        @Override
        public void mouseExited(MouseEvent event) {
            // no code here
        }
    }
}
