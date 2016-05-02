



import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
/*
 * Extension of vertex used in ComputerBoardGraph
 * has new isEND and isHome methods
 */
public class ComputerVertex extends Vertex {
	

	public ComputerVertex(Piece colour, Position position) {
		super(colour, position);

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
