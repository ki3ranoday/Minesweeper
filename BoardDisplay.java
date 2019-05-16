import processing.core.PApplet;
import processing.core.PFont;

/**
 * Created by Kieran on 2/2/17.
 * <p>
 * Draws the board on the screen
 */
public class BoardDisplay {
    /**
     * The Processing applet from Main, so this can draw on the screen
     */
    private PApplet p;

    /**
     * The board to display
     */
    private Board b;

    /**
     * size of each cell
     */
    private int size;

    /**
     * the top right corner's position on the screen
     */
    private float x, y;

    /**
     * makes a display for a board at a size and location
     */
    public BoardDisplay(Board b, PApplet p, float x, float y) {
        this.b = b;
        this.p = p;
        this.x = x;
        this.y = y;
        size = (p.width - 50)/ b.numColumns();

        if ((size * b.numRows()) > p.height - 50)
            size = (p.height - 50) / b.numRows();
    }
    /**
     * makes a display for a board at a size and centered so there is room for stuff in the borders
     */
    public BoardDisplay(Board b, PApplet p) {
        this.b = b;
        this.p = p;
        int width = p.width - 100;
        int height = p.height - 100;
        size = width / b.numColumns();
        x = 50;
        y = 50 + (height - (b.numRows() * size)) / 2;
        if ((size * b.numRows()) > height) {
            size = height / b.numRows();
            y = 50;
            x = 50 + (width - (b.numColumns() * size)) / 2;
        }
    }
    /**
     * draws the board starting at x and y
     */
    public void draw() {
        p.stroke(175);
        for (int r = 0; r < b.numRows(); r++) {
            for (int c = 0; c < b.numColumns(); c++) {
                p.fill(100);
                p.rect(x + c * size, y + r * size, size, size);
                if (b.cellIsNull(r, c))
                    continue;
                if (b.cellIsFlagged(r, c)) {
                    p.fill(252, 0, 0);
                    p.rect(x + c * size, y + r * size, size, size);
                } else if (b.cellIsOpen(r, c)) {
                    if (b.cellHasBomb(r, c)) {
                        p.fill(0);
                        p.rect(x + c * size, y + r * size, size, size);
                    } else {
                        p.fill(150);
                        p.rect(x + c * size, y + r * size, size, size);
                        PFont font = p.createFont("PressStart2P.ttf", 15);
                        p.textFont(font);
                        int num = b.nearbyBombs(r, c);
                        if (num == 1)
                            p.fill(0, 0, 230);
                        else if (num == 2)
                            p.fill(0, 230, 0);
                        else if (num == 3)
                            p.fill(230, 0, 0);
                        else if (num == 4)
                            p.fill(0, 0, 150);
                        else if (num == 5)
                            p.fill(150, 0, 0);
                        else if (num == 6)
                            p.fill(0, 150, 150);
                        else if (num == 7)
                            p.fill(0);
                        else if (num == 8)
                            p.fill(180);
                        if (num != 0)
                            p.text(num, (float) (x + 0.5 * size + c * size), (float) (y + 0.5 * size + r * size));
                    }
                }
            }
        }
    }

    /**
     * given a y location on the screen, calculates what row is at that position in the display
     *
     * @param uY the user y value that is being used (probably mouseY)
     * @return what row is there
     */
    public int rowAt(float uY) {
        for (int r = 0; r < b.numRows(); r++) {
            if (uY > y + r * size && uY < y + (r + 1) * size) {
                return r;
            }
        }
        return -1;
    }

    /**
     * given a x location on the screen, calculates what column is at that position in the display
     *
     * @param uX the user x value that is being used (probably mouseX)
     * @return what column is there
     */
    public int columnAt(float uX) {
        for (int c = 0; c < b.numColumns(); c++) {
            if (uX > x + c * size && uX < x + (c + 1) * size) {
                return c;
            }
        }
        return -1;
    }
}
