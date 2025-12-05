package bricker.brick_strategies;

import bricker.gameobjects.Ball;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;

import java.util.Random;

/**
 * A factory for generating random collision strategies for bricks.
 * Depending on a random number, this factory returns either a basic strategy
 * or one of several advanced collision strategies like extra life, turbo mode, etc.
 *
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see CollisionStrategy
 */
public class StrategyFactory{
    private static final String PUCK_IMAGE_PATH = "assets/mockBall.png";
    private static final String HEART_IMAGE_PATH = "assets/heart.png";
    private static final int BASIC_STRATEGY_PROBABILITY = 5;
    private static final int DOUBLE_STRATEGY_INDEX = 5;
    private static final int EXTRA_BALLS_INDEX = 6;
    private static final int EXTRA_PADDLE_INDEX = 7;
    private static final int TURBO_INDEX = 8;
    private static final int EXTRA_LIFE_INDEX = 9;
    private static final int STRATEGY_BOUND = 10;


    private final BrickerGameManager gameManager;
    private final Counter brickCounter;
    private final Sound sound;
    private final GameObject userPaddle;
    private final Ball mainBall;
    private final ImageReader reader;

    private final Renderable puckImage;
    private final Renderable heartImage;


    /**
     * Constructs a StrategyFactory with all dependencies.
     *
     * @param gameManager   the game manager
     * @param brickCounter  the counter for the bricks
     * @param puckSound     the sound for the ball collision
     * @param userPaddle    the main paddle in the game
     * @param imageReader   for loading images
     * @param mainBall      the main ball object
     */
    public StrategyFactory(BrickerGameManager gameManager,
                           Counter brickCounter, Sound puckSound, GameObject userPaddle,
                           ImageReader imageReader,
                           Ball mainBall
                           ){
        this.gameManager = gameManager;
        this.brickCounter = brickCounter;
        this.reader = imageReader;
        this.userPaddle = userPaddle;
        this.sound = puckSound;
        this.mainBall = mainBall;

        this.puckImage = imageReader.readImage(PUCK_IMAGE_PATH, true);
        this.heartImage = imageReader.readImage(HEART_IMAGE_PATH, true);
    }

    /**
     * Generates a random collision strategy for a brick.
     * This method has a higher chance of returning a basic strategy,
     * and occasionally returns a decorated/advanced strategy.
     *
     * @return a CollisionStrategy instance
     */
    public CollisionStrategy randomStrategy(){
       Random rand = new Random();
       int num = rand.nextInt(STRATEGY_BOUND);
        BasicCollisionStrategy baseStrategy = new BasicCollisionStrategy(gameManager, brickCounter);
       if(num < BASIC_STRATEGY_PROBABILITY){
           return baseStrategy;
       }
       switch (num){
           case DOUBLE_STRATEGY_INDEX :
               return new DoubleStrategy(gameManager, brickCounter, sound, userPaddle, reader, mainBall);
           case EXTRA_BALLS_INDEX :
               return new ExtraBallsStrategy(
                       puckImage, sound, gameManager, brickCounter);
           case EXTRA_PADDLE_INDEX :
               return new ExtraPaddleStrategy(gameManager, userPaddle, brickCounter);
           case TURBO_INDEX :
                return new TurboStrategy(reader, gameManager, brickCounter, mainBall);
           case EXTRA_LIFE_INDEX :
               return new ExtraLifeStrategy(heartImage, gameManager, brickCounter);
        }
       return baseStrategy;
    }
}
