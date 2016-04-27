package game.tests;

import game.AuxClasses.BoardViewGenerator;
import game.Main.Board;
import game.Main.Move;
import game.graphs.ComputerBoardGraph;
import game.graphs.ComputerVertex;
import game.interfaces.BoardAlreadySizedException;
import game.interfaces.InvalidBoardSizeException;
import game.interfaces.InvalidColourException;
import game.interfaces.InvalidPositionException;
import game.interfaces.NoBoardDefinedException;
import game.interfaces.Piece;
import game.interfaces.PositionAlreadyTakenException;

public class Test {

	public static void main(String[] args) throws InvalidBoardSizeException, BoardAlreadySizedException,
			NoBoardDefinedException, InvalidPositionException, PositionAlreadyTakenException, InvalidColourException {
		Board board = new Board();
		board.setBoardSize(5, 5);

		Move m = new Move();
		// m.setPosition(0, 0);
		// board.placePiece(Piece.RED, m);
		// m.setPosition(0, 1);
		// board.placePiece(Piece.RED, m);
		// m.setPosition(0, 2);
		// board.placePiece(Piece.RED, m);
		// /*m.setPosition(0, 3);
		// board.placePiece(Piece.RED, m);*/
		// m.setPosition(0, 4);
		// board.placePiece(Piece.RED, m);
		//
		// m.setPosition(4, 3);
		// board.placePiece(Piece.BLUE, m);
		// m.setPosition(2, 2);
		// board.placePiece(Piece.BLUE, m);
		// m.setPosition(3, 3);
		// board.placePiece(Piece.BLUE, m);
		// m.setPosition(3, 2);
		// board.placePiece(Piece.BLUE, m);
		// m.setPosition(1, 3);
		// board.placePiece(Piece.BLUE, m);
		// /*m.setPosition(0, 3);
		// board.placePiece(Piece.BLUE, m);*/
		// m.setPosition(1, 2);
		// board.placePiece(Piece.BLUE, m);

		int[][] tests = new int[6][2];
		int x = 2;
		int y = 2;

		tests[0] = new int[] { x + 1, y + 1 };
		tests[1] = new int[] { x - 1, y + 2 };
		tests[2] = new int[] { x - 2, y + 1 };
		tests[3] = new int[] { x - 1, y - 1 };
		tests[4] = new int[] { x + 1, y - 2 };
		tests[5] = new int[] { x + 2, y - 1 };

		for (int[] test : tests) {
			int xtemp = test[0];
			int ytemp = test[1];

			try {
				m.setPosition(xtemp, ytemp);
				board.placePiece(Piece.RED, m);

			} catch (InvalidPositionException e) {
				continue;
			}

		}
		BoardViewGenerator view = new BoardViewGenerator(board.getBoardView());
		view.PrintBoard();

		// Piece p = board.gameWon();
		//
		// System.out.println(p + " has won!");

	}

}
