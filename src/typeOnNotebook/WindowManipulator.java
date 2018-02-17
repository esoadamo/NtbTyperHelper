package typeOnNotebook;

import java.awt.Rectangle;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

public class WindowManipulator {
	private static final int MAX_TITLE_LENGTH = 2048;

	/**
	 * Gets the name of the current active window
	 * 
	 * @return name of the current active window
	 */
	public static String getCurrentWindowName() {
		char[] buffer = new char[MAX_TITLE_LENGTH * 2];
		HWND hwnd = User32.INSTANCE.GetForegroundWindow();
		User32.INSTANCE.GetWindowText(hwnd, buffer, MAX_TITLE_LENGTH);

		return Native.toString(buffer);
	}

	/**
	 * Gets position and size of current active window
	 * 
	 * @return position and size of current active window
	 */
	public static Rectangle getCurrentWindowRect() {
		switch (App.os) {
		case WINDOWS:
			HWND hwnd = User32.INSTANCE.GetForegroundWindow();
			RECT rect = new RECT();
			User32.INSTANCE.GetWindowRect(hwnd, rect);
			return new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
		case LINUX:
			// On Linux there is no Java way to do this, so we have to run a few commands
			// and parse the results for ourselves
			String[] sa = CommandRunner.getOutput("xprop -root").split("ACTIVE", 3)[1].split("\n", 2)[0].split(" ");
			final String activeWindowId = sa[sa.length - 1];
			final String commandOuputString = CommandRunner.getOutput("xwininfo -id " + activeWindowId);
			final int x = Integer
					.parseInt(commandOuputString.split("Absolute upper-left X:  ", 2)[1].split("\n", 2)[0]);
			final int y = Integer
					.parseInt(commandOuputString.split("Absolute upper-left Y:  ", 2)[1].split("\n", 2)[0]);
			final int width = Integer.parseInt(commandOuputString.split("Width: ", 2)[1].split("\n", 2)[0]);
			final int height = Integer.parseInt(commandOuputString.split("Height: ", 2)[1].split("\n", 2)[0]);
			return new Rectangle(x, y, width, height);
		default:
			return null;
		}
	}
}