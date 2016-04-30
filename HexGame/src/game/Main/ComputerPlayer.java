package game.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import game.AuxClasses.Axis;
import game.AuxClasses.Direction;
import game.graphs.Bridge;
import game.graphs.ComputerBoardGraph;
import game.interfaces.ColourAlreadySetException;
import game.interfaces.GameState;
import game.interfaces.InvalidColourException;
import game.interfaces.InvalidPositionException;
import game.interfaces.MoveInterface;
import game.interfaces.NoValidMovesException;
import game.interfaces.Piece;
import game.interfaces.PlayerInterface;
import game.graphs.ComputerVertex;
import game.graphs.EmptySetException;
import game.graphs.Position;
import game.graphs.PositionType;;

public class ComputerPlayer implements PlayerInterface {
	private Piece colour;
	private GameState gameState;
	private ComputerBoardGraph boardGraph;
	private LinkedList<ComputerVertex> mainPath;
	private ComputerVertex head;
	private ComputerVertex tail;
	private boolean movingForwards;
	private boolean movingBackwards;
	private int moveNumber;
	private Direction playingDirection;
	private Axis playingAxis;// playing axis if x hops are x-coordinate
								// dependent and vice versa
	private boolean firstMove;
	
	//TODO Restructure MainPath as linked list, remove forwardHop e.t.c
	//when updating leading vertex, add head/tail of main path depending playing direction
	//PathBroken(if bridge recieved)
	//Path Check  [returns breakPoints (broken bridge)]
		//i and i+1 on linked list
		//if i+1 is hop
			//check linking vertices
		//if i+1 is adjacent, link not broken
	
	//fixing the path
		//check length from either side of break point, discard shorter path
		//need an Exception check to see if its possible to get a broken path longer than a bridge i.e. no Links between break point

	public ComputerPlayer() {
		colour = Piece.UNSET;
		playingDirection = Direction.FORWARDS;// starting direction forward
		firstMove = true;
		movingForwards = true;
		movingBackwards = true;
		mainPath = new LinkedList<ComputerVertex>();
		boardGraph = new ComputerBoardGraph();
		gameState = GameState.INCOMPLETE;
	}

