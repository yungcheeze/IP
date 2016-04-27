package game.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bridge {

	private ComputerVertex forwardHop;
	private ComputerVertex backwardHop;
	private ComputerVertex link1;
	private ComputerVertex link2;
	private ComputerVertex compromisedlink;

	public Bridge() {

	}

	public Set<ComputerVertex> getHops() {
		Set<ComputerVertex> hops = new HashSet<ComputerVertex>();
		hops.add(forwardHop);
		hops.add(backwardHop);
		return hops;
	}

	public void setHops(ComputerVertex v1, ComputerVertex v2) {
		forwardHop = v1;
		backwardHop = v2;
	}
	
	public void setForwardHop(ComputerVertex v)
	{
		forwardHop = v;
	}
	
	public void setBackwardHop(ComputerVertex v)
	{
		backwardHop = v;
	}
	
	public ComputerVertex getForwardHop()
	{
		return forwardHop;
	}
	
	public ComputerVertex getBackwardHop()
	{
		return backwardHop;
	}
	

	public Set<ComputerVertex> getLinks() {
		Set<ComputerVertex> links = new HashSet<ComputerVertex>();
		links.add(link1);
		links.add(link2);
		return links;
	}

	public void setLinks(ComputerVertex v1, ComputerVertex v2) {
		link1 = v1;
		link2 = v2;
	}

	public void setLinks(Set<ComputerVertex> links) {
		if (links.size() == 2) {
			List<ComputerVertex> l = new ArrayList<ComputerVertex>(links);
			link1 = l.get(0);
			link2 = l.get(1);
		}
	}
	
	public void setCompromisedLink(ComputerVertex link)
	{
		compromisedlink = link;
	}
	
	public ComputerVertex getCompromisedLink()
	{
		return compromisedlink;
	}

}
