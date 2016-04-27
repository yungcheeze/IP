package game.graphs;

import game.interfaces.Piece;

public class Vertex {

	private Piece colour;
	private Position position;
	private boolean visited;

	public Vertex(Piece colour, Position position) {
		this.colour = colour;
		visited = false;
		this.position = position;
	}

	public void setVisited() {
		visited = true;
	}

	public void setUnvisited() {
		visited = false;
	}

	public boolean isVisited() {
		return visited;
	}

	public Position getPosition() {
		return position;
	}

	public Piece getColour() {
		return colour;
	}

	public void setColour(Piece colour) {
		this.colour = colour;
	}
	
	public boolean isEND() {
		if (colour.equals(Piece.RED))
			return position.getPlacementSet().contains(PositionType.REDEND);
		else
			return position.getPlacementSet().contains(PositionType.BLUEEND);
	}
	
	public boolean isHOME() {
		if (colour.equals(Piece.RED))
			return position.getPlacementSet().contains(PositionType.REDHOME);
		else
			return position.getPlacementSet().contains(PositionType.BLUEHOME);
	}

}
