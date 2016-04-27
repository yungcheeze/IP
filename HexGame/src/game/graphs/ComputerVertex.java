package game.graphs;

import game.AuxClasses.Direction;
import game.interfaces.Piece;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ComputerVertex extends Vertex {
	private ComputerVertex forwardhop;
	private ComputerVertex backwardhop;
	private ComputerVertex forwardlink;
	private ComputerVertex backwardlink;

	public ComputerVertex(Piece colour, Position position) {
		super(colour, position);

	}

	public Vertex getHop(Direction d) {
		if (d.equals(Direction.FORWARDS))
			return forwardhop;
		else
			return backwardhop;
	}

	// TODO possible boolean return if I decide to clear
	public void setHop(Direction d, ComputerVertex v) {
		if (d.equals(Direction.FORWARDS)) {
			forwardhop = v;
		} else {
			backwardhop = v;
		}
	}

	public Vertex getLink(Direction d) {
		if (d.equals(Direction.FORWARDS))
			return forwardlink;
		else
			return backwardlink;
	}

	// TODO possible boolean return if I decide to clear
	public void setLink(Direction d, ComputerVertex v) {
		if (d.equals(Direction.FORWARDS)) {
			forwardlink = v;
		} else {
			backwardlink = v;

		}
	}

	// TODO do I really need to clear hops, or will it be useful when path
	// broken?
	public boolean clearHop(Direction d) {
		return false;
	}

	public boolean clearLink(Direction d) {
		return false;
	}

	public boolean hasHop(Direction d) {
		if (d.equals(Direction.FORWARDS)) {
			if (forwardhop == null)
				return false;
			else
				return true;
		} else {
			if (backwardhop == null)
				return false;
			else
				return true;
		}
	}

	public boolean hasLink(Direction d) {
		if (d.equals(Direction.FORWARDS)) {
			if (forwardlink.equals(null))
				return false;
			else
				return true;
		} else {
			if (backwardlink.equals(null))
				return false;
			else
				return true;
		}
	}
	
	public boolean isEND(Piece colour) {
		if (colour.equals(Piece.RED))
			return getPosition().getPlacementSet().contains(PositionType.REDEND);
		else if (colour.equals(Piece.BLUE))
			return getPosition().getPlacementSet().contains(PositionType.BLUEEND);
		else
			return false;
	}
	
	public boolean isHOME(Piece colour) {
		if (colour.equals(Piece.RED))
			return getPosition().getPlacementSet().contains(PositionType.REDHOME);
		else if (colour.equals(Piece.BLUE))
			return getPosition().getPlacementSet().contains(PositionType.BLUEHOME);
		else
			return false;
	}

}
