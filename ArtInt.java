import java.util.ArrayList;

/**
 * Created by Kieran on 2/2/17.
 * <p>
 * a Minesweeper AI that uses neat logic to win a lot of games
 */

public class ArtInt implements Player {

    private Board board;

    private ArrayList<Move> pendingMoves;
    private ArrayList<Guess> possibleGuesses;
    private rcPair bestGuess;
    private double guessFailRate;

    public ArtInt(Board board) {
        this.board = board;
        pendingMoves = new ArrayList<>();
    }

    /**
     * To make a move
     * go through every cell and check if the cell is open,
     * then see if the number of nearby closed cells is the same as the number of nearby bombs
     * if so, flag all those cells because they are bombs
     * if no more bombs to flag
     * go through the open cells, and see if the number of nearby flags is the same as the number of nearby bombs
     * if so, sweep on that cell
     * or
     * find a border
     * and find a move based off that border
     * or
     * find the best guess to make
     */
    public Move makeMove() {
        Move move;
        bestGuess = null;
        guessFailRate = 100;
        possibleGuesses = new ArrayList<>();

        if (!board.noNullCells())
            return new Move(board.numRows() / 2, board.numColumns() / 2, Move.OPEN);

        if (pendingMoves.size() > 0) {
            Move m = pendingMoves.get(0);
            pendingMoves.remove(0);
            return m;
        }

        move = findPlaceToFlag();
        if (move != null)
            return move;

        move = findPlaceToSweep();
        if (move != null)
            return move;

        for (int r = 0; r < board.numRows(); r++) {
            for (int c = 0; c < board.numColumns(); c++)
                if (!board.cellIsNull(r, c) && board.cellIsOpen(r, c) && board.nearbyFlags(r, c) != board.nearbyBombs(r, c)){
                    //for (int i = 4; i < 10; i++) {
                    int i = 13;
                    Border border = new Border(r, c, board, i);
                    move = makeMove(border);
                    if (move != null)
                        return move;
                }
            if (board.numClosedCells() < 23)
                break;
        }
        return bestGuess();
    }

    /**
     * finds a cell that is safe to flag on
     *
     * @return the move it finds
     */
    private Move findPlaceToFlag() {
        for (int r = 0; r < board.numRows(); r++)
            for (int c = 0; c < board.numColumns(); c++)
                if (board.cellIsOpen(r, c) && board.nearbyBombs(r, c) >= board.nearbyClosed(r, c) + board.nearbyFlags(r, c))
                    for (int nr = r - 1; nr <= r + 1; nr++)
                        for (int nc = c - 1; nc <= c + 1; nc++)
                            if (board.cellIsClosed(nr, nc))
                                return new Move(nr, nc, Move.FLAG);
        return null;
    }

    /**
     * finds a cell that is safe to sweep on
     *
     * @return the move it finds
     */
    private Move findPlaceToSweep() {
        for (int r = 0; r < board.numRows(); r++)
            for (int c = 0; c < board.numColumns(); c++)
                if (board.cellIsOpen(r, c) && board.nearbyClosed(r, c) != 0)
                    if (board.nearbyBombs(r, c) == board.nearbyFlags(r, c))
                        return new Move(r, c, Move.SWEEP);
        return null;
    }

    /**
     * chooses a random cell to open
     *
     * @return a random cell
     */
    private Move randomMove() {
        int row = (int) (Math.random() * board.numRows());
        int column = (int) (Math.random() * board.numColumns());
        if (board.cellIsNull(row, column) || board.cellIsClosed(row, column)) {
            return new Move(row, column, Move.OPEN);
        }
        return randomMove();
    }

