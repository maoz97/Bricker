
package bricker.brick_strategies;

import bricker.gameobjects.Heart;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;


/**
 * A collision strategy that, when triggered, spawns a falling heart collectible from the center
 * of the brick. If collected by the main paddle, the player gains one extra life.
 * The strategy also removes the brick from the game and decrements the brick counter.
 *
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.gameobjects.Heart
 * @see bricker.main.BrickerGameManager
 */
public class ExtraLifeStrategy implements CollisionStrategy {

    /** The renderable image used for the heart. */
    private final Renderable heartImage;

    /** Reference to the main game manager. */
    private final BrickerGameManager gameManager;

    /** Counter tracking the number of remaining bricks. */
    private final Counter brickCounter;

    /**
     * Constructs the ExtraLife strategy.
     *
     * @param heartImage    The image to use for the falling heart.
     * @param gameManager   Reference to the BrickerGameManager.
     * @param brickCounter  Counter for remaining bricks in the game.
     */
    public ExtraLifeStrategy(Renderable heartImage, BrickerGameManager gameManager,
                             Counter brickCounter) {
        this.heartImage = heartImage;
        this.gameManager = gameManager;
        this.brickCounter = brickCounter;
    }


    /**
     * Called when a collision occurs with a brick using this strategy.
     * Removes the brick, decreases the brick counter, and spawns a falling heart.
     *
     * @param thisObj  The brick that was hit.
     * @param otherObj The object that hit the brick (e.g., a ball).
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        if (gameManager.removeGameObjectFromGame(thisObj)) {
            brickCounter.decrement();
        }
        Vector2 heartPosition = thisObj.getCenter();
        Heart fallingHeart = new Heart(heartPosition, heartImage, true, gameManager);
        gameManager.addGameObjectFromGame(fallingHeart);
    }
}
