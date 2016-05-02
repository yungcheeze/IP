package game.graphs;

import java.util.HashSet;
//Used to store position info a vertex in graph representation of board
public class Position {

	private int xPos;
	private int yPos;
	private HashSet<PositionType> placement;
	public Position(int xPos, int yPos)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		placement = new HashSet<PositionType>(); 
	}
	
	public int getXPos()
	{
		return xPos;
	}
	
	public int getYPos()
	{
		return yPos;
	}
	
	public void setPlacement(int xlim, int ylim)
	{
		
		
		if (xPos == 0)
		{
			placement.add(PositionType.BLUEHOME);
		}
		else if(xPos == xlim - 1)
		{
			placement.add(PositionType.BLUEEND);
		}
		
		if (yPos == 0)
		{
			placement.add(PositionType.REDHOME);
		}
		else if(yPos == ylim - 1)
		{
			placement.add(PositionType.REDEND);
		}
		
		if(placement.size() == 0)
		{
			placement.add(PositionType.MID);
		}
	}	
	
	public HashSet<PositionType> getPlacementSet()
	{
		return placement;
	}
	
	public boolean equals(Position other)
	{
		return(this.xPos == other.getXPos()) && (this.yPos == other.getYPos());
	}
	
}
