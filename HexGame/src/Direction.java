

public enum Direction {
FORWARDS("f"), BACKWARDS("b")/*, STATIC*/;
	//TODO if I decide to use static need to create invalidDirectionException for ComputerVertex methods
	private final String dirString;
	private Direction(String dirString)
	{
		this.dirString = dirString;
	}
	
	public Direction otherDirection()
	{
		if(dirString.equals("f"))
			return Direction.BACKWARDS;
		else
			return Direction.FORWARDS;
				
	}
	
}
