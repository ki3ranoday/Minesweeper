import processing.core.PApplet;
import processing.core.PFont;

/**
 * Created by Kieran on 1/31/17.
 * <p>
 * This will keep track of the state of the game that is being played
 * It will also use Player input to interact with the board
 */
public class Game {

    /**
     * Board that the game is played on
     */
    private Board board;

    /**
     * a PApplet to use processing functions (mostly just to pass into boardDisplay)
     */
    private PApplet p;
    /**
     * A display for the board
     */
    private BoardDisplay display;

    /**
     * Player that is playing the game
     */
    private Player player;

    /**
     * state of the game (playing, lost, won)
     */
    private int state;

    /**
     * thepause button for the game
     */
    private Button pauseBtn;

    private final static int WON = 0;     //you won the game
    private final static int LOST = 1;    //you lost the game
    private final static int PLAYING = 2; //playing the game
    private final static int WAITING = 3; // waiting for the first click
    private final static int SETUP = 4; //Setting up the difficulty and stuff
    private final static int PAUSED = 5; // when the game is paused

    private int timeStarted;
    private int timePlayed;
    private int timePaused;

    private boolean timeToMove;
    private boolean difficultyChosen = false; //this is only for setting up the game, there is a special screen just to set up

    /**
     * Makes a game with a board and a player, the default state is playing
     *
     * @param b  the board
     * @param pa the player
     */
    public Game(Board b, Player pl, PApplet pa) {
        board = b;
        p = pa;
        display = new BoardDisplay(board, p);
        player = pl;
        state = SETUP;
        timeToMove = false;
        pauseBtn = new Button("", p.width / 2 - 15, 15, 30, p);
    }

    /**
     * Plays the Game
     */
    public void play() {
        if (state == SETUP) {
            setStuff();
        } else {
            p.background(0);
            pauseBtn.draw();
            if (state != PAUSED)
                display.draw();
            p.fill(255, 0, 0);
            p.textSize(30);
            p.text(timePlayed, p.width - 250, 30);
            if(state!= WAITING)
                p.text(board.numBombs() - board.numFlags(), 250, 30);
            if (state == WAITING) {
                if (player instanceof ArtInt)
                    timeToMove = true;
                if (timeToMove) {
                    timeStarted = p.millis() / 1000;
                    state = PLAYING;
                    timeToMove = false;
                    board.openCell(player.makeMove().row(), player.makeMove().column());
                }
            } else if (state == PLAYING) {
                if (player instanceof ArtInt)
                    timeToMove = true;
                timePlayed = p.millis() / 1000 - timeStarted - timePaused;
                if (timeToMove) {
                    interactWithCell(player.makeMove());
                    timeToMove = false;
                }
                if (board.numOpenCells() == board.numColumns() * board.numRows() - board.numBombs()) {
                    state = WON;
                }
                if (board.bombOpened())
                    state = LOST;
            } else if (state == LOST) {
                //board.unFlagAll();
                board.openAllBombs();
                p.textSize(50);
                p.fill(150, 0, 0);
                p.text("YOU LOSE", p.width / 2, p.height / 2);
                if (timeToMove) {
                    state = SETUP;
                    difficultyChosen = false;
                    timeToMove = false;
                }
            } else if (state == WON) {
                if (timeToMove) {
                    state = SETUP;
                    difficultyChosen = false;
                    timeToMove = false;
                }
                p.textSize(50);
                p.fill(150, 0, 0);
                p.text("YOU WON", p.width / 2, p.height / 2);
            } else if (state == PAUSED) {
                timePaused = p.millis() / 1000 - timePlayed - timeStarted;
            }
        }
    }


    /**
     * Makes a new Board to play a new game on
     */
    public void restart() {
        board = new Board(board.numRows(), board.numColumns(), board.numBombs());
        display = new BoardDisplay(board, p);
        player = new ArtInt(board);
        state = SETUP;
        timeToMove = false;
    }

    /**
     * takes in a move and updates the board accordingly (opens, flags, unflags, or sweeps the cell)
     *
     * @param m the move to make, includes a row, column and action
     */
    public void interactWithCell(Move m) {
        int row = m.row();
        int column = m.column();
        int action = m.action();
        if (action == Move.OPEN) {
            board.openCell(row, column);
        } else if (action == Move.FLAG) {
            board.flagCell(row, column);
        } else if (action == Move.UNFLAG) {
            board.unFlagCell(row, column);
        } else if (action == Move.SWEEP) {
            board.sweepCell(row, column);
        }
    }

    /**
     * tells the board to make its move
     */
    public void doMove() {
        timeToMove = true;
    }


    /**
     * makes a screen that asks the user what they want to do so a new game can be created
     */
    private void setStuff() {
        timePlayed = 0;
        timePaused = 0;
        p.background(0);
        PFont font = p.createFont("PressStart2P.ttf", 20);
        p.fill(150);
        p.textFont(font);
        Button easy, medium, hard, input;
        easy = new Button("EASY", p.width / 4 - 40, p.height / 2 - 40, 80, p);
        medium = new Button("MEDIUM", p.width / 2 - 40, p.height / 2 - 40, 80, p);
        hard = new Button("HARD", 3 * p.width / 4 - 40, p.height / 2 - 40, 80, p);
        //input = new Button("MAKE MY OWN", p.width / 2 - 50, 3 * p.height / 4 - 50, 100, p);
        if (!difficultyChosen) {
            p.text("SELECT DIFFICULTY", p.width / 2, 100);
            easy.draw();
            medium.draw();
            hard.draw();
            //input.draw();
            if (timeToMove) {
                if (easy.pointIn(p.mouseX, p.mouseY)) {
                    board = new Board(Board.EASY);
                    display = new BoardDisplay(board, p);
                    difficultyChosen = true;
                }
                if (medium.pointIn(p.mouseX, p.mouseY)) {
                    board = new Board(Board.MEDIUM);
                    display = new BoardDisplay(board, p);
                    difficultyChosen = true;
                }
                if (hard.pointIn(p.mouseX, p.mouseY)) {
                    board = new Board(Board.HARD);
                    display = new BoardDisplay(board, p);
                    difficultyChosen = true;
                }
                timeToMove = false;
            }
        } else {
            p.text("DO YOU WANT TO PLAY OR NAH?", p.width / 2, 100);
            Button ai, self;
            ai = new Button("NAH", p.width / 3 - 40, p.height / 2 - 40, 80, p);
            self = new Button("YAH", 2 * p.width / 3 - 40, p.height / 2 - 40, 80, p);
            ai.draw();
            self.draw();
            if (timeToMove) {
                if (ai.pointIn(p.mouseX, p.mouseY)) {
                    player = new ArtInt(board);
                    timeToMove = false;
                    state = WAITING;
                }
                if (self.pointIn(p.mouseX, p.mouseY)) {
                    player = new User(board, display, p);
                    timeToMove = false;
                    state = WAITING;
                }
            }
        }
    }

    public void pause() {
        if (pauseBtn.pointIn(p.mouseX, p.mouseY)) {
            if (state == PAUSED)
                state = PLAYING;
            else {
                state = PAUSED;
            }
        }
    }
}