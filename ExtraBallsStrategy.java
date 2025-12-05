package bricker.brick_strategies;


import bricker.gameobjects.Puck;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.Random;

/**
 * A collision strategy that spawns two additional balls ("pucks") when the brick is hit.
 * These balls will move upward in randomized directions and behave like regular balls.
 * The original brick will be removed upon collision, and the brick counter will be updated.
 *
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 */
public class ExtraBallsStrategy implements CollisionStrategy {
    /** Number of puck objects to generate in the game.*/
    private static final int NUM_PUCKS = 2;

    /**Speed of each puck in units per second.*/
    public static final float PUCK_SPEED = 200f;

    /**Dimensions of a puck (width, height) – 75% of the ball size.*/
    private static final Vector2 PUCK_SIZE = new Vector2(37.5f, 37.5f); // 75% of default ball size



    /** Brick counter to decrement when brick is removed. */
    private final Counter brickCounter;

    /** Image used for the generated pucks. */
    private final Renderable puckImage;

    /** Sound played when puck collides with an object. */
    private final Sound puckSound;

    /** Reference to the game manager, used to add/remove objects. */
    private final BrickerGameManager gameManager;


    /**
     * Constructs an ExtraBallsStrategy.
     * @param puckImage Image for the spawned pucks.
     * @param puckSound Sound to play on puck collision.
     * @param gameManager Reference to the game manager.
     * @param brickCounter Counter to track the number of remaining bricks.
     */
    public ExtraBallsStrategy(
            Renderable puckImage,
            Sound puckSound,
            BrickerGameManager gameManager,
            Counter brickCounter) {
        this.puckImage = puckImage;
        this.puckSound = puckSound;
        this.gameManager = gameManager;
        this.brickCounter = brickCounter;
    }


    /**
     * Handles collision with a brick:
     * - Removes the brick from the game.
     * - Decrements the brick counter.
     * - Spawns two pucks that move upward in random directions.
     *
     * @param thisObj The brick that was hit.
     * @param otherObj The object that collided with the brick (e.g., ball).
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        if (gameManager.removeGameObjectFromGame(thisObj)) {
            brickCounter.decrement();
        }
        Vector2 brickCenter = thisObj.getCenter();


        for (int i = 0; i < NUM_PUCKS; i++) {
            Puck puck = new Puck(
                    brickCenter,
                    PUCK_SIZE,
                    puckImage,
                    puckSound,
                    gameManager
            );
            puck.setVelocity(randomUpperUnitVector());
            gameManager.addGameObjectFromGame(puck);
        }
    }

    /**
     * Generates a random upward-pointing unit vector scaled by PUCK_SPEED.
     * The direction is limited to the top half of the unit circle.
     *
     * @return A velocity vector pointing upward.
     */
    private Vector2 randomUpperUnitVector() {
        Random random = new Random();
        double angle = random.nextDouble() * Math.PI;
        float velocityX = (float) Math.cos(angle) * PUCK_SPEED;
        float velocityY = (float) Math.sin(angle) * PUCK_SPEED;
        return new Vector2(velocityX, velocityY);
    }
}