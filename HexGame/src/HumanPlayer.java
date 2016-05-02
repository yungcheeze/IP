


import java.util.Scanner;

public class HumanPlayer implements PlayerInterface {

	private Piece colour;
	private Scanner sc;
	private GameState gameState;

	// private int xLim;
	// private int yLim;

	public HumanPlayer() {
		colour = Piece.UNSET;
		sc = new Scanner(System.in);
		gameState = GameState.INCOMPLETE;
	}

	@Override
	public MoveInterface makeMove(Piece[][] boardView)
			throws NoValidMovesException {

		if (!containsValidMoves(boardView)
				|| !gameState.equals(GameState.INCOMPLETE))
			throw new NoValidMovesException();

		Move playerMove = new Move();
		boolean moveReady = false;
		while (!moveReady) {
			System.out.println("***" + colour + " to play***");
			showBoard(boardView);
			printCommandPrompt();

			String commandString = sc.nextLine();

			if (commandString.trim().toUpperCase().equals("Q")) {
				boolean confirmed = confirmConcession();
				if (confirmed)
					moveReady = playerMove.setConceded();
			} else {

				if (isValidMoveString(commandString)) {
					int[] coordinates = getCoordinates(commandString);
					moveReady = setCoordinates(playerMove, coordinates);
				}
			}
		}

		return playerMove;

	}

	// ******************************************************************************************************************
	// Additional Methods used in makeMove

	private boolean containsValidMoves(Piece[][] boardView) {
		for (int x = 0; x < boardView.length; x++) {
			for (int y = 0; y < boardView[0].length; y++)
				if (boardView[x][y].equals(Piece.UNSET))
					return true;
		}
		return false;
	}

	public void showBoard(Piece[][] boardView) {
		new BoardViewGenerator(boardView).PrintBoard();
	}

	private void printCommandPrompt() {

		System.out.println("How would you like to proceed?");
		System.out
				.println("a. either enter a set of coordinates separated by a comma to make a move ('x , y' e.g. '3 , 2') ");
		System.out.println("b.  or type 'Q' to concede");
		System.out.println();
	}

	private boolean confirmConcession() {
		System.out
				.println("Are you sure you wish to concede? (type 'Y'for yes)");
		System.out.println();
		String command = sc.next();

		if (command.trim().toUpperCase().equals("Y"))
			return true;
		else {
			System.out
					.println("Unsuccesful confirmation. Restarting move Prompt.");
			return false;
		}

	}

	private boolean isValidMoveString(String command) {
		String[] rawCoordinates = command.split(",");

		if (rawCoordinates.length != 2) {
			System.out
					.println("Invalid Command Entered. Restarting Move Prompt.");
			return false;
		}

		for (String entry : rawCoordinates) {
			try {
				Integer.parseInt(entry.trim());
			} catch (NumberFormatException e) {
				System.out
						.println("Invalid Command Entered. Restarting Move Prompt.");
				return false;
			}

		}
		return true;
	}

	private int[] getCoordinates(String commandString) {
		String[] rawCoordinates = commandString.split(",");
		int[] ints = new int[2];

		for (int i = 0; i < 2; i++) {
			ints[i] = Integer.parseInt(rawCoordinates[i].trim());
		}

		return ints;
	}

	private boolean setCoordinates(Move playerMove, int[] coordinates) {
		int x = coordinates[0];
		int y = coordinates[1];

		try {
			playerMove.setPosition(x, y);
		} catch (InvalidPositionException e) {
			System.out
					.println("The coordinates you entered are out of range. Restarting move prompt...");
			return false;
		}
		return true;
	}

	// ******************************************************************************************

	@Override
	public boolean setColour(Piece colour) throws InvalidColourException,
			ColourAlreadySetException {
		boolean successful = false;
		// if colour already set
		if (!this.colour.equals(Piece.UNSET))
			throw new ColourAlreadySetException();

		// if colour parameter is invalid throw exception; else set colour.
		if (colour.equals(Piece.UNSET))
			throw new InvalidColourException();
		else {
			this.colour = colour;
			successful = true;
		}

		return successful;
	}

	@Override
	public boolean finalGameState(GameState state) {
		gameState = state;

		switch (state) {
		case WON:
			System.out.println(String
					.format("Player %s: you %s", colour, state));
			sc.close();
			return true;
		case LOST:
			System.out.println(String
					.format("Player %s: you %s", colour, state));
			sc.close();
			return true;
		default:
			return false;
		}

	}

}
