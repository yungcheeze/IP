



import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class GameManager implements GameManagerInterface {

	private BoardInterface board;
	private HashMap<Piece, PlayerInterface> players;

	public GameManager() {
		board = new Board();
		players = new LinkedHashMap<Piece, PlayerInterface>();
	}

	@Override
	public boolean specifyPlayer(PlayerInterface player, Piece colour)
			throws ColourAlreadySetException {
		if (players.containsKey(colour))
			throw new ColourAlreadySetException();

		try {
			player.setColour(colour);
		} catch (InvalidColourException e) {
			System.out.println("Invalid colour entered");
			return false;
		}
		players.put(colour, player);

		return true;
	}

	@Override
	public boolean boardSize(int sizeX, int sizeY)
			throws InvalidBoardSizeException, BoardAlreadySizedException {
		int max = Integer.MAX_VALUE;
		int min = Integer.MIN_VALUE;
		if (sizeX > max || sizeX < min || sizeY > max || sizeY < min) {
			System.out.println("Integer out of range.");
			return false;
		}
		board.setBoardSize(sizeX, sizeY);

		return true;
	}

	@Override
	public boolean playGame() {
		// TODO Auto-generated method stub

		// ensure both players defined
		if (players.size() < 2)
			return false;

		try {
			// main game loop
			boolean playerConceded = false;
			Piece concededPlayer = Piece.UNSET;
			while (board.gameWon().equals(Piece.UNSET) && !playerConceded) {
				for (Map.Entry<Piece, PlayerInterface> entry : players
						.entrySet()) {
					PlayerInterface player = entry.getValue();
					Piece colour = entry.getKey();
					Piece[][] boardView = board.getBoardView();

					boolean movePlaced = false;
					while (!movePlaced) {
						Move move = (Move) player.makeMove(boardView);
						if (move.hasConceded()) {
							playerConceded = true;
							concededPlayer = colour;
							break;
						}
						movePlaced = placePiece(colour, move);
					}

					if (!board.gameWon().equals(Piece.UNSET) || playerConceded)
						break;
				}
			}

			// end of game sequence
			// set winner and loser
			Piece winner;
			Piece loser;
			if (playerConceded) {
				if (concededPlayer.equals(Piece.RED))
					winner = Piece.BLUE;
				else
					winner = Piece.RED;
			} else
				winner = board.gameWon();

			switch (winner) {
			case RED:
				loser = Piece.BLUE;
				break;
			case BLUE:
				loser = Piece.RED;
				break;
			default:
				System.out.println("Winner could not be determined");
				return false;
			}

			// inform players of final game state
			if (!winner.equals(Piece.UNSET)) {
				boolean result; // result of calling final game state
				result = players.get(winner).finalGameState(GameState.WON);
				if (result)
					result = players.get(loser).finalGameState(GameState.LOST);
				if (!result) {
					System.out.println("Winner could not be determined");
					return false;
				}
			}

		} catch (NoBoardDefinedException e) {
			System.out.println("No Board defined.");
			return false;
		} catch (NoValidMovesException e) {
			System.out
					.println("All positions taken. No more moves can be made.");
			return false;
		} catch (InvalidColourException e) {
			System.out.println("One of the players has an Invalid Colour.");
			return false;
		}
		return true;
	}

	private boolean placePiece(Piece colour, Move move)
			throws NoBoardDefinedException, InvalidColourException {
		try {
			board.placePiece(colour, move);

		} catch (PositionAlreadyTakenException e) {
			System.out
					.println("This position is alrady taken. Restarting move prompt...");
			return false;
		} catch (InvalidPositionException e) {
			System.out
					.println("This position is invalid. Restarting move prompt...");
			return false;
		}

		return true;
	}

}
