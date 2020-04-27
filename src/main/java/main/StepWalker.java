package main;

import java.util.*;

import maze.*;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * An any implementation of the key walker, which you need to complete
 * following the notes.
 */
public class StepWalker extends Walker implements KeyListener {

    private Board board;
    private Walker walker = new LeftWalker();

    public StepWalker(Board board, Walker walker) {
        super("Key Walker");
        this.board = board;
        this.walker = walker;
    }

    protected Direction move(View v) {
        return null;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_Q:
                walker.step(board);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}