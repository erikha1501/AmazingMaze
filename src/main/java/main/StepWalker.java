package main;

import java.util.*;

import maze.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * An implementation of <code>Walker</code> that allows user
 * to control another <code>Walker</code>.
 */
public class StepWalker extends Walker implements KeyListener {

    private final Board board;
    private final Walker walker;
    private int stepKeyCode;

    public StepWalker(Board board, Walker walker) {
        super(String.format("Step Walker (%s)", walker.getName()));
        this.board = board;
        this.walker = walker;
        this.stepKeyCode = KeyEvent.VK_Q;
    }

    /**
     * Create new <code>StepWalker</code>
     * @param keyCode   a virtual key code (VK_*) defined in <code>KeyEvent</code>
     */
    public StepWalker(Board board, Walker walker, int keyCode) {
        this(board, walker);
        stepKeyCode = keyCode;
    }

    protected Direction move(View v) {
        return null;
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == stepKeyCode) {
            if (!board.isSolved()) {
                walker.step(board);
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}