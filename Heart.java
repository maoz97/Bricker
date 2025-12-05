package bricker.gameobjects;

import bricker.brick_strategies.ExtraLifeStrategy;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * The class that represents the heart. A heart can either appear statically in the top-left corner
 * representing the number of lives the player has left, or will fall down the screen as a
 * collectible that grants an extra life for the user.
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see GraphicLife
 * @see ExtraLifeStrategy
 */
public class Heart extends GameObject {
    private static final float FALL_SPEED = 100f;


    /** the size of all hearts. */
    private static final Vector2 HEART_SIZE = new Vector2(30, 30);

    /** whether the heart is falling. */
    private final boolean isFalling;

    /** the game manager used to access the game logic. */
    private final BrickerGameManager gameManager;

    /**the user paddle . */
    private GameObject mainPaddle;


    /**
     * constructs a falling heart
     * @param position    represents the position of the heart on the window
     * @param image       that will be shown on th window representing the heart
     * @param isFalling   determining that the heart is of the ExtraLife strategy.
     * @param gameManager of the current game, so we can add the heart to the gameObjects
     */
    public Heart(Vector2 position, Renderable image,
                 boolean isFalling, BrickerGameManager gameManager) {
        super(position, HEART_SIZE, image);
        this.isFalling = isFalling;
        this.gameManager = gameManager;
        if(this.isFalling) {
            this.setVelocity(new Vector2(0, FALL_SPEED));
        }
    }

    /**
     * Constructs a stationary heart
     * to the game
     * @param position of the heart on the window
     * @param image that will represent the heart on the window
     */
    public Heart(Vector2 position, Renderable image) {
        super(position, HEART_SIZE, image);
        this.isFalling = false;
        this.gameManager = null;
    }

    /**
     * Method that is designed specifically for the ExtraLife strategy.
     * The heart will drop until it hits the paddle, adding a life to the player. And if not,
     * will leave the window.
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
        if(!isFalling) return;
        float screenHeight = gameManager.getWindowDimensions().y();
        if (getTopLeftCorner().y() > screenHeight) {
            gameManager.removeGameObjectFromGame(this);
        }
    }

    /**
     * method will be used only int the context of the ExtraLife strategy. determines if the object that
     * the heart hit is the user paddle
     * @param other the object that collided with the heart.
     * @return true if teh other object is the user paddle, false otherwise
     */
    @Override
    public boolean shouldCollideWith(GameObject other){
        return isFalling && other == gameManager.getUserPaddle();
    }

    /**
     * Method will combine both vocations of the heart class.
     * If the heart did collide with the user paddle, then another life will be added
     * to the user
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, danogl.collisions.Collision collision) {
        if (!isFalling) return;
        if (shouldCollideWith(other)) {
            gameManager.addLife();
            gameManager.removeGameObjectFromGame(this);
        }
    }
}