    /**
     * uses the the combos created by the border class to suggest a move
     * if no combo had a bomb placed on any cell - open is guaranteed to be safe
     * if every combo had a bomb on any cell     - flag is guaranteed to be safe
     * also it keeps track of the percentage of failure if you try to open any of the border cells,
     * so if there are no 100% safe options, the AI will choose the best guess available
     *
     * @param b the border it is looking at
     * @return one of the moves it finds
     */
    public Move makeMove(Border b) {
        if (b.numCombos == 0)
            return null;
        for (int i = 0; i < b.bombsPerCell.length; i++) {
            if (b.bombsPerCell[i] == 0) {
                rcPair rc = b.closedBorder.get(i);
                System.out.println("(" + rc.column + "," + rc.row + ") OPEN");
                pendingMoves.add(new Move(rc.row, rc.column, Move.OPEN));
            } else if (b.bombsPerCell[i] == b.numCombos) {
                rcPair rc = b.closedBorder.get(i);
                System.out.println("(" + rc.column + "," + rc.row + ") FLAG");
                pendingMoves.add(new Move(rc.row, rc.column, Move.FLAG));
            } else {
                Guess g = new Guess(b.closedBorder.get(i), (double) b.bombsPerCell[i] / b.numCombos);
                if (!possibleGuesses.contains(g)) {
                    System.out.println("PG: " + g.rc + "  " + g.failRate);
                    possibleGuesses.add(g);
                } else {
                    int index = possibleGuesses.indexOf(g);
                    if (g.failRate < possibleGuesses.get(index).failRate)
                        possibleGuesses.get(index).failRate = g.failRate;
                }
            }
        }
        if (pendingMoves.size() > 0) {
            Move move = pendingMoves.get(0);
            pendingMoves.remove(0);
            return move;
        }
        return null;
    }

    public Move bestGuess() {

        double bestOpenRate = 1;
        Move open = null;
        Move flag = null;
        if (possibleGuesses.size() > 0) {
            for (Guess g : possibleGuesses) {

                if (g.failRate <= bestOpenRate) {
                    bestOpenRate = g.failRate;
                    open = new Move(g.rc.row, g.rc.column, Move.OPEN);
                }
            }
        }
        double randChance = (double) (board.numBombs() - board.numFlags()) / board.numClosedCells();
        if (randChance < bestOpenRate) {
            bestOpenRate = randChance;
            System.out.print("Random ");
            open = randomMove();
        }
        System.out.println("Guess " + open.row() + ":" + open.column() + "  " + bestOpenRate);
        return open;
    }
    //Learned something new today -- if you make a private class inside another class, the outer class can access all the inner class' private variables

    /**
     * a border is a group of unopened cells and the open cells near those that I can run simulations on: placing every combination of flags
     * on and seeing which combos are valid, then which cells never can be a bomb or has to be a bomb
     */
    private class Border {
        /**
         * a list of the locations on the board of all the closed cells included in the border
         */
        private ArrayList<rcPair> closedBorder;
        /**
         * a list of all the open cells included in the border
         */
        private ArrayList<rcPair> openBorder;
        /**
         * a list of the number of combonations that include a bomb at each closed border cell
         */
        private int[] bombsPerCell;
        /**
         * the total number of valid combinations of bombs on any border
         */
        private int numCombos;
        /**
         * the board that the border is defined on
         */
        private Board b;
        private boolean endGame;

        /**
         * creates a new border with a genesis cell
         *
         * @param row    the row of the genesis cell
         * @param column the column of the genesis cell
         * @param b      the board that the border is being created on
         * @param length the length that the border should be
         */
        private Border(int row, int column, Board b, int length) {
            this.b = b;
            closedBorder = new ArrayList<>();
            openBorder = new ArrayList<>();
            defineBorder(row, column, length);
            bombsPerCell = new int[closedBorder.size()];
            for (int i = 0; i < bombsPerCell.length; i++)
                bombsPerCell[i] = 0;
            createCombos();
            if (b.numClosedCells() < 23)
                endGame = true;
            else
                endGame = false;
        }

