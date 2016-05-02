



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/*
 * Graph representation used by ComputerPlayer
 * Has slightly different implementation from 
 * BoardGraph
 */
public class ComputerBoardGraph {

	private ComputerVertex[][] vertexBoard;
	private Map<ComputerVertex, Set<ComputerVertex>> adjacencyList;
	private Map<Piece, Set<ComputerVertex>> homes;
	private int xlim;
	private int ylim;

	public ComputerBoardGraph(Piece[][] boardView) {
		setUpBoardGraph(boardView);
	}

	public ComputerBoardGraph() {

	}

	public Set<ComputerVertex> getHops(ComputerVertex v) throws EmptySetException {
		Set<ComputerVertex> hopSet = new HashSet<ComputerVertex>();
		int[][] tests = new int[6][2];
		int x = v.getPosition().getXPos();
		int y = v.getPosition().getYPos();

		tests[0] = new int[] { x + 1, y + 1 };
		tests[1] = new int[] { x - 1, y + 2 };
		tests[2] = new int[] { x - 2, y + 1 };
		tests[3] = new int[] { x - 1, y - 1 };
		tests[4] = new int[] { x + 1, y - 2 };
		tests[5] = new int[] { x + 2, y - 1 };

		for (int[] test : tests) {
			int xtemp = test[0];
			int ytemp = test[1];
			Position pos = new Position(xtemp, ytemp);
			if (isOnBoard(pos)) {
				try {
					ComputerVertex u = (ComputerVertex) this.getVertex(pos);
					hopSet.add(u);
				} catch (InvalidPositionException e) {
					continue;
				}
			}
		}
		
		if(hopSet.size() == 0)
			throw new EmptySetException();
		return hopSet;
	}

	public Set<ComputerVertex> getLinks(ComputerVertex v1, ComputerVertex v2) throws EmptySetException{
		Set<ComputerVertex> linkSet = new HashSet<ComputerVertex>();
		Set<ComputerVertex> v1Adjacencies = new HashSet<ComputerVertex>();
		Set<ComputerVertex> v2Adjacencies = new HashSet<ComputerVertex>();

		if (this.getAdjacencyList().containsKey(v1) && this.getAdjacencyList().containsKey(v2)) {
			v1Adjacencies = new HashSet<ComputerVertex>(this.getAdjacencyList().get(v1));
			v2Adjacencies = new HashSet<ComputerVertex>(this.getAdjacencyList().get(v2));
		}

		if (v1Adjacencies.retainAll(v2Adjacencies)) {
			for (ComputerVertex v : v1Adjacencies) {
				linkSet.add((ComputerVertex) v);
			}
		}
		if(linkSet.isEmpty())
			throw new EmptySetException();
		return linkSet;
	}

	// Standard Graph Methods

	public void setUpBoardGraph(Piece[][] boardView) {
		xlim = boardView.length;
		ylim = boardView[0].length;
		vertexBoard = new ComputerVertex[xlim][ylim];
		adjacencyList = new HashMap<ComputerVertex, Set<ComputerVertex>>();
		homes = new HashMap<Piece, Set<ComputerVertex>>();
		homes.put(Piece.RED, new HashSet<ComputerVertex>());
		homes.put(Piece.BLUE, new HashSet<ComputerVertex>());

		MakeVertices(boardView);
		makeAdjacencies(boardView);
	}

	public boolean updateBoardGraph(Piece[][] boardView) {
		boolean changes = false;

		for (int x = 0; x < xlim; x++) {
			for (int y = 0; y < ylim; y++) {
				Piece vColour = vertexBoard[x][y].getColour();
				Piece bColour = boardView[x][y];
				if (!vColour.equals(bColour)) {
					vertexBoard[x][y].setColour(bColour);
					changes = true;
				}
			}
		}

		return changes;
	}

