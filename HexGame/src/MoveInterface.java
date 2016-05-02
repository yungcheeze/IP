



/**
 * Represents a move a player wishes to make
 * 
 * DO NOT CHANGE THIS FILE
 * 
 * @author Stephen McGough
 * @version 0.1
 */
public interface MoveInterface
{
    /**
     * Set the position that the Player wishes to use - both x and y coordinate.
     * 
     * @param x   the x coordinate
     * @param y   the y coordinate
     * @return    true indicating value set correctly
     * 
     * @throws  InvalidPositionException   The position is invalid. E.g. both x and y are negative.
     */
    public boolean setPosition(int x, int y) throws InvalidPositionException;
    
    /**
     * Has the player conceded in this move?
     * i.e. have they yielded to the fact that their opponent has won before all required
     * moves are made.
     *
     * @return true if the player has conceded.
     */
    public boolean hasConceded();
    
    /**
     * Get the x coordinate of the move.
     * 
     * @return the x coordinate.
     */
    public int getXPosition();
    
    /**
     * Get the y coordnate of the move.
     * 
     * @return the y coordinate.
     */
    public int getYPosition();
    
    /**
     * Indicate that the player has conceded in this move.
     * 
     * @return true indicating conceded is set.
     */
    public boolean setConceded();
}
