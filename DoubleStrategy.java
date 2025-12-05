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
 * A collision strategy that applies two (or possibly three) other collision strategies
 * upon a collision event. Strategies are chosen randomly and can include another DoubleStrategy.
 * Ensures that no more than one DoubleStrategy is nested and the total number of strategies
 * remains within allowed limits.
 */
public class DoubleStrategy implements CollisionStrategy{
    private static final int MAX_STRATEGIES = 3;
    private static final int TURNS = 2;
    private static final int NUM_AVAILABLE_STRATEGIES = 5;
    private static final String PUCK_IMAGE = "assets/mockBall.png";
    private static final String HEART_IMAGE_PATH = "assets/heart.png";
    private static final int EXTRA_BALLS_INDEX = 0;
    private static final int EXTRA_PADDLE_INDEX = 1;
    private static final int TURBO_INDEX = 2;
    private static final int DOUBLE_STRATEGY_INDEX = 3;
    private static final int EXTRA_LIFE_INDEX = 4;


    /**
     * private members
     * realCollisions- an array of the collisions that the brick will receive
     * size - what is the current size of realCollisions
     * hasDouble - indicates if a double strategy was picked first(of the the two that are to be picked)
     * if true, then the addition will try finding a new random number
     * from here on members that are needed for the other strategies
     * game - the manager of the game. will be used in most strategies
     * counter - for the pucks, in use for extraBalls
     * sound - of the ball, also in use for extraBalls
     * other - this is the paddle, in use for extraPaddle
     * reader - so we can read images for ExtraPaddle
     */
    private final CollisionStrategy[] realCollisions;
    private int size;
    private final BrickerGameManager gameManager;
    private final Counter brickCounter;
    private final Sound sound;
    private final GameObject paddle;
    private final Random rand;
    private final ImageReader reader;
    private boolean hasDouble;
    private final Ball mainBall;
    private final Renderable heartImage;

    /**
     * Constructs a DoubleStrategy that randomly selects and combines other strategies.
     *
     * @param gameManager  Reference to the game manager.
     * @param brickCounter Counter for the number of remaining bricks.
     * @param puckSound        Sound to use with some strategies.
     * @param paddle       The player's paddle.
     * @param imageReader  Used for loading strategy-related images.
     * @param mainBall     Reference to the main game ball.
     */
    public DoubleStrategy(BrickerGameManager gameManager,
                          Counter brickCounter, Sound puckSound,
                          GameObject paddle, ImageReader imageReader, Ball mainBall){
        realCollisions = new CollisionStrategy[MAX_STRATEGIES];
        size = 0;
        this.gameManager = gameManager;
        this.brickCounter = brickCounter;
        this.sound = puckSound;
        this.paddle = paddle;
        this.reader = imageReader;
        this.mainBall = mainBall;
        this.heartImage = imageReader.readImage(HEART_IMAGE_PATH, true);
        hasDouble = false;
        rand = new Random();
        pickRandomStrategies();
    }


    /**
     * Assigns random collision strategies to this strategy.
     * Ensures no more than one nested DoubleStrategy is included.
     */
    public void pickRandomStrategies(){
        int turn = 1;
        while(turn <= TURNS && size <= MAX_STRATEGIES - 1){
            int num = rand.nextInt(NUM_AVAILABLE_STRATEGIES);
            switch (num) {
                case EXTRA_BALLS_INDEX:
                    Renderable image = reader.readImage(PUCK_IMAGE, true);
                    realCollisions[size] = new ExtraBallsStrategy(
                            image, sound, gameManager, brickCounter);
                    size++;
                    break;
                case EXTRA_PADDLE_INDEX:
                    realCollisions[size] = new ExtraPaddleStrategy(gameManager, paddle, brickCounter);
                    size++;
                    break;
                case TURBO_INDEX:
                    realCollisions[size] = new TurboStrategy(reader, gameManager, brickCounter, mainBall);
                    size++;
                    break;
                case DOUBLE_STRATEGY_INDEX:
                    if(hasDouble){continue;}
                    DoubleStrategy doubleStrategy = new DoubleStrategy(gameManager, brickCounter,
                            sound, paddle, reader, mainBall);
                    addFromOtherDouble(doubleStrategy);
                    hasDouble = true;
                    break;
                case EXTRA_LIFE_INDEX:
                    realCollisions[size] = new ExtraLifeStrategy(heartImage, gameManager, brickCounter);
                    size++;
                    break;
            }
            turn++;
        }
    }

    /**
     * Incorporates the strategies of a nested DoubleStrategy.
     * @param strategy The nested DoubleStrategy to include.
     */
    public void addFromOtherDouble(DoubleStrategy strategy){
        CollisionStrategy[] other = strategy.getRealCollisions();
        for(int i = 0; i < strategy.getSize(); i++){
            if(size >= MAX_STRATEGIES){break;}
            addToExisting(other[i]);
        }
    }

     /**
     * Applies all strategies assigned to this strategy.
     * @param thisObj The brick GameObject.
     * @param otherObj The object that collided with the brick.
     */
    public void implementStrategies(GameObject thisObj, GameObject otherObj){
        for(int i = 0; i < size; i++){
            realCollisions[i].onCollision(thisObj, otherObj);
        }
    }

    /**
     * Adds a new strategy to the internal list.
     * @param collisionStrategy The strategy to add.
     */
    public void addToExisting(CollisionStrategy collisionStrategy){
        realCollisions[size] = collisionStrategy;
        size++;
    }

    /**
     * getter for size of realCollisions
     * @return size
     */
    public int getSize(){
        return size;
    }


    /**
     * @return Array of assigned strategies.
     */
    public CollisionStrategy[] getRealCollisions(){
        return realCollisions;
    }


    /**
     * Applies all strategies assigned to this strategy.
     * @param thisObj The brick GameObject.
     * @param otherObj The object that collided with the brick.
     */
    @Override
    public void onCollision(GameObject thisObj, GameObject otherObj) {
        implementStrategies(thisObj, otherObj);
    }
}