        /**
         * make a border of a certain length by starting at one open cell, adding all the nearby closed cells to the closed border,
         * then finding another open cell near each closed cell, and adding the border defined from that cell until either there are no more
         * unfinished open cells near any of the closed border, or there are more than the specified length of open cells included in the border
         * <p>
         * during the end game, define border just takes every closed cell and any open cell that touches a closed cell
         *
         * @param row    row of the open cell to find closed cells off of, the genesis cell
         * @param column column of that cell
         * @param length max length of the border - this is just really a limit on the recursion
         *               because length 2 means that it could recur 2 times which might add more than two open cells to the border
         */
        private void defineBorder(int row, int column, int length) {
            if (endGame) {
                //System.out.println("END GAME");
                for (int r = 0; r < b.numRows(); r++) {
                    for (int c = 0; c < b.numColumns(); c++) {
                        if (b.cellIsClosed(r, c))
                            closedBorder.add(new rcPair(r, c));
                        if (b.cellIsOpen(r, c) && b.nearbyClosed(r, c) > 0)
                            openBorder.add(new rcPair(r, c));
                    }
                }
                return;
            }
            if (b.cellIsOpen(row, column)) {
                if (!openBorder.contains(new rcPair(row, column)))
                    openBorder.add(new rcPair(row, column));
                for (int r = row - 1; r <= row + 1; r++)
                    for (int c = column - 1; c <= column + 1; c++)
                        if (r > -1 && r < b.numRows() && c > -1 && c < b.numColumns())
                            if (b.cellIsClosed(r, c)) {
                                rcPair rc = new rcPair(r, c);
                                if (!closedBorder.contains(rc)) {
                                    closedBorder.add(rc);
                                    for (int nr = r - 1; nr <= r + 1; nr++)
                                        for (int nc = c - 1; nc <= c + 1; nc++)
                                            if (nr > -1 && nr < b.numRows() && nc > -1 && nc < b.numColumns())
                                                if (b.cellIsOpen(nr, nc))
                                                    if (b.nearbyFlags(nr, nc) != b.nearbyBombs(nr, nc))
                                                        if (!openBorder.contains(new rcPair(nr, nc)))
                                                            if (closedBorder.size() <= length) {
                                                                openBorder.add(new rcPair(nr, nc));
                                                                defineBorder(nr, nc, length - 1);
                                                            }
                                }
                            }
            }
        }

        /**
         * unflags all the cells on the border
         */
        private void unflagAll() {
            for (rcPair rc : closedBorder) {
                b.unFlagCell(rc.row, rc.column);
            }
        }

        /**
         * a cleaner version of the create combos method call, just calls a new create combos function
         */
        private void createCombos() {
            //System.out.println("border is " + closedBorder.size() + "long; " + (int) Math.pow(2, closedBorder.size()));
            createCombos(0, new boolean[closedBorder.size()]);
        }

        /**
         * This spits out every possible combination of flags or no flags by building up both a true and false chain off each possible chain,
         * until the chains are complete, then the completed chains are checked for validity
         * good recursion funtion
         *
         * @param index   the index that the function can change, anything before this index is already set in stone, anything after will be set using recursion
         * @param flagged the array of booleans that coincides with the array list of closed border cells
         *                true means flag, false means no flag
         */
        private void createCombos(int index, boolean[] flagged) {
            if (index == flagged.length) {
                checkCombo(flagged);
                return;
            }
            flagged[index] = true;
            createCombos(index + 1, flagged);
            flagged[index] = false;
            createCombos(index + 1, flagged);
        }

        /**
         * checks a combination by flagging what should be flagged and seeing if all of the open border cells have the right number of flags now
         * if the combo is a valid combo, the function updates the bombs per cell count by adding 1 to each cell's count that has a flag on it
         *
         * @param flagged the boolean [] of whether the cells are flagged or not
         * @return
         */
        private boolean checkCombo(boolean[] flagged) {
            unflagAll();
            for (int i = 0; i < flagged.length; i++) {
                if (flagged[i])
                    board.flagCell(closedBorder.get(i).row, closedBorder.get(i).column);
            }
            if (endGame)
                if (board.numBombs() != board.numFlags())
                    return false;
            for (rcPair rc : openBorder)
                if (board.nearbyFlags(rc.row, rc.column) != board.nearbyBombs(rc.row, rc.column))
                    return false;

            for (int i = 0; i < flagged.length; i++) {
                if (flagged[i])
                    bombsPerCell[i]++;
            }
            numCombos++;
            return true;
        }
    }

    /**
     * an row column pair is just a convenient way to store locations on the board, and make it possible to use contains on a list of cell locations
     */
    public class rcPair {
        private int row;
        private int column;

        private rcPair(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public boolean equals(Object o) {
            if (o instanceof rcPair)
                if (((rcPair) o).row == row && ((rcPair) o).column == column)
                    return true;
            return false;
        }

        public String toString() {
            return "(" + column + "," + row + ")";
        }
    }

    public class Guess {
        private rcPair rc;
        private double failRate;

        public Guess(rcPair rc, double failRate) {
            this.rc = rc;
            this.failRate = failRate;
        }

        public boolean equals(Object o) {
            if (o instanceof Guess)
                if (((Guess) o).rc.equals(this.rc))
                    return true;
            return false;
        }
    }
}
