package com.fatico.winthing.gui;

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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fatico.winthing.Application;
import com.fatico.winthing.logging.ConsoleLogger;

@SuppressWarnings("serial")
public class WindowGUI extends JFrame {
	public static String APPNAME;
	public static String APPVERSION;
	
	private static WindowGUI singleton = new WindowGUI();
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private Map<Integer, Component> components = new HashMap<Integer, Component>();
	
	public enum GUI {
		TEXTAREA (0),
		SCROLLBAR (1),
		TRAYICON (2);
		
		public final int ID;
		
		private GUI(int v) {
			ID = v;
		}
	}
	
	public enum Actions {
		EXIT,
		EVENTS,
		CLOSE,
		QUIT
	}
	
	private WindowGUI() {
		APPNAME = Application.class.getPackage().getImplementationTitle();
		APPVERSION = Application.class.getPackage().getImplementationVersion();
		
		setTitle(APPNAME + " " + APPVERSION);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		setVisible(false);
		setLayout(new BorderLayout());
		setResizable(false);
		setPreferredSize(new Dimension(800, 530));
		setSize(getPreferredSize());
		
		JTextArea logs = new JTextArea();
		logs.setEditable(false);
		logs.setRows(25);
		components.put(GUI.TEXTAREA.ID, logs);
		
		JScrollPane scrolls = new JScrollPane(logs);
		scrolls.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		getContentPane().add(scrolls, BorderLayout.NORTH);
		components.put(GUI.SCROLLBAR.ID, scrolls);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(panel, BorderLayout.SOUTH);		
		
		JButton btnClose = new JButton("Close");
		btnClose.setToolTipText("Close this window");
		btnClose.addMouseListener(new mouseEventListener(this, Actions.CLOSE));
		panel.add(btnClose);
		
		JButton btnQuit = new JButton("Quit");
		btnQuit.setToolTipText("Stop and terminate application");
		btnQuit.addMouseListener(new mouseEventListener(this, Actions.QUIT));
		panel.add(btnQuit);
	}
	
	public static WindowGUI getInstance() {
		return singleton;
	}	
	
	public void tray() {
		try {
			 TrayIcon trayIcon = null;
			 if (SystemTray.isSupported()) {
				 SystemTray tray = SystemTray.getSystemTray();
				 Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource("favicon-red.png"));
				 Image scaled = image.getScaledInstance(tray.getTrayIconSize().width, tray.getTrayIconSize().height, Image.SCALE_SMOOTH);
				 
				 PopupMenu popup = new PopupMenu();
				 
				 MenuItem menuTitle = new MenuItem(APPNAME);
				 menuTitle.setEnabled(false);
				 popup.add(menuTitle);
				 
				 popup.addSeparator();
				 
				 MenuItem menuEvent = new MenuItem("Events");
				 menuEvent.addActionListener(new mouseEventListener(this, Actions.EVENTS));
				 popup.add(menuEvent);
				 
				 MenuItem menuExit = new MenuItem("Exit");
				 menuExit.addActionListener(new mouseEventListener(this, Actions.EXIT));
				 popup.add(menuExit);
				 
				 trayIcon = new TrayIcon(scaled, APPNAME + " " + APPVERSION, popup);
				 trayIcon.addMouseListener(new mouseEventListener(this, Actions.EVENTS));
				 
				 tray.add(trayIcon);
			 }
		}
		catch (Throwable e) {
			logger.warn("System tray not supported", e);
		}
	}
	
	public void setIcon(boolean color) {
		SystemTray tray = SystemTray.getSystemTray();
		TrayIcon[] icons = tray.getTrayIcons();
		if (icons.length > 0) {
			String name = color ? "favicon-green.png" : "favicon-red.png";
			
			Image image = Toolkit.getDefaultToolkit().getImage(getClass().getClassLoader().getResource(name));
			Image scaled = image.getScaledInstance(tray.getTrayIconSize().width, tray.getTrayIconSize().height, Image.SCALE_SMOOTH);
			
			icons[0].setImage(scaled);
		}
	}
	
	public void reload() {
		if (isVisible()) {
			JTextArea area = (JTextArea)components.get(GUI.TEXTAREA.ID);
			area.setText(ConsoleLogger.getEvents());
			
			JScrollPane scroll = (JScrollPane)components.get(GUI.SCROLLBAR.ID);
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
		System.exit(0);
	}
	
	public class mouseEventListener implements ActionListener, MouseListener {
		private Actions action;
		private WindowGUI window;
		
		public mouseEventListener(WindowGUI gui, Actions act) {
			window = gui;
			action = act;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (action == WindowGUI.Actions.EXIT) {
				window.quit();
			}
			
			if (action == WindowGUI.Actions.EVENTS) {
				window.open();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == MouseEvent.BUTTON1) {
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
		public void mousePressed(MouseEvent e) {
			// no code here
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// no code here
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// no code here
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// no code here
		}
	}
}
