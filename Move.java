/**
 * Created by Kieran on 2/2/17.
 *
 * A move in minesweeper is a row, column and action (OPEN FLAG UN-FLAG SWEEP)
 */
public class Move {

    /**
     * the row of a cell you want to move
     */
    private int row;

    /**
     * the column of the cell
     */
    private int column;

    /**
     * what you want to do to the cell
     */
    private int action;

    public final static int OPEN = 0;
    public final static int FLAG = 1;
    public final static int UNFLAG = 2;
    public final static int SWEEP = 3;

    /**
     * make a new Move
     * @param row row of cell
     * @param column column of cell
     * @param action action to do
     */
    public Move (int row, int column, int action){
        this.row = row;
        this.column = column;
        this.action = action;
    }

    /**
     * @return the row
     */
    public int row(){
        return row;
    }

    /**
     * @return the column
     */
    public int column(){
        return column;
    }

    /**
     * @return the action
     */
    public int action(){
        return action;
    }
}