	private void MakeVertices(Piece[][] boardView) {

		for (int x = 0; x < xlim; x++) {
			for (int y = 0; y < ylim; y++) {
				// create vertex
				Position p = new Position(x, y);
				p.setPlacement(xlim, ylim);
				ComputerVertex v;
				v = new ComputerVertex(boardView[x][y], p);

				// add to board and initialise array
				vertexBoard[x][y] = v;
				Set<ComputerVertex> a = new HashSet<ComputerVertex>();
				adjacencyList.put(v, a);

				// add to respective home
				HashSet<PositionType> placements = v.getPosition().getPlacementSet();
				for (PositionType h : placements) {
					if (h.equals(PositionType.REDHOME)) {
						homes.get(Piece.RED).add(v);
					}

					if (h.equals(PositionType.BLUEHOME))
						homes.get(Piece.BLUE).add(v);
				}
			}
		}
	}

	private void makeAdjacencies(Piece[][] boardView) {

		for (int x = 0; x < xlim; x++) {
			for (int y = 0; y < ylim; y++) {

				ComputerVertex v = vertexBoard[x][y];
				Set<ComputerVertex> a = AdjacencyGenerator(x, y);
				adjacencyList.put(v, a);
			}
		}
	}

	// returns an set of positions adjacent to (x,y) to place into
	// adjacency list
	private Set<ComputerVertex> AdjacencyGenerator(int x, int y) {
		Set<ComputerVertex> adjacencies = new HashSet<ComputerVertex>();
		int[][] tests = new int[6][2];

		tests[0] = new int[] { x, y - 1 };
		tests[1] = new int[] { x + 1, y - 1 };
		tests[2] = new int[] { x + 1, y };
		tests[3] = new int[] { x, y + 1 };
		tests[4] = new int[] { x - 1, y + 1 };
		tests[5] = new int[] { x - 1, y };

		for (int[] test : tests) {
			int xtemp = test[0];
			int ytemp = test[1];
			Position pos = new Position(xtemp, ytemp);
			if (isOnBoard(pos)) {
				ComputerVertex v = vertexBoard[xtemp][ytemp];
				adjacencies.add(v);
			}
		}
		return adjacencies;
	}

	public int getXLim() {
		return xlim;
	}

	public int getYlim() {
		return ylim;
	}

	
	public boolean isTaken(Position pos) {
		int x = pos.getXPos();
		int y = pos.getYPos();
		Piece colour = vertexBoard[x][y].getColour();
		boolean result = !colour.equals(Piece.UNSET);
		return result;
	}

	// checks if position (x,y) is on the board
	public boolean isOnBoard(Position pos) {
		int x = pos.getXPos();
		int y = pos.getYPos();
		if (x < 0 || x >= xlim || y < 0 || y >= ylim)
			return false;
		else
			return true;
	}
	
	public boolean areAdjacent(ComputerVertex v1, ComputerVertex v2)
	{
		if(adjacencyList.containsKey(v1) && adjacencyList.containsKey(v2))
			return adjacencyList.get(v1).contains(v2);
		else return false;
	}

	public HashMap<ComputerVertex, Set<ComputerVertex>> getAdjacencyList() {
		return new HashMap<ComputerVertex, Set<ComputerVertex>>(adjacencyList);
	}
	
	public Set<ComputerVertex> getNeighbours(ComputerVertex v) throws EmptySetException
	{
		Set<ComputerVertex> neighbours = new HashSet<ComputerVertex>();
		if(adjacencyList.containsKey(v))
			neighbours = adjacencyList.get(v);
		//TODO throw EmptySetException
		if(neighbours.isEmpty())
			throw new EmptySetException();
		return neighbours;
	}
	
	public Set<ComputerVertex> getAllVertices()
	{
		return adjacencyList.keySet();
	}
	

	public HashMap<Piece, Set<ComputerVertex>> getHomes() {
		return new HashMap<Piece, Set<ComputerVertex>>(homes);
	}

	public ComputerVertex getVertex(Position pos) throws InvalidPositionException {

		if (isOnBoard(pos)) {
			int x = pos.getXPos();
			int y = pos.getYPos();
			return vertexBoard[x][y];
		} else
			throw new InvalidPositionException();
	}

}
