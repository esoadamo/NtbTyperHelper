package typeOnNotebook;

import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;

public class InputActionListener implements NativeKeyListener, NativeMouseInputListener {

	/**
	 * Move mouse away from screen
	 */
	@Override
	public void nativeKeyReleased(NativeKeyEvent e) {
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

	public void nativeKeyPressed(NativeKeyEvent e) {
	}

	public void nativeMousePressed(NativeMouseEvent e) {
	}

	public void nativeMouseReleased(NativeMouseEvent e) {
	}

	public void nativeMouseDragged(NativeMouseEvent e) {
	}
}