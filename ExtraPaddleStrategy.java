package bricker.brick_strategies;

import danogl.GameObject;
import bricker.gameobjects.AIPaddle;
import danogl.util.Counter;
import danogl.util.Vector2;
import bricker.main.BrickerGameManager;

/**
 * A collision strategy that spawns an AI-controlled paddle (AIPaddle)
 * in the middle of the screen upon brick destruction.
 * If the AI paddle already exists and is hit MAX_HITS times, it is removed.
 *
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.gameobjects.AIPaddle
 */
public class ExtraPaddleStrategy implements CollisionStrategy{
    private static final int MAX_HITS = 4;

    /** Divider used to position paddle in the center. */
    private static final int TWO = 2;

    /** Factor to scale the paddle size down. */
    private static final float THREE_QUARTERS = 0.75f;

    /** Hit counter for the AI paddle. */
    private static int hits = 0;

    private static boolean existsPaddle = false;
    private static AIPaddle paddle;
    private final GameObject userPaddle;
    private final BrickerGameManager gameManager;
    private final Counter brickCounter;


    /**
     * Constructs an ExtraPaddle collision strategy.
     *
     * @param gameManager   The game manager.
     * @param userPaddle    The original user-controlled paddle.
     * @param brickCounter  The brick counter to decrement when brick is removed.
     */
    public ExtraPaddleStrategy(BrickerGameManager gameManager, GameObject userPaddle,
                               Counter brickCounter){
        existsPaddle = false;
        this.gameManager = gameManager;
        this.userPaddle = userPaddle;
        this.brickCounter = brickCounter;
        Vector2 newDimensions = new Vector2(userPaddle.getDimensions().x() * THREE_QUARTERS,
                userPaddle.getDimensions().y() * THREE_QUARTERS);
        Vector2 topLeftCorner = new Vector2(this.gameManager.getWindowDimensions().x()/TWO,
                this.gameManager.getWindowDimensions().y()/TWO);
        paddle = new AIPaddle(topLeftCorner, newDimensions, userPaddle.renderer().getRenderable(),
                this.userPaddle, this.gameManager);
    }

    /**
     * Called upon collision between a ball and a brick using this strategy.
     * Adds or removes the AI paddle, and removes the brick.
     *
     * @param thisObj  The brick that was hit.
     * @param otherObj The object that hit the brick.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        if (gameManager.removeGameObjectFromGame(thisObj)) {
            brickCounter.decrement();
        }
        if(existsPaddle){
            hits++;
            if(hits >= MAX_HITS){
                gameManager.removeGameObjectFromGame(paddle);
                existsPaddle = false;
                hits = 0;
            }
            if (gameManager.removeGameObjectFromGame(thisObj)) {
                brickCounter.decrement();
            }
        }
        else{
            gameManager.addGameObjectFromGame(paddle);
            existsPaddle = true;
            if (gameManager.removeGameObjectFromGame(thisObj)) {
                brickCounter.decrement();
            }
        }
    }
}


