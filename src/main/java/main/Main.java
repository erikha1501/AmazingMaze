package main;

import java.io.*;
import java.awt.event.*;

import maze.*;
import maze.gui.*;

/**
 * main class for the program
 *
 * @author ncameron
 */
public class Main {

    /**
     * The main method for this program
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {

        String fileName = null; // name of input file to read
        int width = 25; // default width of board to create
        int height = 25; // default height of board to create
        Mode mode = Mode.LEFT;
        boolean useStepWalker = false;

        // ======================================================
        // ======== First, parse command-line arguments ========
        // ======================================================

        for (int i = 0; i != args.length; ++i) {
            if (args[i].startsWith("-")) {
                String arg = args[i];
                if (arg.equals("-file")) {
                    fileName = args[++i];
                } else if (arg.equals("-width")) {
                    width = Integer.parseInt(args[++i]);
                } else if (arg.equals("-height")) {
                    height = Integer.parseInt(args[++i]);
                } else if (arg.equals("-keyw")) {
                    mode = Mode.KEY;
                } else if (arg.equals("-leftw")) {
                    mode = Mode.LEFT;
                } else if (arg.equals("-randomw")) {
                    mode = Mode.RANDOM;
                } else if (arg.equals("-smart")) {
                    mode = Mode.SMART;
                } else if (arg.equals("-stepw")) {
                    useStepWalker = true;
                } else {
                    throw new RuntimeException("Unknown option: " + args[i]);
                }
            }
        }


        // ======================================================
        // ======= Second, create the board to be searched ======
        // ======================================================

        Board board;
        if (fileName != null) {
            board = new Board(new FileReader(fileName));
        } else {
            board = new Board(width, height);
        }

        // ======================================================
        // ====== Third, create the walker to walk the maze =====
        // ======================================================

        Walker walker = null;
        switch (mode) {
            case RANDOM:
                walker = new RandomWalker();
                break;
            case KEY:
                walker = new KeyWalker();
                break;
            case LEFT:
                walker = new LeftWalker();
                break;
            case SMART:
                walker = new SmarterLeftWalker();
                break;
        }

        // ======================================================
        // ============== Fourth, show the GUI ==================
        // ======================================================

        // Initialise the GUI and put it on the screen
        MazeWindow.getWindowAndShow(board);

        if (useStepWalker) {
            // Wrap current walker inside StepWalker
            walker = new StepWalker(board, walker);
        }

        // Now, register the key walker
        if (walker instanceof KeyListener) {
            MazeWindow.mainWindow.addKeyListener((KeyListener) walker);
        }

        // ======================================================
        // ================== Fifth, solve the maze =============
        // ======================================================

        long time = System.currentTimeMillis(); // record start time

        // Solve the board automatically if StepWalker isn't used
        if (!useStepWalker) {
            walker.solve(board);

            time = System.currentTimeMillis() - time; // subtract start time from current time
            System.out.println("Maze solved by " + walker.getName() + " in " + time + "ms");
            System.out.println("Solution has " + board.getPath().getSteps() + " steps.");
        }
    }
}

enum Mode {RANDOM, KEY, LEFT, SMART}
