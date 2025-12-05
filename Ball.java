package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a ball in the game. The ball will move, and make a sound each time it bounces off
 * an object
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.main.BrickerGameManager
 */

public class Ball extends GameObject {
    /** The number of times a turbo ball can hit another object before turning back to normal.*/
    private static final int MAX_HITS = 6;
    /** The speed by which the ball is multiplied or divided by when it changes modes.*/
    private static final float TURBO_SPEED = 1.4f;

    /** The sound that is played when the ball collides with another game object.*/
    private Sound collisionSound;

    /** keeps track of how many bricks remain in the game.*/
    private int collisionCounter;

    /** Represents if the ball is currently in turbo mode.*/
    private boolean isTurbo;

    /** tracks the number of times the turbo ball has collided with others.*/
    private int hitsWhenFast;

    /** Keeping the original image, so the return to the normal mode is efficient.*/
    private Renderable originalImage;

    /**
     * constructs the ball object for the game
     * @param topLeftCorner where the ball is placed
     * @param dimensions    size of the ball
     * @param renderable    image of the ball
     */
    public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                Sound collisionSound) {
        super(topLeftCorner, dimensions, renderable);
        this.collisionSound = collisionSound;
        collisionCounter = 0;
        isTurbo = false;
        hitsWhenFast = 0;
        originalImage = renderable;
    }

    /**
     * getter for the collisions counter
     * @return the number of collisions
     */
    public int getCollisionCounter(){
        return collisionCounter;
    }

    /**
     * when the ball hits another GameObject, it "bounces" of the object
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        Vector2 newVelocity = getVelocity().flipped(collision.getNormal());
        setVelocity(newVelocity);
        collisionSound.play();
        collisionCounter++;
        if(isTurbo){
            hitsWhenFast++;
            if(hitsWhenFast > MAX_HITS){
                renderer().setRenderable(originalImage);
                setVelocity(getVelocity().mult(1/ TURBO_SPEED));
                isTurbo = false;
                hitsWhenFast = 0;
            }
        }
    }

    /**
     * changeCharacteristics will update the velocity and image of the ball.
     * This method will be used in the TurboStrategy to turn the ball into Turbo mode
     * @param newSpeed The updated velocity of the ball
     * @param newImage The new image that will represent the ball in the window
     */
    public void changeCharacteristics(Vector2 newSpeed, Renderable newImage){
        setVelocity(newSpeed);
        renderer().setRenderable(newImage);
    }


    /**
     * Setting if the ball will go into turbo mode
     * @param update The new boolean. true will indicate that the ball will go into turbo mode.
     */
    public void setIsTurbo(boolean update){isTurbo = update;}

    /**
     * Getter if the ball is in turbo mode
     * @return true if the ball is in turbo mode, false otherwise
     */
    public boolean getIsTurbo(){return isTurbo;}



}
