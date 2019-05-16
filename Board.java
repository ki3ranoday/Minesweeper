/**
 * Created by Kieran on 1/31/17.
 * <p>
 * Keeps track of all the cells in a grid
 */
public class Board {

    /**
     * grid of cells
     */
    private Cell[][] grid;

    /**
     * these are just for convenience, numRows = grid.length, numColumns = grid[0].length
     */
    private int numRows, numColumns;

    /**
     * number of bombs on the board
     */
    private int numBombs;

    /**
     * number of flags on the board
     */
    private int numFlags;

    public final static int EASY = 0, MEDIUM = 1, HARD = 2;

    /**
     * makes a default 20x15 grid with 20% of the total cells bombs
     */
    public Board() {
        numRows = 15;
        numColumns = 20;
        grid = new Cell[numRows][numColumns];
        numBombs = (int) (.2 * numRows * numColumns);
        numFlags = 0;
    }

    /**
     * sets the board dimensions and number of bombs to the standard minesweeper settings, if the parameter is not a valid difficulty it just sets up a hard board
     *
     * @param difficulty
     */
    public Board(int difficulty) {
        if (difficulty == EASY) {
            numRows = 9;
            numColumns = 9;
            numBombs = 10;
        } else if (difficulty == MEDIUM) {
            numRows = 16;
            numColumns = 16;
            numBombs = 40;
        } else {
            numRows = 16;
            numColumns = 30;
            numBombs = 99;
        }
        numFlags = 0;
        grid = new Cell[numRows][numColumns];
    }

    /**
     * Creates a board of a certain size with a certain number of bombs
     *
     * @param numRows    the number of rows in the grid
     * @param numColumns the number of columns in the grid
     * @param numBombs   the number of bombs in a grid
     */
    public Board(int numRows, int numColumns, int numBombs) {
        this.numRows = numRows;
        this.numColumns = numColumns;
        grid = new Cell[numRows][numColumns];
        this.numBombs = numBombs;
        numFlags = 0;
    }

    /**
     * given a specific row and column, distribute the bombs on the screen, then open the first move
     * the given cell and all cells around it cannot be bombs
     * to do this:
     * a) set the given cell and its surrounding cells to cells without bombs
     * b) use spawnRandomBomb to make numBombs bombs
     * c) open the given cell
     */
    public void setupCells(int row, int column) {
        if (!noNullCells()) {
            if (row > -1 && row < numRows && column > -1 && column < numColumns) {
                for (int r = row - 1; r <= row + 1; r++)
                    for (int c = column - 1; c <= column + 1; c++)
                        if (r > -1 && r < numRows && c > -1 && c < numColumns)
                            grid[r][c] = new Cell();
                for (int i = 0; i < numBombs; i++) {
                    spawnBomb();
                }
                for (int r = 0; r < numRows; r++)
                    for (int c = 0; c < numColumns; c++)
                        if (grid[r][c] == null)
                            grid[r][c] = new Cell();
                openCell(row, column);
            }
        }
    }

    /**
     * This one sets up the cells based on a huge random number, then it use %
     * to make random numbers between 0 and the number of null cells left to fill and the bomb is placed based on that
     * helpful because then you can play the same game twice; each game has an id
     * @param row the first move's row
     * @param column the first move's column
     * @param randomNum the huge random number
     */
    public void setupCells(int row, int column, int randomNum){
        int cellsLeft = numRows * numColumns - 9;
        if (!noNullCells()) {
            if (row > -1 && row < numRows && column > -1 && column < numColumns) {
                for (int r = row - 1; r <= row + 1; r++)
                    for (int c = column - 1; c <= column + 1; c++)
                        if (r > -1 && r < numRows && c > -1 && c < numColumns)
                            grid[r][c] = new Cell();
            }
        }
        for(int b = 0; b < numBombs; b ++){
            int selectedSpot = randomNum % cellsLeft; //0 - cells left - 1
            int index = -1;//starts at -1 because the index is added to immediately so it really starts at 0
            for(int r = 0; r < numRows; r ++) {
                for (int c = 0; c < numColumns; c++) {
                    if (cellIsNull(r, c)) {
                        index++;
                        if (index == selectedSpot) {
                            grid[r][c] = new Cell(true);
                            cellsLeft--;
                        }
                    }
                }
            }
        }
        for (int r = 0; r < numRows; r++)
            for (int c = 0; c < numColumns; c++)
                if (grid[r][c] == null)
                    grid[r][c] = new Cell();
        openCell(row, column);
        System.out.println("NUmBombs = " + numBombs());
    }

