package main;

import java.util.*;

import maze.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * An implementation of <code>Walker</code> that follows user input.
 */
public class KeyWalker extends Walker implements KeyListener {

    private Direction currentDirection = null;

    public KeyWalker() {
        super("Key Walker");
    }

    protected Direction move(View v) {

        Direction direction = currentDirection;
        // Reset current direction.
        currentDirection = null;
        return direction;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                currentDirection = Direction.NORTH;
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                currentDirection = Direction.WEST;
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                currentDirection = Direction.SOUTH;
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                currentDirection = Direction.EAST;
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) {
        currentDirection = null;
    }
}