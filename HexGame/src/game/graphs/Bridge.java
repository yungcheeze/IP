package game.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bridge {

	private ComputerVertex leadingHop; //more advanced hop in direction of travel
	private ComputerVertex trailingHop; //further back hop in direction of travel
	private ComputerVertex link1;
	private ComputerVertex link2;
	private ComputerVertex saveablelink;

	public Bridge() {

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
		return true;
	}

	public boolean setLinks(Set<ComputerVertex> links) {
		if (links.size() == 2) {
			List<ComputerVertex> l = new ArrayList<ComputerVertex>(links);
			link1 = l.get(0);
			link2 = l.get(1);
			return true;
		}
		else return false;
	}
	
	public void setSaveableLink(ComputerVertex link)
	{
		saveablelink = link;
	}
	
	public ComputerVertex getSaveableLink()
	{
		return saveablelink;
	}

}