	@Override
	public MoveInterface makeMove(Piece[][] boardView) throws NoValidMovesException {
		// TODO Auto-generated method stub
		if (!containsValidMoves(boardView) || !gameState.equals(GameState.INCOMPLETE) || colour.equals(Piece.UNSET))
			throw new NoValidMovesException();
		System.out.println();
		System.out.println("***" + colour + " to play***");
		
		Move move = new Move();
		
		ComputerVertex leadingVertex = head;
		if(playingDirection.equals(Direction.BACKWARDS))
			leadingVertex = tail;
		
		try {
			// first move
			if (firstMove) {
				// generate boardGraph
				boardGraph.setUpBoardGraph(boardView);
				firstMove = false;
				// find Centre, and try to place piece there
				int xlim = boardGraph.getXLim();
				int ylim = boardGraph.getYlim();

				int xmid = (int) Math.floor(xlim / 2);
				int ymid = (int) Math.floor(ylim / 2);

				Position position = new Position(xmid, ymid);

				// if centre is free, place piece
				if (!boardGraph.isTaken(position)) {
					move.setPosition(xmid, ymid);
					displayMoveDecision(move);
					leadingVertex = boardGraph.getVertex(position);
					head = leadingVertex;
					tail = leadingVertex;
					mainPath.add(leadingVertex);
					return move; // MOVE MADE
				}

				// else try random side hop
				Set<ComputerVertex> hops;
				try {
					hops = getFreeHops(position, playingDirection);
					ComputerVertex mostForward = mostForwardVertex(hops);
					hops.remove(mostForward);
					ComputerVertex v = (ComputerVertex) hops.toArray()[0];
					int x = v.getPosition().getXPos();
					int y = v.getPosition().getYPos();
					leadingVertex = boardGraph.getVertex(position);
					head = leadingVertex;
					tail = leadingVertex;
					mainPath.add(leadingVertex);
					displayMoveDecision(move);
					return move; // MOVE MADE
				} catch (EmptySetException e) {
					// Thrown by getFreeHops
					//if none found continue to find random free vertex
//					e.printStackTrace();
				}
				

			}

			// Main Sequence
			boardGraph.updateBoardGraph(boardView);
			
			// hops refer to two-bridges
			// links refer to connecting vertices in bridge

			// check path, take note of compromised links check if broken
			// if broken, select longer path as main path
			if (pathBroken()) {
				int fromHead = pathLength(head, Direction.BACKWARDS);
				int fromTail = pathLength(tail, Direction.FORWARDS);
				if (fromHead <= fromTail) {
					tail = endOfPath(head, Direction.BACKWARDS);
				} else {
					head = endOfPath(tail, Direction.FORWARDS);
				}
			} else {
				// if head is end, stop going forwards
				if (head.isEND(colour))
					movingForwards = false;
				// if tail is home, stop going backwards
				if (tail.isHOME(colour))
					movingBackwards = false;	
			}
			
			// N.B if complete, start filling gaps (head is end, tail
			// is
			// home)(!movingForwards && !movingBackards)
			
			// check if hops in path compromised
			// modified pathTraversal() if links between start and hop contain
			// one
			// opponent colour and other unset, then return unset;
			Bridge compromisedBridge;
			try {
				compromisedBridge = findCompromisedBridge();
				ComputerVertex linkToSave = compromisedBridge.getCompromisedLink();
				int x = linkToSave.getPosition().getXPos();
				int y = linkToSave.getPosition().getYPos();
				move.setPosition(x, y);
				displayMoveDecision(move);
				return move; // save compromised hop (MOVE MADE)
				
			} catch (EmptySetException e1) {
				// thrown from findCompromisedBridge
				//if caught then no compromised bridge found therefore proceed to check for free hops
//				e1.printStackTrace();
			}

			
			// complete path, select a gap to fill (MOVE MADE)
			if(!movingForwards && !movingBackwards)
			{
				Bridge freeBridge;
				try {
					freeBridge = findFreeBridge(tail);
					ComputerVertex linkToUse = freeBridge.getSaveableLink();
					int x = linkToUse.getPosition().getXPos();
					int y = linkToUse.getPosition().getYPos();
					move.setPosition(x, y);
					displayMoveDecision(move);
					return move; // gap filled (MOVE MADE)
				} catch (EmptySetException e) {
					//thrown by findFreeBridge
					//proceed to find random vertex
//					e.printStackTrace();
				}
				 
			}
			// see if you can add to main path via existing vertices only add if
			// integrity of potential path is good (has viable hops)
			// TODO
			// check forwards
//			if (movingForwards) {
//				Position position = head.getPosition();
//				Set<ComputerVertex> headHops = getAllHops(position, Direction.FORWARDS);
//				ComputerVertex potentialFix = null;
//				for (ComputerVertex hop : headHops) {
//					if (hop.getColour().equals(colour)) {
//
//					}
//				}
//			}


			
			
			
			
			// check for hop in current direction with free links
			Set<ComputerVertex> freeHops = new HashSet<ComputerVertex>();
			try {
				freeHops = getFreeHops(leadingVertex.getPosition(), playingDirection);
				ComputerVertex mostForwardHop = mostForwardVertex(freeHops);
				//TODO add to linked list instead
				//leadingVertex.setHop(playingDirection, mostForwardHop);
				leadingVertex = mostForwardHop;
				updateLeadingVertex(mostForwardHop);
				Position position = mostForwardHop.getPosition();
				int x = position.getXPos();
				int y = position.getYPos();
				move.setPosition(x, y);
				displayMoveDecision(move);
				changeDirection(); // switch directions
				return move; // place piece (MOVE MADE)
			} catch (EmptySetException e) {
				//thrown by getfreeHops()
				//if caught, then no free hops therefore proceed to find free adjacent vertex
//				e.printStackTrace();
			} 


			// check for link in current direction
			Set<ComputerVertex> freeVertices = new HashSet<ComputerVertex>();
			
			try {
				freeVertices = getFreeNeighours(leadingVertex);
				ComputerVertex mostForward = mostForwardVertex(freeVertices);
				updateLeadingVertex(mostForward);
				int x = mostForward.getPosition().getXPos();
				int y = mostForward.getPosition().getYPos();
				move.setPosition(x, y);
				displayMoveDecision(move);
				changeDirection();
				return move; // place piece (MOVE MADE)
			} catch (EmptySetException e) {
				//thrown by getfreeNeighbours
				//if caught, then no free neighbours therefore proceed to find random free vertex
//				e.printStackTrace();
			}
			
			//check for random free vertex
			//Set<ComputerVertex> allVertices = boardGraph.getAdjacencyList().keySet();
			//ComputerVertex 
			
			
			
			
			
			

			
		} catch (InvalidPositionException e) {
			move.setConceded();
			displayMoveDecision(move);
			e.printStackTrace();
			return move;
		}

		return null;
	}

