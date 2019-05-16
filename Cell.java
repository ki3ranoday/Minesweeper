/**
 * Created by Kieran on 1/31/17.
 *
 * Each cell in a minesweeper game
 * Either a bomb or not
 * either open closed or flagged
 */
public class Cell {
    /**
     * if the cell has a bomb in it or not
     */
    private boolean hasBomb;

    /**
     * if the cell is open closed or flagged
     */
    private int state;

    private final static int OPEN = 0;
    private final static int CLOSED = 1;
    private final static int FLAGGED = 2;


    /**
     * basic contructor is a closed cell that has no bomb
     */
    public Cell(){
        state = CLOSED;
        hasBomb = false;
    }
    /**
     * constructor that takes in a boolean for if the cell has a bomb or not
     * still makes the cell closed to start
     */
    public Cell(boolean bomb){
        state = CLOSED;
        hasBomb = bomb;
    }
    /**
     * @return wether or not the cell has a bomb in it
     */
    public boolean hasBomb(){
        return hasBomb;
    }
    /**
     * @return if the cell is open
     */
    public boolean isOpen(){
        return state == OPEN;
    }
    /**
     * @return if the cell is closed
     */
    public boolean isClosed(){
        return state == CLOSED;
    }/**
     * @return if the cell is flagged
     */
    public boolean isFlagged(){
        return state == FLAGGED;
    }
    /**
     * open the cell but only if the cell is closed
     */
    public void open(){
        if(isClosed())
            state = OPEN;
    }
    /**
     * flag the cell but only if the cell is open
     */
    public void flag(){
        if(isClosed()) {
            state = FLAGGED;
        }
    }
    /**
     * un-flag the cell but only if the cell is flagged
     */
    public void unFlag(){
        if(isFlagged())
            state = CLOSED;
    }

}
