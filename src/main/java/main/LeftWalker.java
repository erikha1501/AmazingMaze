package main;

import maze.*;
import java.util.HashMap;
import java.util.Objects;

/**
 * An any implementation of the left walker, which you need to complete
 * following the notes.
 */
public class LeftWalker extends Walker {

	private class Coord {
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

    private static final Direction[] intToDirectionMapping =
            {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

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

    private boolean locateLeftWall(View v) {
        // Start from current direction and turn clockwise until we find a wall on our left.
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

    private int findNextMove(View v, int squareExitInfo) {
    	// Start looking for an open path to the left.
		// Turn clockwise until found one.

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

    private boolean mayMove(View v, int currentDirection) {
        return v.mayMove(getDirection(currentDirection));
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