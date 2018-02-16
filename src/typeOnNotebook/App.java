package typeOnNotebook;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;

public class App {
	final static App app = new App();
	final static long hiddenMouseMillis = 1500;
	static TrayIcon trayIcon;

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		app.run();
	}

	/**
	 * Shows exception as GUI message
	 * @param e exception that is printed to the console
	 */
	public void errorHandler(Exception e) {
		JOptionPane.showMessageDialog(null, "... there was an error :'(\n" + e.getMessage(), "Sorry to fail you, but...", JOptionPane.ERROR_MESSAGE);
		System.err.println(e.getMessage());
		System.exit(1);
	}

	/**
	 * Starts hooks, shows tray icon
	 */
	public void run() {
		System.out.println("Starting...");

		InputActionListener actionListener = new InputActionListener();

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			errorHandler(e);
		}
		
		initTrayIcon();

		GlobalScreen.addNativeKeyListener(actionListener);
		GlobalScreen.addNativeMouseListener(actionListener);
		GlobalScreen.addNativeMouseMotionListener(actionListener);
	}
	
	/**
	 * Initializes and shows system tray icon
	 */
	private void initTrayIcon() {
		if(!SystemTray.isSupported())
			errorHandler(new Exception("System tray is not supported"));
	    
	    final SystemTray systemTray = SystemTray.getSystemTray();
	    final PopupMenu trayPopupMenu = new PopupMenu();
	    Image trayIconImage = null;
		try {
			trayIconImage = ImageIO.read(getClass().getResourceAsStream("/pencil.png"));
		} catch (IOException e2) {
			errorHandler(e2);
		}
	    
	    MenuItem close = new MenuItem("Exit");
	    close.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	            System.exit(0);             
	        }
	    });
	    trayPopupMenu.add(close);
	    
	    trayIcon = new TrayIcon(trayIconImage, "Hide mouse while typing", trayPopupMenu);
	    trayIcon.setImageAutoSize(true);
	    
	    try {
			systemTray.add(trayIcon);
		} catch (AWTException e1) {
			errorHandler(e1);
		}
	    
	    trayIcon.displayMessage("Touchapd disabler", "your mouse will be taken care of while you type now", TrayIcon.MessageType.INFO);
	}
}
