

public class Main {

	public static void main(String[] args) throws Exception {
	       GameManager game = new GameManager();
	       PlayerInterface player1 = new ComputerPlayer_nbgt74();
	       PlayerInterface player2 = new HumanPlayer();
	       
	       game.boardSize(11,11);
	       game.specifyPlayer(player1, Piece.RED);
	       game.specifyPlayer(player2, Piece.BLUE);
	       
	       game.playGame();
	}
}
