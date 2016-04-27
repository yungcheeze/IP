package game.Main;
import game.interfaces.Piece;
import game.interfaces.PlayerInterface;

public class Main {

	public static void main(String[] args) throws Exception {
	       GameManager game = new GameManager();
	       PlayerInterface player1 = new HumanPlayer();
	       PlayerInterface player2 = new ComputerPlayer();
	       
	       game.boardSize(5,5);
	       game.specifyPlayer(player1, Piece.RED);
	       game.specifyPlayer(player2, Piece.BLUE);
	       
	       game.playGame();
	}
}
