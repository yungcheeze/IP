package game.Main;

import game.interfaces.InvalidPositionException;
import game.interfaces.MoveInterface;

public class Move implements MoveInterface {
	private int x;
	private int y;
	private boolean conceded;

	public Move() {
		conceded = false;
	}

	@Override
	public boolean setPosition(int x, int y) throws InvalidPositionException {
		boolean toReturn = false;

		if (x < 0 || y < 0) {
			// x or y being negative results in an invalid coordinate
			throw new InvalidPositionException();
		} else {
			this.x = x;
			this.y = y;
			toReturn = true;
		}
		return toReturn;
	}

	@Override
	public boolean hasConceded() {
		return conceded;
	}

	@Override
	public int getXPosition() {
		return x;
	}

	@Override
	public int getYPosition() {
		return y;
	}

	@Override
	public boolean setConceded() {
		// TODO when would I return false
		conceded = true;
		return true;
	}

}