    /**
     * spawn a random bomb on the grid, it cannot spawn on a spot that already has anything, so if that happens just try again until the spot you choose is empty
     */
    public void spawnBomb() {
        int r = (int) (Math.random() * (numRows));
        int c = (int) (Math.random() * (numColumns));
        if (grid[r][c] == null)
            grid[r][c] = new Cell(true);
        else
            spawnBomb();
    }

    /**
     * open the given cell, if the board is not set up yet, setup the board with a big random number
     *
     * @param row    the row that the cell is in
     * @param column the column that the cell is in
     */
    public void openCell(int row, int column) {
        if (row > -1 && row < numRows && column > -1 && column < numColumns) {
            if (grid[row][column] == null) {
                int x = 1000000000 + (int) (Math.random() * 999999999);
                //x = 1493660834; // to try a board again copy and paste from the console log and uncomment this line
                setupCells(row, column, x);
                System.out.println(x);
            } else {
                grid[row][column].open();
                if (nearbyBombs(row, column) == 0)
                    sweepCell(row, column);//Even though this isn't clear recursion, it actually is recursion because sweep cell calls open cell inside of it
            }
        }
    }

    /**
     * flag the given cell
     *
     * @param row    the row that the cell is in
     * @param column the column that the cell is in
     */
    public void flagCell(int row, int column) {
        if (row > -1 && row < numRows && column > -1 && column < numColumns) {
            if (grid[row][column].isClosed()) {
                grid[row][column].flag();
                numFlags++;
            }
        }
    }

    /**
     * un-flag the given cell
     *
     * @param row    the row that the cell is in
     * @param column the column that the cell is in
     */
    public void unFlagCell(int row, int column) {
        if (row > -1 && row < numRows && column > -1 && column < numColumns) {
            if (grid[row][column].isFlagged()) {
                grid[row][column].unFlag();
                numFlags--;
            }
        }
    }

    /**
     * @return number of flags on the board
     */
    public int numFlags() {
        return numFlags;
    }

    /**
     * sweep the given cell if the number of nearby bombs is equal to the number of nearby flags and the cell is open
     *
     * @param row    the row that the cell is in
     * @param column the column that the cell is in
     */
    public void sweepCell(int row, int column) {
        if (grid[row][column].isOpen())
            if (nearbyBombs(row, column) == nearbyFlags(row, column))
                for (int r = row - 1; r <= row + 1; r++)
                    for (int c = column - 1; c <= column + 1; c++)
                        if (r > -1 && r < numRows && c > -1 && c < numColumns)
                            if (grid[r][c].isClosed()) //not flagged or open
                                openCell(r, c);
    }

