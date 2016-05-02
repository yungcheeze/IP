package game.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

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
import game.graphs.BridgeState;
import game.graphs.ComputerVertex;
import game.graphs.EmptySetException;
import game.graphs.NoBridgeFoundException;
import game.graphs.NoGoodVertexException;
import game.graphs.Position;
import game.graphs.PositionType;;

public class ComputerPlayer implements PlayerInterface {
	private Piece colour;
	private GameState gameState;
	private ComputerBoardGraph boardGraph;
	private LinkedList<ComputerVertex> mainPath;
	private Map<Direction, Integer> adjacencyCount;//counts number of times adjcaent vertex in 
	//current move direction was chosen
	private ComputerVertex head;
	private ComputerVertex tail;
	private boolean movingForwards;
	private boolean movingBackwards;
	private int moveNumber;
	private Direction playingDirection;
	private Axis playingAxis;// playing axis if x hops are x-coordinate
								// dependent and vice versa
	private boolean firstMove;
	

	public ComputerPlayer() {
		colour = Piece.UNSET;
		playingDirection = Direction.FORWARDS;// starting direction forward
		firstMove = true;
		movingForwards = true;
		movingBackwards = true;
		mainPath = new LinkedList<ComputerVertex>();
		boardGraph = new ComputerBoardGraph();
		gameState = GameState.INCOMPLETE;
		adjacencyCount = new HashMap<Direction, Integer>();
		adjacencyCount.put(Direction.FORWARDS, 0);
		adjacencyCount.put(Direction.BACKWARDS, 0);
	}

