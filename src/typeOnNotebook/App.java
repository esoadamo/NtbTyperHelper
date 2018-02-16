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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;

public class App {
	// User settings
	public final static long hiddenMouseMillis = 300;
	public final static int keyboardShortcutModeSwitch[] = { NativeKeyEvent.VC_CONTROL, NativeKeyEvent.VC_ALT,
			NativeKeyEvent.VC_P };

	// Other global variables
	public final static App app = new App();
	private static TrayIcon trayIcon;
	private static AppMode mode = AppMode.ENABLED;
	private static Map<AppMode, Image> images = new HashMap<AppMode, Image>();

	public static void main(String[] args) {
		LogManager.getLogManager().reset();
		final Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		app.run(args);

		for (String arg : args)
			// user wants to start in disabled mode
			if (arg.toLowerCase().equals("disabled")) {
				System.out.println("going into disabled mode");
				setMode(AppMode.DISABLED);
			}
	}

	/**
	 * Shows exception as GUI message
	 * 
	 * @param e
	 *            exception that is printed to the console
	 */
	public void errorHandler(Exception e) {
		JOptionPane.showMessageDialog(null, "... there was an error :'(\n" + e.getMessage(),
				"Sorry to fail you, but...", JOptionPane.ERROR_MESSAGE);
		System.err.println(e.getMessage());
		System.exit(1);
	}

	/**
	 * Starts hooks, shows tray icon
	 */
	public void run(String[] args) {
		System.out.print("Starting ... ");

		InputActionListener actionListener = new InputActionListener();
		try {
			actionListener.init();
		} catch (Exception e1) {
			errorHandler(e1);
		}

		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException e) {
			errorHandler(e);
		}

		boolean showBalloonTipMessage = true;
		for (String arg : args)
			// user wants to start in disabled mode
			if (arg.toLowerCase().equals("silent")) {
				System.out.print("going into silent mode ... ");
				showBalloonTipMessage = false;
			}
		initTrayIcon(showBalloonTipMessage);

		GlobalScreen.addNativeKeyListener(actionListener);
		GlobalScreen.addNativeMouseListener(actionListener);
		GlobalScreen.addNativeMouseMotionListener(actionListener);

		System.out.println("ready!");
	}

	/*
	 * Checks if the application is in disabled mode
	 */
	public static boolean isDisabled() {
		return mode == AppMode.DISABLED;
	}

	/**
	 * Changes mode of the application
	 * 
	 * @param newMode
	 *            new mode of application
	 */
	public static void setMode(AppMode newMode) {
		mode = newMode;
		trayIcon.setImage(images.get(mode));
	}

	/**
	 * Initializes and shows system tray icon
	 * 
	 * @param showBalloonTipMessage
	 *            if set to true, shows balloon message upon start
	 */
	private void initTrayIcon(boolean showBalloonTipMessage) {
		if (!SystemTray.isSupported())
			errorHandler(new Exception("System tray is not supported"));

		final SystemTray systemTray = SystemTray.getSystemTray();
		final PopupMenu trayPopupMenu = new PopupMenu();
		try {
			images.put(AppMode.ENABLED, ImageIO.read(getClass().getResourceAsStream("/pencil.png")));
			images.put(AppMode.DISABLED, ImageIO.read(getClass().getResourceAsStream("/pencil-red.png")));
			images.put(AppMode.ACTIVE, ImageIO.read(getClass().getResourceAsStream("/pencil-green.png")));
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

		trayIcon = new TrayIcon(images.get(mode), "Hide mouse while typing", trayPopupMenu);
		trayIcon.setImageAutoSize(true);

		try {
			systemTray.add(trayIcon);
		} catch (AWTException e1) {
			errorHandler(e1);
		}

		if (showBalloonTipMessage)
			trayIcon.displayMessage("Touchapd disabler", "your mouse will be taken care of while you type now",
					TrayIcon.MessageType.INFO);
	}
}
