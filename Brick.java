package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a brick in the game. Depending on the strategy that is chosen for the brick,
 * when the brick is hit it will perform its strategy(like creating an AIPaddle, or release more
 * balls or other strategies)
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.main.BrickerGameManager
 */
public class Brick extends GameObject {
    private final CollisionStrategy strategy;

    /**
     * Constructor to build a brick for the game.
     * @param topLeftCorner of the brick that is being created
     * @param dimensions what size the brick will be
     * @param renderable determine the image of the brick
     * @param strategy that the brick will perform when hit
     */
    public Brick(Vector2 topLeftCorner, Vector2 dimensions,
                 Renderable renderable, CollisionStrategy strategy) {
        super(topLeftCorner, dimensions, renderable);
        this.strategy = strategy;
    }

    /**
     * performing the strategy of the brick when hit
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        strategy.onCollision(this, other);
    }
}
