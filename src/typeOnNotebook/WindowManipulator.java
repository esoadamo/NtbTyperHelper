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
     * @return position and size of current active window
     */
    public static Rectangle getCurrentWindowRect() {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        RECT rect = new RECT();
    	User32.INSTANCE.GetWindowRect(hwnd, rect);
    	
    	return new Rectangle(rect.left, rect.top, rect.right - rect.left, rect.bottom - rect.top);
    }
 }