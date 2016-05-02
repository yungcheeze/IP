package game.graphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import game.interfaces.InvalidColourException;
import game.interfaces.InvalidPositionException;
import game.interfaces.Piece;
/*Graph representation of board used by board in conjunction 
with the pathFinder to implement the gameWon method*/
public class BoardGraph {
	private Vertex[][] vertexBoard;
	private HashMap<Vertex, ArrayList<Vertex>> adjacencyList;
	private HashMap<Piece, HashSet<Vertex>> homes; 
	protected int xlim;
	protected int ylim;
	
	public BoardGraph(Piece[][] boardView)
	{
		xlim = boardView.length;
		ylim = boardView[0].length;
		vertexBoard = new Vertex[xlim][ylim];
		adjacencyList = new HashMap<Vertex, ArrayList<Vertex>>();
		homes = new HashMap<Piece, HashSet<Vertex>>();
		homes.put(Piece.RED, new HashSet<Vertex>());
		homes.put(Piece.BLUE, new HashSet<Vertex>());
		
		
		MakeVertices(boardView);
		makeAdjacencies(boardView);
		
	}
	
	
	
	public void MakeVertices(Piece[][] boardView)
	{
		
		for (int x = 0; x < xlim; x++)
		{
			for (int y = 0; y < ylim; y++)
			{
				//create vertex
				Position p = new Position(x,y);
				p.setPlacement(xlim, ylim);
				Vertex v;
				v = new Vertex(boardView[x][y], p);
				
				//add to board and initialise array
				vertexBoard[x][y] = v;
				ArrayList<Vertex> a = new ArrayList<Vertex>();
				adjacencyList.put(v, a);
				
				//add to respective home
				HashSet<PositionType> placements = v.getPosition().getPlacementSet();
				for(PositionType h: placements)
				{
					if (h.equals(PositionType.REDHOME))
					{
						homes.get(Piece.RED).add(v);
					}
					
					if(h.equals(PositionType.BLUEHOME))
						homes.get(Piece.BLUE).add(v);
				}
			}
		}
	}
	
	public void makeAdjacencies(Piece[][] boardView)
	{
		
		for (int x = 0; x < xlim; x++)
		{
			for (int y = 0; y < ylim; y++)
			{
				
				Vertex v = vertexBoard[x][y];
				ArrayList<Vertex> a = AdjacencyGenerator(x, y);
				adjacencyList.put(v,a);
			}
		}
	}
	
	
	//returns an arraylist of positions adjacent to (x,y) to place into adjacency list
	private ArrayList<Vertex> AdjacencyGenerator(int x, int y)
	{
		ArrayList<Vertex> adjacencies = new ArrayList<Vertex>();
		int[][] tests = new int[6][2];
		
		tests[0] = new int[]{x, y-1};
		tests[1] = new int[]{x+1, y-1};
		tests[2] = new int[]{x+1, y};
		tests[3] = new int[]{x, y+1};
		tests[4] = new int[]{x-1, y+1};
		tests[5] = new int[]{x-1, y};
		
		for (int[] test : tests) 
		{
			int xtemp = test[0];
			int ytemp = test[1];
			if (isOnBoard(xtemp, ytemp)) 
			{
				Vertex v =  vertexBoard[xtemp][ytemp];
				adjacencies.add(v);
			} 
		}
		return adjacencies;
	}
	
	//checks if position (x,y) is on the board
	protected boolean isOnBoard(int x,int y)
	{
		if(x < 0 || x >= xlim || y < 0 || y >= ylim )
			return false;
		else
			return true;
	}
	
	public HashMap<Vertex, ArrayList<Vertex>> getAdjacencyList()
	{
		return new HashMap<Vertex, ArrayList<Vertex>>(adjacencyList);
	}
	
	public HashMap<Piece, HashSet<Vertex>> getHomes()
	{
		return new HashMap<Piece, HashSet<Vertex>>(homes);
	}
	
	public Vertex getVertex(int x, int y) throws InvalidPositionException
	{
		if(isOnBoard(x,y))
			return vertexBoard[x][y];
		else
			throw new InvalidPositionException();
	}
	
	

}
