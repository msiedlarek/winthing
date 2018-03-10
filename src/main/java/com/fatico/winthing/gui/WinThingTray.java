package com.fatico.winthing.gui;

import com.fatico.winthing.Application;

import com.google.inject.Inject;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

public class WinThingTray {

    private static final String ICON_PATH = "/images/2692-128.png";
    private static final String ICON_DESCRIPTION = "Emoji icons provided free by EmojiOne";

    private static final Logger logger = LoggerFactory.getLogger(WinThingTray.class);

    @Inject
    public WinThingTray() {
        // do nothing
    }

    // Obtain the TrayIcon
    private TrayIcon createTrayIcon() {

        Image image = getAboutIcon().getImage();
        if (image == null) {
            return null;
        }

        // resize the icon to fit into the system tray
        int trayIconWidth = new TrayIcon(image).getSize().width;
        TrayIcon trayIcon = new TrayIcon(
                image.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));

        return trayIcon;
    }

    /**
     * Gets and Icon for the about dialog box.
     * 
     * @return the ImageIcon
     */
    private ImageIcon getAboutIcon() {
        URL imageUrl = Application.class.getResource(ICON_PATH);

        if (imageUrl == null) {
            System.err.println("Resource not found: " + ICON_PATH);
            return null;
        } else {
            return new ImageIcon(imageUrl, ICON_DESCRIPTION);
        }
    }

    /**
     * Create a system tray icon, and populate the menu.
     */
    public void initalise() {

        // Check the SystemTray is supported
        if (!SystemTray.isSupported()) {
            logger.error("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        final TrayIcon trayIcon = createTrayIcon();
        final SystemTray tray = SystemTray.getSystemTray();

        // Create a pop-up menu components
        MenuItem aboutItem = new MenuItem("About");
        aboutItem.addActionListener(new AboutItemListener());

        MenuItem setupItem = new MenuItem("Setup...");
        setupItem.setEnabled(false);
        setupItem.addActionListener(new SetupActionListener());

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.addActionListener(new QuitActionListener());

        // Add components to pop-up menu
        popup.add(setupItem);
        popup.addSeparator();
        popup.add(quitItem);
        popup.add(aboutItem);

        trayIcon.setPopupMenu(popup);

        try {
            tray.add(trayIcon);
            logger.debug("Added Tray Icon");
        } catch (AWTException e) {
            logger.error("TrayIcon could not be added.");
        }
    }

    /**
     * An ActionListener for the aboutItem.
     * 
     * @author Steven Conway
     */
    private class AboutItemListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {

            String version = null;

            // try to load from maven properties
            InputStream is = null;
            try {
                Properties properties = new Properties();
                is = getClass()
                        .getResourceAsStream("/META-INF/maven/com.fatico/winthing/pom.properties");

                if (is != null) {
                    properties.load(is);
                    version = properties.getProperty("version", "");
                }
            } catch (Exception ex) {
                version = "Unknown";
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ex) {
                        logger.error(ex.getMessage());
                    }
                }
            }

            String title = "About WinThing";
            String html = "<html><h1><strong>WinThing</strong></h1>" + "<p>Version: " + version
                    + "<p>&nbsp;</p>" + "<p>Copyright 2015-2016 Mikołaj Siedlarek &lt;"
                    + "<a href=\"mailto:mikolaj@siedlarek.pl\">mikolaj@siedlarek.pl</a>&gt;</p>"
                    + "<p>Licensed under the Apache License, Version 2.0 (the \"License\");</p>"
                    + "<p>you may not use this software except in compliance with the License.</p>"
                    + "<p>You may obtain a copy of the License at</p>" + "<p>&nbsp;</p>"
                    + "<blockquote>"
                    + "<p><a href=\"http://www.apache.org/licenses/LICENSE-2.0\" rel=\"nofollow\">"
                    + "http://www.apache.org/licenses/LICENSE-2.0</a></p>" + "</blockquote>"
                    + "<p>&nbsp;</p>"
                    + "<p>Unless required by applicable law or agreed to in writing, software</p>"
                    + "<p>distributed under the License is distributed on an \"AS IS\" BASIS,</p>"
                    + "<p>WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or "
                    + "implied.</p>"
                    + "<p>See the License for the specific language governing permissions and</p>"
                    + "<p>limitations under the License.</p>" + "</blockquote>" + "<p>&nbsp;</p>"
                    + "<p> Thanks to EmojiOne for providing free emoji icons. "
                    + "(<a href=\"www.emojione.com\">www.emojione.com</a>)</p>" + "</html>";
            JLabel message = new JLabel(html);
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE,
                    getAboutIcon());
        }
    }

    private static class QuitActionListener implements ActionListener {
        @SuppressFBWarnings(value = "DM_EXIT", 
                justification = "Really want to kill the entire application")
        @Override
        public void actionPerformed(ActionEvent evt) {
            // FIXME: should emit MQTT-LWT before killing
            System.exit(1);
        }
    }

    private static class SetupActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            // TODO: implement a way to modify and save properties
            JOptionPane.showMessageDialog(null, "Implement", "Setup", JOptionPane.ERROR_MESSAGE,
                    null);
        }
    }
}