	// *****************************************************************************
	// Additional Methods used in MakeMove

	private boolean containsValidMoves(Piece[][] boardView) {
		for (int x = 0; x < boardView.length; x++) {
			for (int y = 0; y < boardView[0].length; y++)
				if (boardView[x][y].equals(Piece.UNSET))
					return true;
		}
		return false;
	}
	
	private void displayMoveDecision(Move move)
	{
		int x = move.getXPosition();
		int y = move.getYPosition();
		String toPrint = String.format("A.I. placed piece at (%d, %d)", x, y);
		if(!move.hasConceded())
			System.out.println(toPrint);
		else
			System.out.println("A.I. has Conceeded");
		
		System.out.println();
	}
	
	private void updateLeadingVertex(ComputerVertex leadingVertex)
	{
		if(playingDirection.equals(Direction.FORWARDS))
		{
			mainPath.addLast(leadingVertex);
			head = leadingVertex;
		}
		else
		{
			mainPath.addFirst(leadingVertex);
			tail = leadingVertex;
		}
	}
	
	//TODO hasHop method
	private boolean hasNextHop(ComputerVertex u, Direction d)
	{
		boolean toReturn = false;
		try {
			if (mainPath.contains(u))
			{
				int uIndex = mainPath.indexOf(u);
				int maxIndex = mainPath.size() - 1;
				
				if (d.equals(Direction.FORWARDS) && uIndex < maxIndex)
				{
					ComputerVertex v = mainPath.get(uIndex + 1);
					int noOfLinks = boardGraph.getLinks(u, v).size();
					boolean adjacent = boardGraph.areAdjacent(u, v);

					if(noOfLinks == 2 && !adjacent)
						toReturn = true;
					
					
				}
				else if(d.equals(Direction.FORWARDS) && uIndex > 0)
				{
					ComputerVertex v = mainPath.get(uIndex - 1);
					int noOfLinks = boardGraph.getLinks(u, v).size();
					boolean adjacent = boardGraph.areAdjacent(u, v);

					if(noOfLinks == 2 && !adjacent)
						toReturn = true;;
				}
					
			}
		} catch (EmptySetException e) {
			return false;
		}
		return toReturn;
					
	}
	
	private boolean hasNext(ComputerVertex u, Direction d)
	{
		int uIndex = mainPath.indexOf(u);
		int maxIndex = mainPath.size() - 1;
		int minIndex = 0;
		if(d.equals(Direction.FORWARDS))
		{
			ComputerVertex v = mainPath.get(uIndex + 1);
			boolean adjacent = boardGraph.areAdjacent(u, v);
			return uIndex < maxIndex && adjacent;
		}
		else
		{
			ComputerVertex v = mainPath.get(uIndex - 1);
			boolean adjacent = boardGraph.areAdjacent(u, v);
			return uIndex > minIndex && adjacent;
		}
	}
	

	
	private boolean changeDirection()
	{
		if (playingDirection.equals(Direction.FORWARDS) && movingBackwards)
		{
			playingDirection = playingDirection.otherDirection();
			return true;
		}
		else if (playingDirection.equals(Direction.BACKWARDS) && movingForwards)
		{
			playingDirection = playingDirection.otherDirection();
			return true;
		}
		else return false;
	}
	
		

