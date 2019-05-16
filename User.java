import processing.core.PApplet;

/**
 * Created by Kieran on 2/11/17.
 * A user's move is calculated based on where his mouse is and what button he presses
 * A user can also use keyboard input instead of a mouse
 */
public class User implements Player {

    private Board board;
    private BoardDisplay bd;
    private PApplet p;

    public User(Board board, BoardDisplay b, PApplet p) {
        this.board = board;
        bd = b;
        this.p = p;
    }

    public Move makeMove() {
        int action = Move.OPEN;
        int row = bd.rowAt(p.mouseY);
        int column = bd.columnAt(p.mouseX);
        if (p.mouseButton == p.LEFT) {
            action = Move.OPEN;
        } else {
            if (board.cellIsClosed(row, column))
                action = Move.FLAG;
            else if (board.cellIsFlagged(row, column))
                action = Move.UNFLAG;
            else if (board.cellIsOpen(row, column))
                action = Move.SWEEP;
        }
        return new Move(row, column, action);
    }
}
