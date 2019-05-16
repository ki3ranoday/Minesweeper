import processing.core.PApplet;
import processing.core.PFont;

/**
 * Created by Kieran on 2/15/17.
 *
 * This class just draws a square button and has a method to see if a point is inside the button
 */
public class Button {
    private String text;
    private float x, y, size;
    PApplet p;

    public Button (String text, float x, float y, float size, PApplet p){
        this.size = size;
        this.x = x;
        this.y = y;
        this.text = text;
        this.p = p;
    }

    public void draw(){
        p.fill(150);
        p.rect(x, y, size, size);
        p.fill(0);
        PFont font = p.createFont("PressStart2P.ttf", 7);
        p.textFont(font);
        p.text(text, x, y, size, size);
    }

    public boolean pointIn(float x, float y){
        return (x > this.x && x < this.x + size && y > this.y && y < this.y + size);
    }
}
