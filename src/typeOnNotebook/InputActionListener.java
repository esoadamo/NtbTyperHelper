package typeOnNotebook;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class InputActionListener implements NativeKeyListener, NativeMouseInputListener {
	private boolean pressedShortcutKeys[] = null;
	private boolean initCompted = false;

	/**
	 * Initializes shortcut pressing
	 * 
	 * @throws Exception
	 *             you may call this function only once!
	 */
	public void init() throws Exception {
		if (initCompted)
			throw new Exception("Init is already complted");
		pressedShortcutKeys = new boolean[App.keyboardShortcutModeSwitch.length];
		for (int i = 0; i < pressedShortcutKeys.length; i++)
			pressedShortcutKeys[i] = false;
	}

	/**
	 * Looks for pressing of the key shortcut and if so, switches the mode
	 */
	@Override
	public void nativeKeyPressed(NativeKeyEvent e) {
		final int pressedCode = e.getKeyCode();
		for (int i = 0; i < App.keyboardShortcutModeSwitch.length; i++)
			if (App.keyboardShortcutModeSwitch[i] == pressedCode) {
				pressedShortcutKeys[i] = true;
				break;
			}
		for (boolean keyIsPressed : pressedShortcutKeys)
			if (!keyIsPressed)
				return;
		// OK, all shortcut keys are pressed
		// Reset shortcut pressing upon executing
		for (int i = 0; i < pressedShortcutKeys.length; i++)
			pressedShortcutKeys[i] = false;
		if (App.isDisabled())
			App.setMode(AppMode.ENABLED);
		else
			App.setMode(AppMode.DISABLED);
	}

	/**
	 * Move mouse away from screen Remove pressed keyboard shortcut key if one was
	 * released
	 */
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
		final int pressedCode = e.getKeyCode();
		for (int i = 0; i < App.keyboardShortcutModeSwitch.length; i++)
			if (App.keyboardShortcutModeSwitch[i] == pressedCode) {
				pressedShortcutKeys[i] = false;
				break;
			}
		if (!App.isDisabled())
			MouseHider.hide();
	}

	/**
	 * If the mouse is hidden, move it back to its hidden position
	 */
	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		if (MouseHider.isHidden())
			MouseHider.moveToHiddenPos();
	}

	// Unused interface voids

	public void nativeMouseClicked(NativeMouseEvent e) {
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
	}

	public void nativeMousePressed(NativeMouseEvent e) {
	}

	public void nativeMouseReleased(NativeMouseEvent e) {
	}

	public void nativeMouseDragged(NativeMouseEvent e) {
	}
}