    /**
     * calculates the number of bombs that are in the adjacent spaces of an open cell
     *
     * @param row    the cell's row
     * @param column the cell's column
     * @return
     */
    public int nearbyBombs(int row, int column) {
        int count = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = column - 1; c <= column + 1; c++) {
                if (r > -1 && r < numRows && c > -1 && c < numColumns)
                    if (grid[r][c].hasBomb())
                        count++;
            }
        }
        return count;
    }

    /**
     * calculates the number of flags that are in the adjacent spaces of an open cell
     *
     * @param row    the cell's row
     * @param column the cell's column
     * @return
     */
    public int nearbyFlags(int row, int column) {
        int count = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = column - 1; c <= column + 1; c++) {
                if (r > -1 && r < numRows && c > -1 && c < numColumns)
                    if (grid[r][c].isFlagged())
                        count++;
            }
        }
        return count;
    }

    /**
     * calculates the number of closed cells that are in the adjacent spaces of an open cell
     *
     * @param row    the cell's row
     * @param column the cell's column
     * @return
     */
    public int nearbyClosed(int row, int column) {
        int count = 0;
        for (int r = row - 1; r <= row + 1; r++) {
            for (int c = column - 1; c <= column + 1; c++) {
                if (r > -1 && r < numRows && c > -1 && c < numColumns)
                    if (grid[r][c].isClosed())
                        count++;
            }
        }
        return count;
    }

    /**
     * Checks to see if all the cells have been set up
     *
     * @return true if all the cells have been set up false if there are any empty cells
     */
    public boolean noNullCells() {
        for (Cell[] row : grid) {
            for (Cell c : row) {
                if (c == null)
                    return false;
            }
        }
        return true;
    }

    public int numClosedCells() {
        int count = 0;
        for (int r = 0; r < numRows; r++)
            for (int c = 0; c < numColumns; c++)
                if (grid[r][c].isClosed())
                    count++;
        return count;
    }

    /**
     * The number of cells on the board
     *
     * @return the number of cells
     */
    public int numOpenCells() {
        int count = 0;

        for (Cell[] row : grid)
            for (Cell c : row)
                if (c == null || c.isOpen())
                    count++;
        return count;
    }

    /**
     * Opens all the bombs on the board (when you lose)
     */
    public void openAllBombs() {
        for (Cell[] row : grid) {
            for (Cell x : row) {
                if (x.hasBomb())
                    x.open();
            }
        }
    }
    public void unFlagAll() {
        for (Cell[] row : grid) {
            for (Cell x : row) {
                if (x.isFlagged())
                    x.unFlag();
            }
        }
    }

    /**
     * returns if the cell is open
     *
     * @param row    cells row
     * @param column cells column
     * @return if the cell is open
     */
    public boolean cellIsOpen(int row, int column) {
        if (row < 0 || row >= numRows || column < 0 || column >= numColumns())
            return false;
        return grid[row][column].isOpen();
    }

    /**
     * returns if the cell is closed
     *
     * @param row    cells row
     * @param column cells column
     * @return if the cell is closed
     */
    public boolean cellIsClosed(int row, int column) {
        if (row < 0 || row >= numRows || column < 0 || column >= numColumns())
            return false;
        return grid[row][column].isClosed();
    }

    /**
     * returns if the cell is flagged
     *
     * @param row    cells row
     * @param column cells column
     * @return if the cell is flagged
     */
    public boolean cellIsFlagged(int row, int column) {
        if (row < 0 || row >= numRows || column < 0 || column >= numColumns())
            return false;
        return grid[row][column].isFlagged();
    }

    /**
     * returns if the cell is null
     *
     * @param row    cells row
     * @param column cells column
     * @return if the cell is null
     */
    public boolean cellIsNull(int row, int column) {
        if (row < 0 || row >= numRows || column < 0 || column >= numColumns())
            return true;
        return grid[row][column] == null;
    }

    /**
     * returns if the cell has a bomb
     *
     * @param row    cells row
     * @param column cells column
     * @return if the cell has a bomb
     */
    public boolean cellHasBomb(int row, int column) {
        if (row < 0 || row >= numRows || column < 0 || column >= numColumns())
            return false;
        return grid[row][column].hasBomb();
    }

    /**
     * the number of rows on the board
     */
    public int numRows() {
        return numRows;
    }

    /**
     * the number of columns on the board
     */
    public int numColumns() {
        return numColumns;
    }

    /**
     * the number of bombs on the board
     */
    public int numBombs() {
        int count = 0;
        for(Cell [] row: grid)
            for(Cell c: row)
                if(c.hasBomb())
                    count ++;
        return count;
    }

    /**
     * checks to see if any bombs on the board have been opened
     * @return if there is an open bomb
     */
    public boolean bombOpened(){
        for(Cell [] row: grid)
            for(Cell c : row)
                if(c != null && c.isOpen())
                    if(c.hasBomb())
                        return true;
        return false;
    }
}
