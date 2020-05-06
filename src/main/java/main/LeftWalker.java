package main;

import maze.*;

import java.util.HashMap;
import java.util.Objects;

/**
 * Implementation of <code>Walker</code> that follows the nearest
 * left wall.
 */
public class LeftWalker extends Walker {

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
    private final HashMap<Coord, Integer> visitedSquareLookup = new HashMap<Coord, Integer>();

    private boolean isFollowingLeft = false;
    private int facingDirection = 0;

    private int posX = 0;
    private int posY = 0;

    public LeftWalker() {
        super("Left Walker");
    }

    protected Direction move(View v) {

        if (!isFollowingLeft) {
            // Try to find a left wall to follow.
            if (locateLeftWall(v)) {
                isFollowingLeft = true;
            } else {
                // Take a step NORTH if no wall found.
                return Direction.NORTH;
            }
        }

        Coord coord = new Coord(posX, posY);

        // Find info about past entry of the current square.
        int squareExitInfo = visitedSquareLookup.getOrDefault(coord, 0);
        int nextMoveDirection = findNextMove(v, squareExitInfo);

        if (nextMoveDirection < 0) {
            System.out.println("Cannot find next move");
            return null;
        } else {
            // Before moving to the next square, mark the current location as visited.
            squareExitInfo = setUsedExitDirection(squareExitInfo, nextMoveDirection);
            visitedSquareLookup.put(coord, squareExitInfo);

            // Update current pos.
            updatePos(nextMoveDirection);

            return getDirection(nextMoveDirection);
        }
    }

    /**
     * Try to find a left wall. Rotates 90 degrees clock wise until a wall is found.
     * @param   v    a <code>View</code> into current maze
     * @return       a boolean indicating whether a left wall found
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
     * Start looking for an open path to the left.
     * Turn clockwise until found one.
     * @param v                 a <code>View</code> into current maze
     * @param squareExitInfo    used exits
     * @return                  next possible direction, -1 if can't find any
     */
    private int findNextMove(View v, int squareExitInfo) {

        int initialProbingDirection = leftOf(facingDirection);
        int probingDirection = initialProbingDirection;

        while (true) {

            // Check if an open path found.
            if (mayMove(v, probingDirection)) {

                // Check if this exit has been taken before.
                if (exitDirectionUsed(squareExitInfo, probingDirection)) {

                    // Choosing another exit breaks left-following state.
                    isFollowingLeft = false;
                } else {

                    // Change facing direction towards the open path.
                    facingDirection = probingDirection;
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
     * @param v                 a <code>View</code> into current maze
     * @param direction         direction to be checked
     * @return                  whether <code>direction</code> can be taken
     */
    private boolean mayMove(View v, int direction) {
        return v.mayMove(getDirection(direction));
    }

    private int leftOf(int currentDirection) {
        return (currentDirection + 3) % 4;
    }

    private int rightOf(int currentDirection) {
        return (currentDirection + 1) % 4;
    }

    private boolean exitDirectionUsed(int squareExitInfo, int direction) {
        // Basically check if bit representing 'direction' is set.
        return (squareExitInfo & (1 << direction)) != 0;
    }

    private int setUsedExitDirection(int squareExitInfo, int direction) {
        // Set bit representing 'direction'.
        return squareExitInfo | (1 << direction);
    }

    private Direction getDirection(int direction) {
        return intToDirectionMapping[direction];
    }
}