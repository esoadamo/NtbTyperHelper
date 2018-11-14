package typeOnNotebook;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;

public class MouseHider {
	private static Robot r = null;
	private static Timer timerUnhide = null;
	private static Timer timerMouseKeeper = null;
	private static Point savedMousePos = null;
	private static Point hiddenMousePos = null;
	private static boolean hidden = false;
	private static JFrame frame = null;

	/**
	 * Hides the mouse and sets hidden to true
	 */
	public static void hide() {
		// Initialize robot if it is not already
		if (r == null)
			try {
				r = new Robot();
			} catch (AWTException e) {
				App.app.errorHandler(e);
			}

		// Create the invisible frame that will be used to catch all mouse clicks while
		// invisible
		if (frame == null) {
			frame = new JFrame();
			final BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			final Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0),
					"blank cursor");
			frame.setSize(400, 400);
			frame.setResizable(false);
			frame.setAlwaysOnTop(true);
			frame.setUndecorated(true);
			frame.setFocusable(false);
			frame.setFocusableWindowState(false);
			frame.setCursor(blankCursor);
			frame.setOpacity((float) 0.1);
		}

		hidden = true;

		// Stop un-hiding, hide it!
		if (timerUnhide != null)
			timerUnhide.cancel();
		
		if (timerMouseKeeper == null) {
			// Create new timer for keeping the mouse hidden
			timerMouseKeeper = new Timer();
			
			// Save the position of the mouse before hiding
			savedMousePos = MouseInfo.getPointerInfo().getLocation();
			final Rectangle currentWindowRect = WindowManipulator.getCurrentWindowRect();
			
			// Compute where to put hidden the mouse
			hiddenMousePos = new Point(currentWindowRect.x + (currentWindowRect.width / 2),
					currentWindowRect.y + (10 * (App.os == OS.WINDOWS ? 1 : -1))); // on Linux the position is without
																					// the title bar, so we have to move
																					// 10px in up, not down
			
			// Paint a frame around the mouse so that clicking will not make any intrusive event
			frame.setLocation((int)(hiddenMousePos.getX() - 200), (int)(hiddenMousePos.getY() - 200));
			frame.setVisible(true);
			if (!App.isDisabled())
				App.setMode(AppMode.ACTIVE);
			
			// Move the mouse to the hidden position every 10ms so it keeps being hidden until .show() is called
			moveToHiddenPos();
			timerMouseKeeper.schedule(new TimerTask() {

				@Override
				public void run() {
					moveToHiddenPos();
				}
				
			}, 10, 10);
		}

	}

	/**
	 * Shows the mouse again after a timeout
	 */
	public static void show() {
		if (!hidden)
			return;
		// If we have a timer running, just reset it
		if (timerUnhide != null)
			timerUnhide.cancel();
		
		timerUnhide = new Timer();

		// Reappear the mouse after App.hiddenMouseMillis milliseconds
		timerUnhide.schedule(new TimerTask() {

			@Override
			public void run() {
				timerUnhide = null;
				hidden = false;
				
				if ((frame != null) && (frame.isVisible()))
					frame.setVisible(false);
				
				// cancel the timer that is keeping our mouse away
				if (timerMouseKeeper != null) {
					timerMouseKeeper.cancel();
					timerMouseKeeper = null;
				}
				
				r.mouseMove(savedMousePos.x, savedMousePos.y);
				if (!App.isDisabled())
					App.setMode(AppMode.ENABLED);
			}

		}, App.hiddenMouseMillis);
	}

	/**
	 * Moves the mouse back to it's hidden position
	 */
	public static void moveToHiddenPos() {
		if ((!hidden) || (r == null) || (hiddenMousePos == null))
			return;
		r.mouseMove(hiddenMousePos.x, hiddenMousePos.y);
	}

	/**
	 * Checks if the mouse is hidden
	 * 
	 * @return true if the mouse is hidden, false otherwise
	 */
	public static boolean isHidden() {
		return hidden;
	}
}
