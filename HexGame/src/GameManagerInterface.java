



/**
 * GameManagerInterface provides an abstract interface to a GameManager Class that should be implemented.
 * 
 * DO NOT CHANGE THIS FILE
 * 
 * @author Stephen McGough
 * @version 0.1
 */
public interface GameManagerInterface
{
    /**
     * Define who will be playing each colour. This method will be called twice for each game once for
     * RED and once for BLUE.
     * 
     * @param  player     the player who will be playing red
     * @param  colour     the enum for a Piece (RED or BLUE)
     * @return boolean    true if the player was successfully set to the specified colour
     * 
     * @throws ColourAlreadySetException  If the colour is alredy allocated to a player
     */
    public boolean specifyPlayer(PlayerInterface player, Piece colour) throws InvalidColourException, ColourAlreadySetException;

    /**
     * Specifiy the size of the board that we are playing on. Both numbers must be greater than zero
     * 
     * @param  sizeX      how wide the board will be
     * @param  sizeY      how tall the board will be
     * @returns boolean   true if the board could be set successfully
     * 
     * @throws InvalidBoardSizeException  If either size value is less than one.
     * @throws BoardAlreadySizedException If the board has already been created.
     */
    public boolean boardSize(int sizeX, int sizeY) throws InvalidBoardSizeException, BoardAlreadySizedException;
    
    /**
     * The core of the game manager. This requests each player to make a move and plays these out on the 
     * game board.
     */
    public boolean playGame();
}
