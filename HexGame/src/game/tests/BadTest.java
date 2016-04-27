package game.tests;

import game.Main.Board;
import game.Main.Move;
import game.interfaces.BoardAlreadySizedException;
import game.interfaces.InvalidBoardSizeException;
import game.interfaces.InvalidColourException;
import game.interfaces.InvalidPositionException;
import game.interfaces.NoBoardDefinedException;
import game.interfaces.Piece;
import game.interfaces.PositionAlreadyTakenException;

public class BadTest 
{
	
	public static void main(String[] args) throws InvalidBoardSizeException, BoardAlreadySizedException, NoBoardDefinedException, InvalidPositionException, PositionAlreadyTakenException, InvalidColourException {
		Board board = new Board();

		board.setBoardSize(5, 5);
//		board.gameWon();
		
		Move m = new Move();
		m.setPosition(3, 0);
		board.placePiece(Piece.RED, m);
		
//		m.setPosition(0, 4);
//		board.placePiece(Piece.RED, m);
//		
//		m.setPosition(4, 3);
//		board.placePiece(Piece.BLUE, m);
//		
		m.setPosition(3, 0);
		board.placePiece(Piece.BLUE, m);
//		
//		BoardViewGenerator view = new BoardViewGenerator(board.getBoardView());
//		view.PrintBoard();
		
		
	}

}
