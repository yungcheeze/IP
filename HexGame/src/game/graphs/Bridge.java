package game.graphs;

import game.interfaces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//Class used to represent a bridge on the board
public class Bridge {

	private ComputerVertex leadingHop; //more advanced hop in direction of travel
	private ComputerVertex trailingHop; //further back hop in direction of travel
	private ComputerVertex link1;
	private ComputerVertex link2;
	private Map<Piece, ComputerVertex> colourMap; //Stores the colour of each link
	//(helpful when determining state of the bridge

	public Bridge() {
		colourMap = new HashMap<Piece, ComputerVertex>();
	}

	public Set<ComputerVertex> getHops() {
		Set<ComputerVertex> hops = new HashSet<ComputerVertex>();
		hops.add(leadingHop);
		hops.add(trailingHop);
		return hops;
	}

	public void setHops(ComputerVertex trailing, ComputerVertex leading) {
		trailingHop = leading;
		leadingHop = leading;
	}
	
	public void setLeadingHop(ComputerVertex v)
	{
		leadingHop = v;
	}
	
	public void setTrailingHop(ComputerVertex v)
	{
		trailingHop = v;
	}
	
	public ComputerVertex getLeadingHop()
	{
		return leadingHop;
	}
	
	public ComputerVertex getBackwardHop()
	{
		return trailingHop;
	}
	

	public Set<ComputerVertex> getLinks() {
		Set<ComputerVertex> links = new HashSet<ComputerVertex>();
		links.add(link1);
		links.add(link2);
		return links;
	}

	public boolean setLinks(ComputerVertex v1, ComputerVertex v2) {
		link1 = v1;
		link2 = v2;
		updateColourMap(v1, v2);
		return true;
	}

	public boolean setLinks(Set<ComputerVertex> links) {
		if (links.size() == 2) {
			List<ComputerVertex> l = new ArrayList<ComputerVertex>(links);
			link1 = l.get(0);
			link2 = l.get(1);
			updateColourMap(link1, link2);
			return true;
		}
		else return false;
	}
	
	private void updateColourMap(ComputerVertex link1, ComputerVertex link2)
	{
		colourMap.put(link1.getColour(), link1);
		colourMap.put(link2.getColour(), link2);
	}
	
	//Determines state of the bridge
	//Compromised: can still be saved
	//Free: both links are unoccupied
	//Lost: both links occupied by opponent
	//Saved: at least one link occupied by calling player
	
	public BridgeState getBridgeState(Piece playerColour)
	{
		Piece opponent = opponentsColour(playerColour);
		boolean notPlayerColour = !colourMap.containsKey(playerColour);
		boolean oneFree = colourMap.containsKey(Piece.UNSET);
		boolean oneTaken = colourMap.containsKey(opponent);
		boolean noRED = !colourMap.containsKey(Piece.RED);
		boolean noBLUE = !colourMap.containsKey(Piece.BLUE);
		if(oneFree && oneTaken)
			return BridgeState.COMPROMISED;
		else if(noRED && noBLUE)
			return BridgeState.FREE;
		else if(oneTaken && notPlayerColour) 
			return BridgeState.LOST;
		else
			return BridgeState.SAVED;
			
	}
	
	private Piece opponentsColour(Piece colour) {
		if (colour.equals(Piece.RED))
			return Piece.BLUE;
		else if (colour.equals(Piece.BLUE))
			return Piece.RED;
		else
			return Piece.UNSET;
	}
	
	
	public ComputerVertex getSaveableLink()
	{
		return colourMap.get(Piece.UNSET);
	}

}
