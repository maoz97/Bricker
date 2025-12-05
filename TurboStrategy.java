package bricker.brick_strategies;

import bricker.gameobjects.Ball;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import danogl.gui.ImageReader;

/**
 * When colliding with a brick containing this strategy, the ball will go into "turbo mode".
 * The velocity will multiply by 1.4, and the color will turn to red.
 * After the "turbo ball" hits six game objects, it will turn back to the regular ball
 */
public class TurboStrategy implements CollisionStrategy{
    /** the speed the ball will be multiplied by.*/
    private static final float TURBO_SPEED = 1.4f;

    /** The path to find the image representing the turbo ball.*/
    private static final String RED_BALL_PATH = "assets/redball.png";

    /** imageReader that will allow us to interpret the image that will be shown in the window.*/
    private final ImageReader imageReader;

    /** The manager of the game, will allow access to the game logic.*/
    private final BrickerGameManager gameManager;

    /** tracks the number of bricks in the game.*/
    private final Counter brickCounter;

    /** reference to the main ball.*/
    private final Ball mainBall;


    /**
     * constructs a TurboStrategy. The strategy will be given to the brick during the
     * initialization of the game
     * @param imageReader  the imageReader used to read image from the assets folder
     * @param gameManager  manager of the game, which will help add and remove objects
     * @param brickCounter keeps track of the number of the remaining bricks in the game
     * @param mainBall     the ball that will potentially turn into turbo mode
     */
    public TurboStrategy(ImageReader imageReader, BrickerGameManager gameManager,
                         Counter brickCounter,
                         Ball mainBall){
        this.imageReader = imageReader;
        this.gameManager = gameManager;
        this.brickCounter = brickCounter;
        this.mainBall = mainBall;
    }

    /**
     * Only the main ball can be turned into the turbo, while the pucks will simply remove the
     * brick from the game
     * @param thisObj  The brick being hit.
     * @param otherObj The object that hit the brick (e.g., the ball or puck).
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        //only the main ball can activate the turbo strategy
        if(otherObj != mainBall){
            if (gameManager.removeGameObjectFromGame(thisObj)) {
                brickCounter.decrement();
            }
            return;
        }
        //delete the brick from the game
        if (gameManager.removeGameObjectFromGame(thisObj)) {
            brickCounter.decrement();
        }
        // if the ball is normal, then turn it to turbo
        if(!mainBall.getIsTurbo()){
            Vector2 newSpeed = mainBall.getVelocity().mult(TURBO_SPEED);
            Renderable newImage = imageReader.readImage(RED_BALL_PATH, true);
            mainBall.changeCharacteristics(newSpeed, newImage);
            mainBall.setIsTurbo(true);
        }
    }
}
