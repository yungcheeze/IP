package game.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
	private Set<ComputerVertex> mainPath;
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
		mainPath = new HashSet<ComputerVertex>();
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
					head = boardGraph.getVertex(position);
					tail = boardGraph.getVertex(position);
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
					head = boardGraph.getVertex(position);
					tail = boardGraph.getVertex(position);
					move.setPosition(x, y);
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
			// complete path, select a gap to fill (MOVE MADE)
			if(!movingForwards && !movingBackwards)
			{
				Bridge freeBridge;
				try {
					freeBridge = findFreeBridge(tail);
					ComputerVertex linkToUse = freeBridge.getCompromisedLink();
					int x = linkToUse.getPosition().getXPos();
					int y = linkToUse.getPosition().getYPos();
					move.setPosition(x, y);
					ComputerVertex bh = freeBridge.getBackwardHop();
					ComputerVertex fh = freeBridge.getForwardHop();
					bh.setLink(Direction.FORWARDS, linkToUse);
					fh.setLink(Direction.BACKWARDS, linkToUse);
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

			// check if hops in path compromised
			// modified pathTraversal() if links between start and hop contain
			// one
			// opponent colour and other unset, then return unset;
			Bridge compromisedBridge;
			try {
				compromisedBridge = findCompromisedBridge(tail);
				ComputerVertex linkToSave = compromisedBridge.getCompromisedLink();
				int x = linkToSave.getPosition().getXPos();
				int y = linkToSave.getPosition().getYPos();
				move.setPosition(x, y);
				ComputerVertex bh = compromisedBridge.getBackwardHop();
				ComputerVertex fh = compromisedBridge.getForwardHop();
				bh.setLink(Direction.FORWARDS, linkToSave);
				fh.setLink(Direction.BACKWARDS, linkToSave);
				displayMoveDecision(move);
				return move; // save compromised hop (MOVE MADE)
				
			} catch (EmptySetException e1) {
				// thrown from findCompromisedBridge
				//if caught then no compromised bridge found therefore proceed to check for free hops
//				e1.printStackTrace();
			}

			
			
			ComputerVertex leadingVertex = head;
			if(playingDirection.equals(Direction.BACKWARDS))
				leadingVertex = tail;
			
			// check for hop in current direction with free links
			Set<ComputerVertex> freeHops = new HashSet<ComputerVertex>();
			try {
				freeHops = getFreeHops(leadingVertex.getPosition(), playingDirection);
				ComputerVertex mostForwardHop = mostForwardVertex(freeHops);
				leadingVertex.setHop(playingDirection, mostForwardHop);
				leadingVertex = mostForwardHop;
				updateLeadingVertex(mostForwardHop);
				Position position = mostForwardHop.getPosition();
				int x = position.getXPos();
				int y = position.getYPos();
				move.setPosition(x, y);
				displayMoveDecision(move);
				playingDirection = playingDirection.otherDirection(); // switch directions
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
				playingDirection = playingDirection.otherDirection();
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
			head.setHop(playingDirection, leadingVertex);
			leadingVertex.setHop(playingDirection.otherDirection(), head);
			head = leadingVertex;
		}
		else
		{
			tail.setHop(playingDirection, leadingVertex);
			leadingVertex.setHop(playingDirection.otherDirection(), tail);
			tail = leadingVertex;
		}
	}
	
	//TODO add changeDirection Method
	//if notMoving forwards and d = backwards
		//no change
	//if notMoving backwards and d = forwards
		//no change
	
	//if movingBackwards and d == forwards
		//change
	//if movingForwards and d== backwards
		//change
	
	//if d == forwards
		//if movingBackwards
			//change
	//if d == backwards
		//if movingforwards
			//change
		

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

	// from tail to head, returns head
	public ComputerVertex endOfPath(ComputerVertex start, Direction d) {

		if (start.hasHop(d)) {
			ComputerVertex forwardHop = (ComputerVertex) start.getHop(d);
			return endOfPath(forwardHop, d);
		} else
			return start;
	}

	public int pathLength(ComputerVertex start, Direction d) {
		ComputerVertex current = start;
		int length = 0;
		while (current.hasHop(d)) {
			current = (ComputerVertex) current.getHop(d);
			length++;
		}
		return length;
	}

	// modified pathTraversal() if links between start and hop contain one
	// opponent colour and other unset, then return unset;
	public Bridge findCompromisedBridge(ComputerVertex start) throws EmptySetException {
		Direction d = Direction.FORWARDS;

		if (start.hasHop(d)) {
			ComputerVertex forwardHop = (ComputerVertex) start.getHop(d);
			Set<ComputerVertex> links = boardGraph.getLinks(start, forwardHop);
			HashMap<Piece, ComputerVertex> colourMap = new HashMap<Piece, ComputerVertex>();

			for (ComputerVertex link : links) {
				Piece colour = link.getColour();
				colourMap.put(colour, link);
			}

			Piece otherColour = opponentsColour();

			if (colourMap.containsKey(Piece.UNSET) && colourMap.containsKey(otherColour)) {
				Bridge bridge = new Bridge();
				bridge.setBackwardHop(start);
				bridge.setForwardHop(forwardHop);
				bridge.setLinks(links);
				bridge.setCompromisedLink(colourMap.get(Piece.UNSET));
				return bridge;

			} else
				return findCompromisedBridge(forwardHop);
		} else
			throw new EmptySetException();

	}
	
	private Bridge findFreeBridge(ComputerVertex start) throws EmptySetException
	{
		Direction d = Direction.FORWARDS;
		if (start.hasHop(d)) {
			ComputerVertex forwardHop = (ComputerVertex) start.getHop(d);
			Set<ComputerVertex> links = boardGraph.getLinks(start, forwardHop);
			
			HashMap<Piece, ComputerVertex> colourMap = new HashMap<Piece, ComputerVertex>();

			for (ComputerVertex link : links) {
				Piece colour = link.getColour();
				colourMap.put(colour, link);
			}
			
			if (!colourMap.containsKey(Piece.RED) && !colourMap.containsKey(Piece.BLUE)) {
				Bridge bridge = new Bridge();
				bridge.setBackwardHop(start);
				bridge.setForwardHop(forwardHop);
				bridge.setLinks(links);
				bridge.setCompromisedLink(colourMap.get(Piece.UNSET));
				return bridge;
			}
			else 
				return findFreeBridge(forwardHop);	
		}
		else
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
