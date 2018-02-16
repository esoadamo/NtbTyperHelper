package typeOnNotebook;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.util.Timer;
import java.util.TimerTask;

public class MouseHider {
	private static Robot r = null;
	private static Timer t = null;
	private static Point savedMousePos = null;
	private static Point hiddenMousePos = null;
	private static boolean hidden = false;

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
		hidden = true;

		// If we have a timer running, just reset it
		if (t != null)
			t.cancel();
		// No timer running -> hide the mouse in the middle of the window bar of this window
		else {
			savedMousePos = MouseInfo.getPointerInfo().getLocation();
			final Rectangle currentWindowRect = WindowManipulator.getCurrentWindowRect();
			hiddenMousePos = new Point(currentWindowRect.x + (currentWindowRect.width / 2), currentWindowRect.y + 10);
			moveToHiddenPos();
		}
		t = new Timer();

		// Reappear the mouse after App.hiddenMouseMillis milliseconds
		t.schedule(new TimerTask() {

			@Override
			public void run() {
				t = null;
				hidden = false;
				r.mouseMove(savedMousePos.x, savedMousePos.y);
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
