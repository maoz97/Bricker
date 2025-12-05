package bricker.gameobjects;

import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Displays the player's remaining lives as a number instead of graphical hearts.
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.main.BrickerGameManager
 */
public class NumericLife extends GameObject {
    private static final float NUMERIC_WIDTH = 30;
    private static final float NUMERIC_HEIGHT = 40;
    private static final int TWO_LIVES_LEFT = 2;
    private static final int THREE_LIVES_LEFT = 3;

    /** renderable used to display the number of lives remaining.*/
    private TextRenderable textRenderable;

    /** The collection of game objects, where the numeric will be added.*/
    private GameObjectCollection gameObjects;

    /**
     * constructs a numeric life counter. will be added just below the Graphic LIfe
     * @param position where the numeric will be set
     * @param gameObjects collection of game objects that will append the numeric
     * @param initialLives the number of remaining lives the user starts the game with
     */
    public NumericLife(Vector2 position, GameObjectCollection gameObjects, int initialLives) {
        super(position, new Vector2(NUMERIC_WIDTH,NUMERIC_HEIGHT), null);
        this.textRenderable = new TextRenderable(Integer.toString(initialLives));
        this.textRenderable.setColor(Color.GREEN);
        this.renderer().setRenderable(textRenderable);
        this.gameObjects = gameObjects;
        this.gameObjects.addGameObject(this, danogl.collisions.Layer.UI);
    }

    /**
     * Updates the color of the numeric.
     * Green for 3+, yellow for 2 and red for one
     * @param livesLeft
     */
    public void updateLives(int livesLeft) {
        textRenderable.setString(Integer.toString(livesLeft));

        if (livesLeft >= THREE_LIVES_LEFT)
            textRenderable.setColor(Color.GREEN);
        else if (livesLeft == TWO_LIVES_LEFT)
            textRenderable.setColor(Color.YELLOW);
        else
            textRenderable.setColor(Color.RED);
    }
}
