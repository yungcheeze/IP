



import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//Used by gameWon() in board
/*Runs a DFS from a given 0 co-ordinate and sees if it can reach
a MAX coordinate on other end of board*/
public class PathFinder {
	
	private HashMap<Vertex, ArrayList<Vertex>> adjacencyList;
	private HashMap<Piece, HashSet<Vertex>> homes; 
	private boolean pathFound;
	
	public PathFinder(HashMap<Vertex, ArrayList<Vertex>> adjacencyList, HashMap<Piece, HashSet<Vertex>> homes)
	{
		this.adjacencyList = adjacencyList;
		this.homes = homes;
		pathFound = false;
	}
	
	public boolean pathFound(Piece player) throws InvalidColourException
	{
		if(player.equals(Piece.UNSET))
			throw new InvalidColourException();
		
		pathFound = false;
		
		//for each home position
		for(Vertex startVertex : homes.get(player))
		{
			//start a pathFinder
			if(startVertex.getColour().equals(player) && !pathFound)
				pathFinder(player, startVertex);
				
				
		}
			
		
		return pathFound;
	}
	
	
	
	private void pathFinder(Piece player, Vertex vertex)
	{
		if(pathFound)
			return;
		
		//set vertex to visited
		vertex.setVisited();
		//if vertex is End
			//pathFound = true
			//return
		boolean end = vertex.isEND();
		if(end)
		{
			pathFound = true;
			return;
		}
		
		//for vertex in adjacencylist
			//if vertex is unvisited and has correct piece
				//pathfinder(player, u)
		
		for(Vertex u : adjacencyList.get(vertex))
		{
			if(!u.isVisited() && u.getColour().equals(player))
				pathFinder(player, u);
		}
		
	}
	

}
