import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class InputListener {

    private class MouseListener extends MouseInputAdapter {
        boolean mouseDown;
        int mouseX, mouseY;

        public void mousePressed(MouseEvent e) {
            mouseDown = true;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDown = false;
        }

        public void mouseMoved(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }

        public void mouseDragged(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }
    
    private class KeyListener extends KeyAdapter {
        HashMap<Integer, Boolean> keyPressed = new HashMap<Integer, Boolean>();
        HashMap<Integer, Boolean> keyTyped = new HashMap<Integer, Boolean>();

        public void keyPressed(KeyEvent e) {
            keyPressed.put(e.getKeyCode(), true);
        }

        public void keyReleased(KeyEvent e) {
            keyPressed.put(e.getKeyCode(), false);
            keyTyped.put(e.getKeyCode(), true);
        }

        public void keyTyped(KeyEvent e) {
            // if we hit escape, exit
            if (e.getKeyChar() == 27) {
                System.exit(0);
            }
        }

        /* User-facing functions begin here */

        // returns true if the given key is down.
        public boolean isKeyDown(int keyCode) {
            Boolean result = keyPressed.get(keyCode);
            if (result == null) {
                return false;
            } else {
                return (boolean) result;
            }
        }

        // returns true if the key has been typed (i.e. pressed and then released).
        // not idempotent--if this returns true, the next call won't return true until
        // the key is typed again.
        public boolean getKeyTyped(int keyCode) {
            Boolean result = keyTyped.get(keyCode);
            if (result == null) {
                return false;
            } else {
                if (result) {
                    keyTyped.put(keyCode, false);
                    return true;
                } else {
                    return false;
                }
            }
        }

        public void clearKeyTyped(int keyCode) {
            keyTyped.put(keyCode, false);
        }

        public void clearAllKeysTyped() {
            keyTyped.clear();
        }

    }

    public static final int LEFT = KeyEvent.VK_LEFT;
    public static final int RIGHT = KeyEvent.VK_RIGHT;
    public static final int UP = KeyEvent.VK_UP;
    public static final int DOWN = KeyEvent.VK_DOWN;
    public static final int JUMP = KeyEvent.VK_SPACE;
    public static final int MODE_SWITCH = KeyEvent.VK_ENTER;
    public static final int SAVE = KeyEvent.VK_S;

    private MouseListener ml;
    private KeyListener kl;

    public InputListener(Component c) {
        ml = new MouseListener();
        kl = new KeyListener();
        c.addMouseListener(ml);
        c.addMouseMotionListener(ml);
        c.addKeyListener(kl);
    }

    public int getMouseX() { return ml.mouseX; }

    public int getMouseY() { return ml.mouseY; }

    public boolean mouseDown() { return ml.mouseDown; }

    public boolean isKeyDown(int keyCode) { return kl.isKeyDown(keyCode); }

    public boolean getKeyTyped(int keyCode) { return kl.getKeyTyped(keyCode); }

    public void clearKeyTyped(int keyCode) { kl.clearKeyTyped(keyCode); }

    public void clearAllKeysTyped() { kl.clearAllKeysTyped(); }

}