import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

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

        boolean leftPressed, rightPressed, jumpPressed;

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                jumpPressed = true;
            }
        }

        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                leftPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                rightPressed = false;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                jumpPressed = false;
            }
        }

        public void keyTyped(KeyEvent e) {
            // if we hit escape, exit
            if (e.getKeyChar() == 27) {
                System.exit(0);
            }
        }
    }

    private MouseListener ml;
    private KeyListener kl;

    public InputListener(Component c) {
        ml = new MouseListener();
        kl = new KeyListener();
        c.addMouseListener(ml);
        c.addMouseMotionListener(ml);
        c.addKeyListener(kl);
    }

    public int getMouseX() {
        return ml.mouseX;
    }

    public int getMouseY() {
        return ml.mouseY;
    }

    public boolean mouseDown() {
        return ml.mouseDown;
    }

    public boolean leftPressed() {
        return kl.leftPressed;
    }

    public boolean rightPressed() {
        return kl.rightPressed;
    }

    public boolean jumpPressed() {
        return kl.jumpPressed;
    }
}