	private Set<ComputerVertex> getAllHops(Position position, Direction direction) throws EmptySetException {
		Set<ComputerVertex> hops = new HashSet<ComputerVertex>();

		try {
			ComputerVertex v = boardGraph.getVertex(position);

			for (ComputerVertex u : boardGraph.getHops(v)) {
				Position uPos = u.getPosition();

				if (playingDirection.equals(Direction.FORWARDS)) {
					if (playingAxis.equals(Axis.X) && uPos.getXPos() > position.getXPos())
						hops.add(u);
					if (playingAxis.equals(Axis.Y) && uPos.getYPos() > position.getYPos())
						hops.add(u);
				} else {
					if (playingAxis.equals(Axis.X) && uPos.getXPos() < position.getXPos())
						hops.add(u);
					if (playingAxis.equals(Axis.Y) && uPos.getYPos() < position.getYPos())
						hops.add(u);
				}
			}
		} catch (InvalidPositionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hops;
	}

	private Set<ComputerVertex> getFreeHops(Position position, Direction direction) throws EmptySetException {
		Set<ComputerVertex> hops = new HashSet<ComputerVertex>();

		try {
			ComputerVertex v = boardGraph.getVertex(position);

			for (ComputerVertex u : boardGraph.getHops(v)) {
				if (boardGraph.isTaken(u.getPosition()))
					continue;

				Position uPos = u.getPosition();

				if (playingDirection.equals(Direction.FORWARDS)) {
					if (playingAxis.equals(Axis.X) && uPos.getXPos() > position.getXPos())
						hops.add(u);
					if (playingAxis.equals(Axis.Y) && uPos.getYPos() > position.getYPos())
						hops.add(u);
				} else {
					if (playingAxis.equals(Axis.X) && uPos.getXPos() < position.getXPos())
						hops.add(u);
					if (playingAxis.equals(Axis.Y) && uPos.getYPos() < position.getYPos())
						hops.add(u);
				}
			}
		} catch (InvalidPositionException e) {
			// TODO Auto-generated catch block
			System.out.println("invalid position exception caught by getFreeHops!");
			e.printStackTrace();
		}

		return hops;
	}
	
	//TODO possible freeVertices(Set<>) method
	private Set<ComputerVertex> getFreeNeighours (ComputerVertex vertex) throws EmptySetException
	{
		Set<ComputerVertex> allNeighbours = boardGraph.getNeighbours(vertex);
		Set<ComputerVertex> freeNeighbours = new HashSet<ComputerVertex>();
		
		for(ComputerVertex v : allNeighbours)
		{
			if(v.getColour().equals(Piece.UNSET))
				freeNeighbours.add(v);
		}
		
		if(!freeNeighbours.isEmpty())
			return allNeighbours;
		else
			throw new EmptySetException();
	}

	private ComputerVertex mostForwardVertex(Set<ComputerVertex> vertices) throws EmptySetException {
		ArrayList<ComputerVertex> vList = new ArrayList<ComputerVertex>(vertices);
		ComputerVertex mostForward;
		if (vList.size() > 0)
			mostForward = vList.get(0);
		else
			throw new EmptySetException();
		for (ComputerVertex v : vertices) {
			if(playingDirection.equals(Direction.FORWARDS)){
				if (playingAxis.equals(Axis.X) && v.getPosition().getXPos() > mostForward.getPosition().getXPos())
					mostForward = v;
				else if (playingAxis.equals(Direction.BACKWARDS) && v.getPosition().getYPos() > mostForward.getPosition().getYPos())//playingAxis == y
					mostForward = v;
			}
			else{
				if (playingAxis.equals(Axis.X) && v.getPosition().getXPos() < mostForward.getPosition().getXPos())
					mostForward = v;
				else if (playingAxis.equals(Direction.BACKWARDS) && v.getPosition().getYPos() < mostForward.getPosition().getYPos())
					mostForward = v;
			}
		}

		return mostForward;
	}

	private boolean pathBroken() {
		return !endOfPath(tail, Direction.FORWARDS).equals(head);
	}
	//TODO replace recursive function with linkedList implementation 
	 //from tail to head, returns head
	public ComputerVertex endOfPath(ComputerVertex start, Direction d) {
		//preprocessing
		int startIndex = mainPath.indexOf(start);
		int nextIndex = startIndex + 1;
		if(d.equals(Direction.BACKWARDS))
			nextIndex = startIndex - 1;
		
		boolean hasNext = hasNext(start, d) || hasNextHop(start, d);
		if (!hasNext) { //base case
			return start;
		} else //recursive case
		{
			ComputerVertex next = mainPath.get(nextIndex);
			return endOfPath(next, d);
			
		}
	}

	public int pathLength(ComputerVertex start, Direction d) {
		ComputerVertex current = start;
		int currentIndex = mainPath.indexOf(current);
		
		int nextIndex = currentIndex + 1;
		if(d.equals(Direction.BACKWARDS))
			nextIndex = currentIndex - 1;
		boolean hasNext = hasNext(current, d) || hasNextHop(current, d);
		
		int length = 0;
		while (hasNext) {
			current = mainPath.get(nextIndex);
			hasNext = hasNext(current, d) || hasNextHop(current, d);
			length++;
		}
		return length;
	}

	// modified pathTraversal() if links between start and hop contain one
	// opponent colour and other unset, then return unset;
	//TODO make iterative instead of recursive
	public Bridge findCompromisedBridge() throws EmptySetException {
		
		boolean found = false;
		int maxIndex = mainPath.size()-1;
		int i = 0;
		while(i < maxIndex && !found)
		{
			ComputerVertex current = mainPath.get(i);
			boolean hasHop = hasNextHop(current, Direction.FORWARDS);
			if(hasHop)
			{
				ComputerVertex next = mainPath.get(i+1);
				Set<ComputerVertex> linkSet = boardGraph.getLinks(current, next);
				//TODO implement colourMap within Bridge and bridgeState ennum
				Map<Piece, ComputerVertex> colourMap = new HashMap<Piece, ComputerVertex>();
				for (ComputerVertex link: linkSet)
				{
					Piece c = link.getColour();
					colourMap.put(c, link);
				}
				
				Piece opponent = opponentsColour();
				boolean oneFree = colourMap.containsKey(Piece.UNSET);
				boolean oneTaken = colourMap.containsKey(opponent);
				if(oneFree && oneTaken)
				{
					Bridge compromisedBridge = new Bridge();
					compromisedBridge.setHops(current, next);
					compromisedBridge.setLinks(linkSet);
					ComputerVertex saveableLink = colourMap.get(Piece.UNSET);
					compromisedBridge.setSaveableLink(saveableLink);
					return compromisedBridge;
				}	
			}
		}
		
		throw new EmptySetException();

	}
	
	//TODO make iterative instead of recursive
	private Bridge findFreeBridge(ComputerVertex start) throws EmptySetException
	{
		boolean found = false;
		int maxIndex = mainPath.size()-1;
		int i = 0;
		while(i < maxIndex && !found)
		{
			ComputerVertex current = mainPath.get(i);
			boolean hasHop = hasNextHop(current, Direction.FORWARDS);
			if(hasHop)
			{
				ComputerVertex next = mainPath.get(i+1);
				Set<ComputerVertex> linkSet = boardGraph.getLinks(current, next);
				Map<Piece, ComputerVertex> colourMap = new HashMap<Piece, ComputerVertex>();
				for (ComputerVertex link: linkSet)
				{
					Piece c = link.getColour();
					colourMap.put(c, link);
				}
				
				boolean noRED = !colourMap.containsKey(Piece.RED);
				boolean noBlue = !colourMap.containsKey(Piece.BLUE);
				if(noRED && noBlue)
				{
					Bridge freeBridge = new Bridge();
					freeBridge.setHops(current, next);
					freeBridge.setLinks(linkSet);
					ComputerVertex saveableLink = colourMap.get(Piece.UNSET);
					freeBridge.setSaveableLink(saveableLink);
					return freeBridge;
				}	
			}
		}
		
		throw new EmptySetException();
	}

	private Piece opponentsColour() {
		if (colour.equals(Piece.RED))
			return Piece.BLUE;
		else if (colour.equals(Piece.BLUE))
			return Piece.RED;
		else
			return Piece.UNSET;
	}

	// *****************************************************************************

	@Override
	public boolean setColour(Piece colour) throws InvalidColourException, ColourAlreadySetException {
		boolean successful = false;
		// if colour already set
		if (!this.colour.equals(Piece.UNSET))
			throw new ColourAlreadySetException();

		// if colour parameter is invalid throw exception; else set colour.
		if (colour.equals(Piece.UNSET))
			throw new InvalidColourException();
		else {
			this.colour = colour;
			successful = true;
		}

		if (colour.equals(Piece.RED))
			playingAxis = Axis.Y;
		else
			playingAxis = Axis.X;

		return successful;
	}

	@Override
	public boolean finalGameState(GameState state) {

		gameState = state;

		return true;
	}

}