	@Override
	public MoveInterface makeMove(Piece[][] boardView) throws NoValidMovesException {

		if (!containsValidMoves(boardView) || !gameState.equals(GameState.INCOMPLETE) || colour.equals(Piece.UNSET))
			throw new NoValidMovesException();
		System.out.println();
		System.out.println("***" + colour + " to play***");
		
		//builds/updates abstraction of the board
		/*(A graph with positions as vertices and edge between any two vertices
		that share a border.*/
		Move move = new Move();
		if(firstMove)
			boardGraph.setUpBoardGraph(boardView);
		else
			boardGraph.updateBoardGraph(boardView);
		
		//changes the current head of the main path
		ComputerVertex leadingVertex = head;
		if(playingDirection.equals(Direction.BACKWARDS))
			leadingVertex = tail;
		
		try {
			// first move
			if (firstMove) {
				// generate boardGraph

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
				} else {
					// else try random side hop
					ComputerVertex mostForward;
					try {
						mostForward = findNewHead();
					} catch (NoGoodVertexException e) {
						try {
							mostForward = anyRandomPosition();
						} catch (NoGoodVertexException e1) {
							move.setConceded();
							displayMoveDecision(move);
							return move;
						}
					}
					ComputerVertex v = mostForward;
					int x = v.getPosition().getXPos();
					int y = v.getPosition().getYPos();
					move.setPosition(x, y);
					leadingVertex = boardGraph.getVertex(position);
					leadingVertex = v;
					head = leadingVertex;
					tail = leadingVertex;
					mainPath.add(leadingVertex);
					displayMoveDecision(move);
					return move; // MOVE MADE
				}

			}

			// Main Sequence
			
			
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
			// one opponent colour and other unset,
			//  then return unset;
			Bridge compromisedBridge;
			try {
				compromisedBridge = findCompromisedBridge();
				ComputerVertex linkToSave = compromisedBridge.getSaveableLink();
				int x = linkToSave.getPosition().getXPos();
				int y = linkToSave.getPosition().getYPos();
				move.setPosition(x, y);
				displayMoveDecision(move);
				return move; // save compromised hop (MOVE MADE)
				
			}  catch (NoBridgeFoundException e) {
				// thrown from findCompromisedBridge
				//if caught then no compromised bridge found therefore proceed to check for free hops
			}

			
			// complete the path, select a gap to fill
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
				}  catch (NoBridgeFoundException e) {
					//thrown by findFreeBridge
					//proceed to find random vertex
				}
				 
			}			
			
			boolean hasEndNeighbour = hasFreeEndNeighbour(leadingVertex);
			
			if (!hasEndNeighbour) {
				// check for hop in current direction with free links
				Set<ComputerVertex> freeHops = new HashSet<ComputerVertex>();
				try {
					freeHops = getFreeHops(leadingVertex.getPosition(),
							playingDirection);
					ComputerVertex mostForwardHop = mostForwardVertex(freeHops);
					leadingVertex = mostForwardHop;
					updateLeadingVertex(mostForwardHop);
					Position position = mostForwardHop.getPosition();
					int x = position.getXPos();
					int y = position.getYPos();
					move.setPosition(x, y);
					displayMoveDecision(move);
					resetadjacencyCount();
					changeDirection(); // switch directions
					return move; // place piece (MOVE MADE)
				} catch (EmptySetException e) {
					//thrown by getfreeHops()
					//if caught, then no free hops therefore proceed to find free adjacent vertex
					//				e.printStackTrace();
				}
			}
			else resetadjacencyCount();
			
			boolean tooManyAdjacencies = getAdjacencyCount() == 1;
			
			if (!tooManyAdjacencies) {
				// check for link in current direction
				Set<ComputerVertex> freeVertices = new HashSet<ComputerVertex>();
				try {
					freeVertices = getFreeNeighours(leadingVertex);
					//Make it most in line.
					ComputerVertex mostForward = mostForwardAndInLineVertex(freeVertices, leadingVertex);
					updateLeadingVertex(mostForward);
					int x = mostForward.getPosition().getXPos();
					int y = mostForward.getPosition().getYPos();
					move.setPosition(x, y);
					displayMoveDecision(move);
					incrementAdjacencyCount();
					changeDirection();
					return move; // place piece (MOVE MADE)
				} catch (EmptySetException e) {
					//thrown by getfreeNeighbours
					//if caught, then no free neighbours therefore proceed to find random free vertex
				}
			}
			else
			{
				try {
					resetPath();
					Position nextPos = head.getPosition();
					int x = nextPos.getXPos();
					int y = nextPos.getYPos();
					move.setPosition(x, y);
					return move;
				} catch (NoGoodVertexException e) {
					System.out.println("Move Decision Sequence Failed");
					e.printStackTrace();
				}
			}
			
			//check for random free vertex
			try {
				ComputerVertex randomFreeVertex = anyRandomPosition();
				Position randomPosition = randomFreeVertex.getPosition();
				int x = randomPosition.getXPos();
				int y = randomPosition.getYPos();
				move.setPosition(x, y);
				displayMoveDecision(move);
				
				return move;
			} catch (NoGoodVertexException e) {
				move.setConceded();
				displayMoveDecision(move);
				e.printStackTrace();
				return move;
			}
			
			
			

			
		} catch (InvalidPositionException e) {
			//failsafe in case of flawed decision routine
			move.setConceded();
			displayMoveDecision(move);
			e.printStackTrace();
			return move;
		}

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
	
	private void incrementAdjacencyCount()
	{
		int current = adjacencyCount.get(playingDirection);
		adjacencyCount.put(playingDirection, current + 1);
	}
	
	private int getAdjacencyCount()
	{
		return adjacencyCount.get(playingDirection);
	}
	
	private void resetadjacencyCount()
	{
		adjacencyCount.put(playingDirection, 0);
	}
	
	private void resetPath() throws NoGoodVertexException
	{
		//find new start;
		ComputerVertex newHead = findNewHead();
		mainPath.clear();
		mainPath.add(newHead);
		head = newHead;
		tail = newHead;
		movingForwards = true;
		movingBackwards = true;
		adjacencyCount.put(Direction.FORWARDS, 0);
		adjacencyCount.put(Direction.BACKWARDS, 0);
	}
	
	private ComputerVertex findNewHead() throws NoGoodVertexException
	{
		ComputerVertex newHead;

		int xlim = boardGraph.getXLim();
		int ylim = boardGraph.getYlim();
		// Check Middle
		int xmid = (int) Math.floor(xlim / 2);
		int ymid = (int) Math.floor(ylim / 2);

		try {
			if (playingAxis.equals(Axis.X)) {
				newHead = findGoodPositionInColumn(xmid);
				return newHead;
			} else {
				newHead = findGoodPositionInRow(ymid);
				return newHead;
			}
		} catch (NoGoodVertexException e) {
			// Nothing found Carry On
		}
		
		// Check all other columns
		if (playingAxis.equals(Axis.X)) {
			int increment = 1;
			int nextX = 0;
			while (increment < xmid) {
				if (increment % 2 == 0)
					nextX = xmid - increment;
				else
					nextX = xmid + increment;

				increment++;

				try {
					newHead = findGoodPositionInColumn(nextX);
					return newHead;
				} catch (NoGoodVertexException e) {
					continue;
				}
			}
		} else {
			int increment = 1;
			int nextY = 0;
			while (increment < ymid) {
				if (increment % 2 == 0)
					nextY = ymid - increment;
				else
					nextY = ymid + increment;

				increment++;

				try {
					newHead = findGoodPositionInRow(nextY);
					return newHead;
				} catch (NoGoodVertexException e) {
					continue;
				}
			}
			
		}
		//Nothing good found, return a random position
		newHead = anyRandomPosition();
		return newHead;

//		throw new NoGoodVertexException();
		
		
	}
	//gives first encountered free vertex on board 
	private ComputerVertex anyRandomPosition() throws NoGoodVertexException
	{
		Set<ComputerVertex> allVertices = boardGraph.getAllVertices();
		
		for(ComputerVertex v : allVertices)
		{
			if(!boardGraph.isTaken(v.getPosition()))
				return v;
		}
		
		throw new NoGoodVertexException();
	}
	
	//Checks column x for a vertex with all free neighbours
	private ComputerVertex findGoodPositionInColumn(int x) throws NoGoodVertexException
	{
		int ylim = boardGraph.getYlim();
		int xlim = boardGraph.getXLim();
		if(x < 0 || x >= xlim )
			throw new NoGoodVertexException();
		for(int y = 1; y < ylim; y++)
		{
			Position pos = new Position(x,y);
			boolean isGood = goodPosition(pos);
			if (isGood)
			{
				try {
					ComputerVertex newHead = boardGraph.getVertex(pos);
					return newHead;
				} catch (InvalidPositionException e) {
					continue;
				}
			}
		}
		
		throw new NoGoodVertexException();
	}
	
	//Checks column y for a vertex with all free neighbours
	private ComputerVertex findGoodPositionInRow(int y) throws NoGoodVertexException
	{
		int xlim = boardGraph.getXLim();
		for(int x = 1; x < xlim; x++)
		{
			Position pos = new Position(x,y);
			boolean isGood = goodPosition(pos);
			if (isGood)
			{
				try {
					ComputerVertex newHead = boardGraph.getVertex(pos);
					return newHead;
				} catch (InvalidPositionException e) {
					continue;
				}
			}
		}
		
		throw new NoGoodVertexException();
	}
	
	//Checks if a particular position has all free neighbours
	private boolean goodPosition(Position pos)
	{
		
		try {
			ComputerVertex toCheck = boardGraph.getVertex(pos);
			Set<ComputerVertex> neighbours = getFreeNeighours(toCheck);
			if(neighbours.size() == 6)
				return true;
			else
				return false;
		} catch (InvalidPositionException e) {
			return false;
		} catch (EmptySetException e) {
			return false;
		}
	}
	
	//Updates head/tail of mainPath and pointers depending on playing direction
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
	
	//Checks if next item on main path is a hop.
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
				else if(d.equals(Direction.BACKWARDS) && uIndex > 0)
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
	//checks if next item on main path is adjacent
	private boolean hasNext(ComputerVertex u, Direction d)
	{
		
		if (mainPath.contains(u)) {
			int uIndex = mainPath.indexOf(u);
			int maxIndex = mainPath.size() - 1;
			int minIndex = 0;
			if (d.equals(Direction.FORWARDS) && uIndex < maxIndex) {
				ComputerVertex v = mainPath.get(uIndex + 1);
				boolean adjacent = boardGraph.areAdjacent(u, v);
				return uIndex < maxIndex && adjacent;
			} else if (d.equals(Direction.BACKWARDS) && uIndex > 0) {
				ComputerVertex v = mainPath.get(uIndex - 1);
				boolean adjacent = boardGraph.areAdjacent(u, v);
				return uIndex > minIndex && adjacent;
			} else return false;
		}else return false;
	}
	
	//Checks if any neighbours of u connect to end of board
	private boolean hasFreeEndNeighbour(ComputerVertex u)
	{
		Set<ComputerVertex> neighbours;
		try {
			neighbours = getFreeNeighours(u);
		} catch (EmptySetException e) {
			return false;
		}
		
		for (ComputerVertex v : neighbours) {
			if (v.isEND(colour) || v.isHOME(colour))
				return true;
		}
		return false;
	}
	
	//Changes the direction in which the mainPath is built.
	//Forwards is towards Max coordinate, Backwards is towards 0
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
	
		
	//Gets all hops that get the vertex closer to the Specified direction
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
			
			e.printStackTrace();
		}

		return hops;
	}
	//Same as before but only hops on which a piece can be placed are returned
	private Set<ComputerVertex> getFreeHops(Position position, Direction direction) throws EmptySetException {
		Set<ComputerVertex> hops = new HashSet<ComputerVertex>();

		try {
			ComputerVertex v = boardGraph.getVertex(position);

			for (ComputerVertex u : boardGraph.getHops(v)) {
				if (boardGraph.isTaken(u.getPosition()))
					continue;

				Position uPos = u.getPosition();
				Bridge bridge;
				try {
					bridge = buildBridge(v, u);
					BridgeState state = bridge.getBridgeState(colour);
					if(!state.equals(BridgeState.FREE))
						continue;
				} catch (NoBridgeFoundException e) {
					continue;
				}

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
			
			System.out.println("invalid position exception caught by getFreeHops!");
			e.printStackTrace();
		}

		return hops;
	}
	
	//Gets all adjacent vertices on which a piece can be placed
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
			return freeNeighbours;
		else
			throw new EmptySetException();
	}
	//Returns the vertex that will advance the main path the farthest in playing direction
	private ComputerVertex mostForwardVertex(Set<ComputerVertex> vertices) throws EmptySetException {
		ArrayList<ComputerVertex> vList = new ArrayList<ComputerVertex>(vertices);
		ComputerVertex mostForward;
		if (vList.size() > 0)
			mostForward = vList.get(0);
		else
			throw new EmptySetException();
		for (ComputerVertex v : vertices) {
			boolean biggerXPos = v.getPosition().getXPos() > mostForward.getPosition().getXPos();
			boolean biggerYPos = v.getPosition().getYPos() > mostForward.getPosition().getYPos();
			if (!boardGraph.isTaken(v.getPosition())) {
				if (playingDirection.equals(Direction.FORWARDS)) {
					if (playingAxis.equals(Axis.X) && biggerXPos)
						mostForward = v;
					else if (playingAxis.equals(Axis.Y) && biggerYPos)//playingAxis == y
						mostForward = v;
				} else {
					if (playingAxis.equals(Axis.X)&& !biggerXPos)
						mostForward = v;
					else if (playingAxis.equals(Axis.Y) && !biggerYPos)
						mostForward = v;
				}
			}else continue;
		}
		

		return mostForward;
	}
	
	//Specific for adjacent vertices, makes sure returned vertex is in line with current one
	//(Used to improve opponent blocking)
	private ComputerVertex mostForwardAndInLineVertex(Set<ComputerVertex> vertices, ComputerVertex source) throws EmptySetException {
		ArrayList<ComputerVertex> vList = new ArrayList<ComputerVertex>(vertices);
		ComputerVertex mostForward;
		if (vList.size() > 0)
			mostForward = vList.get(0);
		else
			throw new EmptySetException();
		for (ComputerVertex v : vertices) {
			boolean biggerXPos = v.getPosition().getXPos() >= mostForward.getPosition().getXPos();
			boolean biggerYPos = v.getPosition().getYPos() >= mostForward.getPosition().getYPos();
			boolean smallerXPos = v.getPosition().getXPos() <= mostForward.getPosition().getXPos();
			boolean smallerYPos = v.getPosition().getYPos() <= mostForward.getPosition().getYPos();
			boolean aheadOfSourceX = v.getPosition().getXPos() > source.getPosition().getXPos();
			boolean aheadOfSourceY = v.getPosition().getYPos() > source.getPosition().getYPos();
			boolean inXLine = v.getPosition().getYPos() == source.getPosition().getYPos();
			boolean inYLine = v.getPosition().getYPos() == source.getPosition().getYPos();
			if (!boardGraph.isTaken(v.getPosition())) {
				if (playingDirection.equals(Direction.FORWARDS)) {
					if (playingAxis.equals(Axis.X) && biggerXPos){
						if(inXLine && aheadOfSourceX) return v;
						mostForward = v;
					} else if (playingAxis.equals(Axis.Y) && biggerYPos){//playingAxis == y
						if(inYLine && aheadOfSourceY) return v;
						mostForward = v;
					}
				} else {
					if (playingAxis.equals(Axis.X)&& smallerXPos){
						if(inXLine && aheadOfSourceX) return v;
						mostForward = v;
					} else if (playingAxis.equals(Axis.Y) && smallerYPos){
						if(inYLine  && aheadOfSourceY) return v;
						mostForward = v;
					}
				}
			}else continue;
		}
		

		return mostForward;
	}

	private boolean pathBroken() {
		return !endOfPath(tail, Direction.FORWARDS).equals(head);
	}

	 //traverses main path and returns the end of it
	private ComputerVertex endOfPath(ComputerVertex start, Direction d) {
		//preprocessing
		int startIndex = mainPath.indexOf(start);
		int nextIndex = startIndex + 1;
		if(d.equals(Direction.BACKWARDS))
			nextIndex = startIndex - 1;
		
		boolean nextIsLink = hasNext(start, d);
		boolean nextIsHop = hasNextHop(start, d);
		if (nextIsLink) // recursive case 1
		{
			ComputerVertex next = mainPath.get(nextIndex);
			return endOfPath(next, d);
		} 
		else if (nextIsHop) // recursive case 2
		{
			ComputerVertex next = mainPath.get(nextIndex);
			Bridge bridge;
			try {
				bridge = buildBridge(start, next);
				BridgeState state = bridge.getBridgeState(colour);
				if (!state.equals(BridgeState.LOST))
					return endOfPath(next, d);
				else
					return start;
			} catch (NoBridgeFoundException e) {
				return start;
			}
			
		} 
		else
			return start;
	}
	
	//traverses main path from start vertex till end/break point
	//and returns the length of traversal
	private int pathLength(ComputerVertex start, Direction d) {
		ComputerVertex current = start;
		int currentIndex = mainPath.indexOf(current);
		
		int nextIndex = currentIndex + 1;
		if(d.equals(Direction.BACKWARDS))
			nextIndex = currentIndex - 1;
		boolean hasNext = hasNext(current, d) || hasNextHop(current, d);
		
		int length = 0;
		while (hasNext) {
			boolean nextIsLink = hasNext(start, d);
			boolean nextIsHop = hasNextHop(start, d);
			ComputerVertex next = mainPath.get(nextIndex);
			if (nextIsLink)
				hasNext = true;
			else if (nextIsHop) {
				Bridge bridge;
				try {
					bridge = buildBridge(current, next);
					BridgeState state = bridge.getBridgeState(colour);
					if (!state.equals(BridgeState.LOST))
						hasNext = false;
				} catch (NoBridgeFoundException e) {
					hasNext = false;
				}
			}
			current = next;
			length++;
		}
		return length;
	}
	//Finds a bridge on the main path that opponent is attempting to break 
	private Bridge findCompromisedBridge() throws NoBridgeFoundException {
		
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

				Bridge compromisedBridge = buildBridge(current, next);
				BridgeState state = compromisedBridge.getBridgeState(colour);
				if(state.equals(BridgeState.COMPROMISED))
					return compromisedBridge;	
			}
			i++;
		}
		
		throw new NoBridgeFoundException();

	}
	
	//finds a bridge that has not yet been closed
	private Bridge findFreeBridge(ComputerVertex start) throws NoBridgeFoundException
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
				Bridge freeBridge = buildBridge(current, next);
				BridgeState state = freeBridge.getBridgeState(colour);
				if(state.equals(BridgeState.FREE))
					return freeBridge;
			}
			i++;
		}
		
		throw new NoBridgeFoundException();
		
	}
	
	//constructs a bridge object given two viable vertices
	private Bridge buildBridge(ComputerVertex current, ComputerVertex next) throws NoBridgeFoundException
	{
		try {
			Set<ComputerVertex> linkSet = boardGraph.getLinks(current, next);
			Bridge prospectiveBridge = new Bridge();
			prospectiveBridge.setHops(current, next);
			prospectiveBridge.setLinks(linkSet);
			return prospectiveBridge;
		} catch (EmptySetException e) {
			throw new NoBridgeFoundException();
		}
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
