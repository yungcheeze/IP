package game.Main;

import game.graphs.BoardGraph;
import game.graphs.PathFinder;
import game.interfaces.BoardAlreadySizedException;
import game.interfaces.InvalidBoardSizeException;
import game.interfaces.InvalidColourException;
import game.interfaces.InvalidPositionException;
import game.interfaces.MoveInterface;
import game.interfaces.NoBoardDefinedException;
import game.interfaces.Piece;
import game.interfaces.PositionAlreadyTakenException;
import game.interfaces.BoardInterface;;

public class Board implements BoardInterface {
	// private int sizeX;
	// private int sizeY;
	private Piece[][] boardArray;

	public Board() {

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public boolean setBoardSize(int sizeX, int sizeY) throws InvalidBoardSizeException, BoardAlreadySizedException {
		boolean toReturn = false;
		if (boardArray != null) {
			throw new BoardAlreadySizedException();
		} else if (sizeX < 1 || sizeY < 1) {
			throw new InvalidBoardSizeException();
		} else {
			buildBoard(sizeX, sizeY);
			toReturn = true;
		}
		return toReturn;
	}
	
	// sets each entry in the board to Unset
	private void buildBoard(int sizeX, int sizeY) {
		
		boardArray = new Piece[sizeX][sizeY];
		for (int x = 0; x < boardArray.length; x++) {
			for (int y = 0; y < boardArray[0].length; y++)
				boardArray[x][y] = Piece.UNSET;
		}
	}

	public Piece[][] getBoardView() throws NoBoardDefinedException {
		if (boardArray == null)
			throw new NoBoardDefinedException();
		return boardArray;
	}

	public boolean placePiece(Piece colour, MoveInterface move) throws PositionAlreadyTakenException,
			InvalidPositionException, InvalidColourException, NoBoardDefinedException {
		// TODO may possibly need to add NoBoardDefinedException
		boolean success = false;
		int xPos = move.getXPosition();
		int yPos = move.getYPosition();

		if (colour.equals(Piece.UNSET))
			throw new InvalidColourException();
		if (boardArray != null) {
			if (xPos >= boardArray.length || yPos >= boardArray[0].length)
				throw new InvalidPositionException();

			if (!boardArray[xPos][yPos].equals(Piece.UNSET))
				throw new PositionAlreadyTakenException();
			else {
				boardArray[xPos][yPos] = colour;
				success = true;
			}
		} else {
			throw new NoBoardDefinedException();
		}

		return success;
	}

	public Piece gameWon() throws NoBoardDefinedException {
		Piece result = Piece.UNSET;

		if (boardArray == null)
			throw new NoBoardDefinedException();

		BoardGraph boardGraph = new BoardGraph(getBoardView());

		PathFinder pathFinder = new PathFinder(boardGraph.getAdjacencyList(), boardGraph.getHomes());

		try {

			if (pathFinder.pathFound(Piece.RED))
				result = Piece.RED;
			else if (pathFinder.pathFound(Piece.BLUE))
				result = Piece.BLUE;

		} catch (InvalidColourException e) {
			
			e.printStackTrace();
		}

		return result;
	}

}
