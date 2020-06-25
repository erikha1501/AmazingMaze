package main;

import maze.*;

import java.util.Objects;
import java.util.HashMap;
import java.util.Stack;

/**
 * Implementation of <code>Walker</code> that follows the nearest
 * left wall.
 */
public class SmarterLeftWalker extends LeftWalker {

    private static class SquareSnapshot {
        public final int takenDirection;
        public final int facingDirection;

        public SquareSnapshot(int takenDirection, int facingDirection) {
            this.takenDirection = takenDirection;
            this.facingDirection = facingDirection;
        }
    }

    private final Stack<SquareSnapshot> visitedSquareHistory = new Stack<SquareSnapshot>();

    public SmarterLeftWalker() {
        super("Smarter Left Walker");
    }

    @Override
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

            // Change facing direction towards the open path.
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


}
