package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.util.Counter;

/**
 * BasicCollisionStrategy will remove the brick that was hit from the game.
 * will serve as default strategy
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see StrategyFactory
 * @see DoubleStrategy
 */
public class BasicCollisionStrategy implements CollisionStrategy {
    /**
     * the game manager, allowing to access to the game logic.
     */
    BrickerGameManager brickerGameManager;

    /**
     * counter of the bricks in the game, that will be updated after the brick is deleted.
     */
    private final Counter brickCounter;

    /**
     * constructs a BasicCollisionStrategy
     *
     * @param brickerGameManager the game manager, so we can access the logic
     * @param brickCounter       counter of the bricks. Will be updated after each call
     */
    public BasicCollisionStrategy(BrickerGameManager brickerGameManager, Counter brickCounter) {
        this.brickerGameManager = brickerGameManager;
        this.brickCounter = brickCounter;
    }

    /**
     * handles the collision by removing the brick and decrementing the counter
     *
     * @param thisObj  that will be removed from the game(will be a brick)
     * @param otherObj object that collides with thisObj( will be a ball or puck)
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        if (brickerGameManager.removeGameObjectFromGame(thisObj)) {
            brickCounter.decrement();
        }
    }
}
