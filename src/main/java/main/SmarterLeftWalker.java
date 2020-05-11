package main;

import maze.*;

import java.util.Objects;
import java.util.HashMap;
import java.util.Stack;

/**
 * Implementation of <code>Walker</code> that follows the nearest
 * left wall.
 */
public class SmarterLeftWalker extends Walker {

    /**
     * Represents 2D coordinates. Consists of a pair of integers.
     * <p>
     * Used in <code>visitedSquareLookup</code>
     */
    private static class Coord {
        private final int x;
        private final int y;

        public Coord(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coord coord = (Coord) o;
            return x == coord.x &&
                    y == coord.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private static class SquareInfo {
        public int exitInfo;

        public SquareInfo(int exitInfo) {
            this.exitInfo = exitInfo;
        }

        public void updateExitInfo(int usedDirection) {
            exitInfo = setUsedExitDirection(exitInfo, usedDirection);
        }
    }

    private static class SquareSnapshot {
        public final int takenDirection;
        public final int facingDirection;

        public SquareSnapshot(int takenDirection, int facingDirection) {
            this.takenDirection = takenDirection;
            this.facingDirection = facingDirection;
        }
    }

    /**
     * An array lookup which maps integer directions into <code>Direction</code>s.
     * <p>
     * Internally <code>LeftWalker</code> uses integers for directions. This makes
     * calculations (find left, right) easier.
     */
    private static final Direction[] intToDirectionMapping =
            {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

    /**
     * A dictionary holding information about visited squares.
     * <p>
     * Key: a <code>Coord</code>
     * <p>
     * Value: an <code>Integer</code> storing previously used exits
     */
    private final HashMap<Coord, SquareInfo> visitedSquareLookup = new HashMap<Coord, SquareInfo>();
    private final Stack<SquareSnapshot> visitedSquareHistory = new Stack<SquareSnapshot>();

    private boolean isFollowingLeft = false;
    private int facingDirection = 0;

    private int posX = 0;
    private int posY = 0;

    public SmarterLeftWalker() {
        super("Smarter Left Walker");
    }

    protected Direction move(View v) {

        Coord coord = new Coord(posX, posY);

        // Get info about current square.
        // Prepare a new one if this is the first entrance.
        SquareInfo squareInfo = visitedSquareLookup.get(coord);

        if (squareInfo == null) {
            squareInfo = new SquareInfo(0);
            visitedSquareLookup.put(coord, squareInfo);
        }

        if (visitedSquareHistory.size() > 0) {
            SquareSnapshot previousSquareSnapshot = visitedSquareHistory.peek();
            squareInfo.updateExitInfo(oppositeOf(previousSquareSnapshot.takenDirection));
        }

        // Begin finding next move.
        if (!isFollowingLeft) {
            // Try to find a left wall to follow.
            if (locateLeftWall(v)) {
                isFollowingLeft = true;
            }
        }

        int nextMoveDirection = findNextMove(v, squareInfo.exitInfo);

        if (nextMoveDirection >= 0) {

            // Before moving to the next square, mark next move direction as used.
            squareInfo.updateExitInfo(nextMoveDirection);

            // Save snapshot.
            SquareSnapshot newSquareSnapshot = new SquareSnapshot(nextMoveDirection, facingDirection);
            visitedSquareHistory.push(newSquareSnapshot);

            facingDirection = nextMoveDirection;
        } else {

            // Try to backtrack.
            if (visitedSquareHistory.size() > 0) {
                SquareSnapshot squareSnapshot = visitedSquareHistory.pop();
                facingDirection = squareSnapshot.facingDirection;
                nextMoveDirection = oppositeOf(squareSnapshot.takenDirection);
            }
        }

        if (nextMoveDirection >= 0) {

            updatePos(nextMoveDirection);
            return getDirection(nextMoveDirection);
        } else {

            System.out.println("Cannot find next move");
            return null;
        }
    }

    /**
     * Try to find a left wall. Rotates 90 degrees clock wise until a wall is found.
     *
     * @param v a <code>View</code> into current maze
     * @return a boolean indicating whether a left wall found
     */
    private boolean locateLeftWall(View v) {

        int initialProbingDirection = leftOf(facingDirection);
        int probingDirection = initialProbingDirection;

        while (true) {
            // Check if a wall found.
            if (!mayMove(v, probingDirection)) {

                // Change facing direction so that the wall is on our left.
                facingDirection = rightOf(probingDirection);
                return true;
            }

            // Continue searching.
            probingDirection = rightOf(probingDirection);

            // Done probing and no walls found.
            if (probingDirection == initialProbingDirection) {
                return false;
            }
        }
    }

    /**
     * Find next possible move.
     * <p>
     * Depending on left-following state, <code>findNextMove</code> will either choose
     * left-most exit or step forward.
     *
     * @param v              a <code>View</code> into current maze
     * @param squareExitInfo used exits
     * @return next possible direction, -1 if can't find any
     */
    private int findNextMove(View v, int squareExitInfo) {

        int initialProbingDirection = isFollowingLeft ? leftOf(facingDirection) : facingDirection;
        int probingDirection = initialProbingDirection;

        while (true) {

            // Check if an open path found.
            if (mayMove(v, probingDirection)) {

                // Check if this exit has been taken before.
                if (exitDirectionUsed(squareExitInfo, probingDirection)) {

                    // Choosing another exit breaks left-following state.
                    isFollowingLeft = false;
                } else {

                    return probingDirection;
                }
            }

            // Continue searching.
            probingDirection = rightOf(probingDirection);

            // Done probing and no open path found.
            if (probingDirection == initialProbingDirection) {
                return -1;
            }
        }
    }

    private void updatePos(int direction) {
        switch (direction) {
            case 0:
                posY++;
                break;
            case 1:
                posX++;
                break;
            case 2:
                posY--;
                break;
            case 3:
                posX--;
                break;
        }
    }

    /**
     * A helper method checking if <code>direction</code> can be taken.
     *
     * @param v         a <code>View</code> into current maze
     * @param direction direction to be checked
     * @return whether <code>direction</code> can be taken
     */
    private boolean mayMove(View v, int direction) {
        return v.mayMove(getDirection(direction));
    }

    private static int leftOf(int direction) {
        return (direction + 3) % 4;
    }

    private static int rightOf(int direction) {
        return (direction + 1) % 4;
    }

    private static int oppositeOf(int direction) {
        return (direction + 2) % 4;
    }

    private static int directionFromToCoord(Coord from, Coord to) {
        int dx = to.x - from.x;
        int dy = to.y - from.y;

        if (dx == 1) {
            return 1;
        }
        if (dx == -1) {
            return 3;
        }
        if (dy == 1) {
            return 0;
        }
        if (dy == -1) {
            return 2;
        }

        return -1;
    }

    private static boolean exitDirectionUsed(int squareExitInfo, int direction) {
        // Basically check if bit representing 'direction' is set.
        return (squareExitInfo & (1 << direction)) != 0;
    }

    private static int setUsedExitDirection(int squareExitInfo, int direction) {
        // Set bit representing 'direction'.
        return squareExitInfo | (1 << direction);
    }

    private static Direction getDirection(int direction) {
        return intToDirectionMapping[direction];
    }
}
