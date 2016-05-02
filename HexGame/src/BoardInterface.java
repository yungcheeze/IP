


/**
 * This interface is used to define how an instance of a Board can be communicated with.
 * 
 * DO NOT CHANGE THIS FILE
 * 
 * @author Stephen McGough
 * @version 0.1
 */
public interface BoardInterface
{
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
    public boolean setBoardSize(int sizeX, int sizeY) throws InvalidBoardSizeException, BoardAlreadySizedException;
    
    /**
     * This method will return a two dimentional array of Pieces which represents the current state of the 
     * board. As this is just a copy of the data it is safe to send to a Player.
     * 
     * @returns Piece[][]  a two dimentional representation of the game board.
     * 
     * @throws  NoBoardDefinedException   Thrown when a call is made to this method before the boardSize 
     * method.
     */
    public Piece[][] getBoardView() throws NoBoardDefinedException;
    
    /**
     * Places a piece on the board at the specified location.
     * 
     * @param colour     the colour of the piece to place (RED or BLUE)
     * @param move       the position where you wish to place a piece
     * @return boolean   true if the piece was placed successfully
     * 
     * @throws PositionAlreadyTakenException   if there is already a Piece in this position
     * @throws InvalidPositionException        if the specified position is invalid - e.g. (-1, -1)
     * @throws InvalidColourException          if the colour being set is invalid. E.g. if you try to place two BLUE pieces one after the other
     */
    public boolean placePiece(Piece colour, MoveInterface move) throws PositionAlreadyTakenException, InvalidPositionException, InvalidColourException, NoBoardDefinedException;
    
    /**
     * Checks to see if either player has won.
     * 
     * @return Piece   RED if red has won, BLUE if blue has won, UNSET if neither player has won.
     * 
     * @throws NoBoardDefinedException  Indicates that this method has been called before the boardSize 
     * method
     */
    public Piece gameWon() throws NoBoardDefinedException;
}
