package game.tests;

import java.util.HashMap;

import game.AuxClasses.Direction;
import game.graphs.ComputerVertex;
import game.graphs.Position;
import game.interfaces.Piece;

public class MutabilityTest {

	ComputerVertex head;
	ComputerVertex tail;
	HashMap<Direction, ComputerVertex> leadVertexMap;

	public MutabilityTest() {
		Piece c = Piece.RED;
		Position r = new Position(4, 1);
		Position p = new Position(0, 0);

		head = new ComputerVertex(c, p);
		tail = new ComputerVertex(c, r);
		
		leadVertexMap = new HashMap<Direction, ComputerVertex>();
		leadVertexMap.put(Direction.FORWARDS, head);
		leadVertexMap.put(Direction.BACKWARDS, tail);
	}

	public void tests() {
		// TODO Auto-generated method stub
		Piece c = Piece.RED;
		Position p = new Position(0, 0);
		Position q = new Position(3, 1);
		Position r = new Position(4, 1);

		ComputerVertex newHead = new ComputerVertex(c, q);

		ComputerVertex leadingVertex = head;
		 System.out.println(leadingVertex.equals(head));
		 leadingVertex = newHead;
		 head = newHead;
		 System.out.println(leadingVertex.equals(head));
		 System.out.println(leadVertexMap.get(Direction.FORWARDS).equals(head));
		

		
//
//		leadingVertex = newHead;
//		leadVertexMap.get(Direction.FORWARDS) = newHead;

	}

	public static void main(String[] args) {
		new MutabilityTest().tests();
	}

}
