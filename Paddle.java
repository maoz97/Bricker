package bricker.gameobjects;

import danogl.GameObject;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * Represents the user paddle.
 * positioned at the bottom level and is restricted to horizontal movement only
 * move left or right
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.main.BrickerGameManager
 */
public class Paddle extends GameObject {
    private static final int MOVEMENT_SPEED = 300;
    private static final int WALL_WIDTH = 15;

    /** inputListener will allow us to accept the keyboard that the user pressed.*/
    private UserInputListener inputListener;

    /** width of the board, so that the paddle will not be out of bounds.*/
    private final float width;


    /**
     * constructs a user paddle
     * @param topLeftCorner the spot on the board where the paddle will be placed
     * @param dimensions of the user paddle
     * @param paddleImage the image that will represent the paddle in the window
     * @param inputListener that will allow us to follow what keys are pressed
     * @param width of the window of the game
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable paddleImage,
                  UserInputListener inputListener, float width){
        super(topLeftCorner, dimensions, paddleImage);
        this.inputListener = inputListener;
        this.width = width;
    }

    /**
     *  Updates the paddle's position based on user input.
     * The paddle responds to left and right arrow key presses and is constrained
     * to remain within the horizontal bounds of the game window.
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDir = Vector2.ZERO;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT) && getTopLeftCorner().x() > WALL_WIDTH){
            movementDir = movementDir.add(Vector2.LEFT);
        }
        else if(inputListener.isKeyPressed(KeyEvent.VK_RIGHT) &&
                getTopLeftCorner().x() + getDimensions().x() < width - WALL_WIDTH){
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        setVelocity(movementDir.mult(MOVEMENT_SPEED));
    }
}
