package game.tests;

import game.AuxClasses.Direction;
import game.Main.ComputerPlayer;
import game.graphs.ComputerVertex;
import game.graphs.Position;
import game.interfaces.Piece;

public class TraversalTest {

	public static void main(String[] args) {
		Piece c = Piece.RED;
		Position p = new Position(0, 0);
		Direction d = Direction.FORWARDS;
		ComputerPlayer player = new ComputerPlayer();
		ComputerVertex v1 = new ComputerVertex(c, p);
		ComputerVertex v2 = new ComputerVertex(c, p);
		ComputerVertex v3 = new ComputerVertex(c, p);
		ComputerVertex v4 = new ComputerVertex(c, p);
		ComputerVertex v5 = new ComputerVertex(c, p);

		

		ComputerVertex end = player.endOfPath(v1, d);

		int length = player.pathLength(v1, d);
		System.out.println(v5.equals(end) + " : " + length);

	}
}
