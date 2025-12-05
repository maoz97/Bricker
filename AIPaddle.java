package bricker.gameobjects;

import bricker.brick_strategies.ExtraPaddleStrategy;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * this is the paddle that is created for the ExtraPaddle collision strategy
 * @author ishay shaul
 * @author maoz bar-shimon
 * @see ExtraPaddleStrategy
 */
public class AIPaddle extends GameObject {
    private static final int MOVEMENT_SPEED = 300;
    private static final int MAX_HITS = 4;


    /**
     * private members
     * toFollow - the user paddle, that the AIPaddle will be imitating
     * hits - the number of times the AIPaddle has been hit
     * game - reference to the BrickGameManager that is handling the game
     */
    private final GameObject toFollow;
    private int hits;
    private final BrickerGameManager gameManager;

    /**
     * constructs an AIPaddle object
     * @param topLeftCorner of the AIPaddle that is being created
     * @param dimensions of the AIPaddle
     * @param renderable the image of the AIPaddle
     * @param objectToFollow the user paddle
     * @param gameManager manager of the game that is being played
     */
    public AIPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                    GameObject objectToFollow, BrickerGameManager gameManager){
        super(topLeftCorner, dimensions, renderable);
        toFollow = objectToFollow;
        hits = 0;
        this.gameManager = gameManager;
    }

    /**
     * updates the AIPaddles position to match the user paddle.
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
        if(toFollow.getCenter().x() < getCenter().x()){
            movementDir = Vector2.LEFT;
        }
        if(toFollow.getCenter().x() > getCenter().x()){
            movementDir = Vector2.RIGHT;
        }
        setVelocity(movementDir.mult(MOVEMENT_SPEED));
    }

    /**
     * As requested in the assignment, the AIPaddle will be removed from the game if
     * it is hit more than six times
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
         hits++;
         if(hits > MAX_HITS){
             gameManager.removeGameObjectFromGame(this);
             hits = 0;
            }
    }
}
