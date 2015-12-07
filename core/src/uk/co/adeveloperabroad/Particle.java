package uk.co.adeveloperabroad;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Particle extends Rectangle {


    public Integer id;
    public boolean collided = false;
    private Color colour = Color.GREEN;

    public Integer velocityX = 0;
    public Integer velocityY = 0;

    public Particle(Rectangle rectangle, int id) {
        super(rectangle.getX(),rectangle.getY(),rectangle.getWidth(), rectangle.getHeight());
        this.id = id;
        setSpeed();
    }

    public void changeDirection() {
        setSpeed();
    }

    private void setSpeed() {
        velocityX =  MathUtils.random(-50, 50);
        velocityY =  MathUtils.random(-50, 50);
    }

    public Color getColour() {
        colour = Color.GREEN;
        if (collided) {
            colour = Color.FIREBRICK;
        }
        return colour;
    }

}
