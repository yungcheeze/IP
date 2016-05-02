

import java.util.Scanner;


public class BoardViewGenerator 
{

	private Piece[][] board;
	private int xSize;
	private int ySize;
	
	
	public BoardViewGenerator(Piece[][] boardView)
	{
		board = boardView;
		xSize = board.length;
		ySize = board[0].length;
	}
	
	
	public void PrintBoard()
	{
		
		String indent = "";
		String yLine = "|    ";
		
		System.out.println("***CURRENT BOARD***");
		System.out.println();
		printXAxis(yLine);
		printRedBorder(yLine);
		
		for (int y = 0; y < ySize; y++)
		{
			String augmentedYLine = yLine.trim() + " " + indent + y +" " + "B";
			System.out.print(augmentedYLine);
			for (int x = 0; x < xSize; x++)
			{
				Piece p = board[x][y];
				String slot = pieceToString(p); 
				System.out.print(slot);
			}
			System.out.print("B\n");
			indent += " ";
					
		}
		
		printRedBorder(yLine + indent);
		printYLabel();
		
	}
	
	private void printXAxis(String yLine)
	{
		//axis line
		System.out.print("----");
		
		for(int i = 0; i < xSize; i++)
			System.out.print("---");
		
		System.out.println("> x-axis");
		
		//numbers
		System.out.print(yLine);
		for(int i = 0; i < xSize; i++)
			System.out.print(" " + i + " ");
		
		System.out.print("\n");
		
	
	}
	
	private void printRedBorder(String yLine)
	{
		System.out.print(yLine);
		for(int i = 0; i < xSize; i++)
			System.out.print(" R ");
		
		System.out.print("\n");
	}
	
	private void printYLabel()
	{
		System.out.print("v \ny-axis \n\n");
		
	}
	
	private String pieceToString(Piece piece)
	{
		switch(piece)
		{
			case RED:
				return "[R]";
			case BLUE:
				return "[B]";
			default:
				return "[ ]";
		}
	}
}
