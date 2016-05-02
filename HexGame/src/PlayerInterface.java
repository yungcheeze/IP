



/**
 * This interface represents a player within the game of Hex and the methods that a player should make
 * 
 * DO NOT CHANGE THIS FILE
 * 
 * @author Stephen McGough
 * @version 0.1
 */
public interface PlayerInterface
{
    /**
     * Ask the player to make a move.
     * 
     * @param  boardView   the current state of the board
     * @return        a Move object representing the desired place to place a piece
     * 
     * @throws NoValidMovesException Indicates that no valid moves are possible - e.g. all cells on the
     * board are already occupied by a piece.
     */
    public MoveInterface makeMove(Piece[][] boardView) throws NoValidMovesException;
    
    /**
     * Set the colour that this player will be
     * 
     * @param colour  A Piece (RED/BLUE) that this player will be
     * @return   true indicating that the method succeeded
     * 
     * @throws InvalidColourException   A colour other than RED/BLUE was provided
     * @throws ColourAlreadySetException  The colour has already been set for this player.
     */
    public boolean setColour(Piece colour) throws InvalidColourException, ColourAlreadySetException;
    
    /**
     * Informs the player of the final game state. Player has Won, lost.
     * 
     * @param state   either WON or LOST
     * @return   true indicating method has compleated successfully.
     * 
     */
    public boolean finalGameState(GameState state);
}
