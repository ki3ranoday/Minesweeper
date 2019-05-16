/**
 * Created by Kieran on 1/31/17.
 * Due: not sure
 * Minesweeper:
 Requirements
 + Cells are in a grid 20x15
 + Bombs are placed 20% of the cells are bombs
 + Can place flags on cells
 + RIGHT CLICK on a closed cell to place a flag
 + RIGHT CLICK on a flagged cell to remove the flag
 - Displays somewhere number of bombs - number of flags placed
 + Can open cells
 + LEFT CLICK on a cell to open it
 + Open Cells display Number of nearby bombs
 + If you open a 0, it opens all nearby cells
 + Can't open a flagged cell
 + If you open a bomb, you LOSE
 + If every space that's not a bomb is opened, you WIN
 + Can "sweep" by clicking on a "completed" open cell
 + LEFT CLICK on a completed cell to sweep it
 + "complete" means that there is the same # of flags next to the
 + cell as the number of bombs next to the cell
 + "sweep" means to open all nearby closed cells
 + Display Timer for # of seconds played in game @TODO make pausing work
 + Be able to RESET the game back to starting conditions
 - High Score
 + If you LOSE, display all the positions of remaining Bombs
 - and INVALID FLAGS
 Optional:
 smiley face
 Cannot click a bomb on the 1st click
 Selectable grid sizes
 High Score stays around between games
 clicking a non-completed open cell highlites the adjacent closed
 cells
 */

import processing.core.*;

public class Main extends PApplet {
    public static void main(String[] args) {
        PApplet.main("Main");
    }

    Board b;
    BoardDisplay bd;
    Player p;
    Game test;

    public void setup(){
        textAlign(CENTER, CENTER);
    }
    public void settings() {
        size(1000, 690);
        b = new Board(16, 30, 99);
        bd = new BoardDisplay(b, this);
        //p = new User(b, bd, this);
        p = new ArtInt(b);
        test = new Game(b, p, this);
    }

    public void draw() {
        //test.doMove();
        test.play();
    }

    public void mouseReleased() {
        test.doMove();
    }
    public void keyReleased() {
        test.doMove();
    }

    public void mouseClicked() {
        test.pause();
    }
}
