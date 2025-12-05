package bricker.main;

import bricker.brick_strategies.StrategyFactory;
import bricker.gameobjects.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import java.awt.event.KeyEvent;



import java.awt.*;
import java.util.Random;

/**
 * Manages the entire Bricker game flow, including game initialization,
 * object creation, collision handling, life tracking, and win/lose conditions.
 * This class extends {@link GameManager} and serves as the entry point for launching
 * and running the Bricker game. It initializes all game components, such as the paddle,
 * ball, bricks, walls, background, and life indicators (both graphical and numeric).
 * It also handles user input, game updates, and end-of-game prompts.
 * Game elements are rendered and updated using the danogl framework, and additional
 * behaviors are managed via different {@code CollisionStrategy} implementations.
 *
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.brick_strategies.CollisionStrategy
 * @see bricker.gameobjects.Paddle
 * @see bricker.gameobjects.Ball
 * @see danogl.GameManager
 */
public class BrickerGameManager extends GameManager {

    /** Number of command-line arguments expected */
    private static final int NUM_ARGS = 2;

    /** Size of the user-controlled paddle (width, height) */
    private static final Vector2 USER_PADDLE_SIZE = new Vector2(200, 20);

    /** Dimensions of the ball (width, height) */
    private static final Vector2 BALL_DIMENSIONS = new Vector2(50, 50);

    /** Factor to determine initial vertical position of the ball relative to the screen height */
    private static final float BALL_INITIAL_POSITION_FACTOR = 0.5f;

    /** Used to divide width/height by 2 for centering calculations */
    private static final float CENTER_DIVISOR = 2f;

    /** Height of the border around the screen */
    private static final int BORDER_HEIGHT = 2;

    /** Extra spacing added in layout calculations */
    private static final int ADD_SPACES = 1;

    /** Vertical offset of the paddle from the bottom of the screen */
    private static final float PADDLE_BOTTOM_OFFSET = 30f;

    /** Horizontal offset for placing graphical heart icons */
    private static final float HEART_OFFSET_X = 10f;

    /** Vertical offset for displaying numeric life counter */
    private static final float NUMERIC_LIFE_OFFSET_Y = 90f;

    /** Size (diameter) of the heart icon */
    private static final int HEART_SIZE = 10;

    /** Starting number of lives the player has */
    private static final int INITIAL_LIVES = 3;

    /** Maximum number of lives allowed */
    private static final int MAX_LIVES = 4;

    /** Width of the border around the screen */
    private static final int BORDER_WIDTH = 10;

    /** Space between adjacent bricks */
    private static final int SPACE_BETWEEN_BRICKS = 5;

    /** Color of the screen border */
    private static final Color BORDER_COLOR = Color.BLACK;

    /** Speed of the ball in units per second */
    private static final float BALL_SPD = 200;

    /** Default number of rows of bricks */
    private static final int DEFAULT_ROWS = 7;

    /** Default number of bricks per row */
    private static final int DEFAULT_BRICKS_PER_ROW = 8;

    /** Width of the game board in pixels */
    private static final int BOARD_WIDTH = 700;

    /** Height of the game board in pixels */
    private static final int BOARD_HEIGHT = 500;

    /** Height of a single brick */
    private static final int BRICK_HEIGHT = 15;

    /** Constant used for reversing direction (e.g., ball bounce) */
    private static final int DIRECTION_INVERSION = -1;

    /** Message displayed when the player loses */
    private static final String LOSE = " You Lose!";

    /** Message displayed when the player wins */
    private static final String WIN = " You WIN!";

    /** Empty string constant */
    private static final String EMPTY = "";

    /** Message prompting the player to play again */
    private static final String PLAY_AGAIN = " play again?";

    /** File path to the paddle image asset */
    private static final String PADDLE_ASSET_PATH = "assets/paddle.png";

    /** File path to the brick image asset */
    private static final String BRICK_ASSET_PATH = "assets/brick.png";

    /** File path to the heart icon image asset */
    private static final String HEART_ASSET_PATH = "assets/heart.png";

    /** File path to the background image */
    private static final String BACKGROUND_IMAGE_PATH = "assets/DARK_BG2_small.jpeg";

    /** File path to the ball image asset */
    private static final String BALL_IMAGE_PATH = "assets/ball.png";

    /** File path to the ball collision sound effect */
    private static final String BALL_SOUND_PATH = "assets/blop.wav";

    /** Name of the game */
    public static final String BRICKER = "Bricker";


    /** Number of rows of bricks */
    private final int rows;

    /** Number of bricks per row (columns) */
    private final int bricksPerRow;

    /** Width of the game board */
    private final float width;

    /** Height of the game board */
    private final float height;

    /** The main ball object used in the game */
    private Ball mainBall;

    /** Controller for window operations (e.g., closing, screen size) */
    private WindowController windowController;

    /** Counter tracking remaining bricks */
    private Counter brickCounter;

    /** Listener for user input (keyboard/mouse) */
    private UserInputListener inputListener;

    /** Graphical life counter display (heart icons) */
    private GraphicLife graphicLife;

    /** Numeric life counter display */
    private NumericLife numericLife;

    /** Sound effect played on collision */
    private Sound collisionSound;

    /** Paddle controlled by the player */
    private Paddle userPaddle;

    /** Number of lives the player has left */
    private int livesLeft;



    /**
     * Constructs a Bricker game manager with default settings
     * for rows and bricks per row.
     *
     * @param windowTitle The title of the game window.
     * @param windowDimensions The size of the game window.
     */

    public BrickerGameManager(String windowTitle, Vector2 windowDimensions) {
        super(windowTitle, windowDimensions);
        this.rows = DEFAULT_ROWS;
        this.bricksPerRow = DEFAULT_BRICKS_PER_ROW;
        this.width = windowDimensions.x();
        this.height = windowDimensions.y();
    }

    /**
     * Constructs a Bricker game manager with a custom number of bricks per row and rows.
     *
     * @param windowTitle The title of the game window.
     * @param windowDimensions The size of the game window.
     * @param bricksPerRow Number of bricks in each row.
     * @param numRows Number of rows of bricks.
     */
    public BrickerGameManager(String windowTitle, Vector2 windowDimensions,
                              int bricksPerRow, int numRows){
        super(windowTitle, windowDimensions);
        this.rows = numRows;
        this.bricksPerRow = bricksPerRow;
        this.width = windowDimensions.x();
        this.height = windowDimensions.y();
    }

    /**
     * game initializer
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.windowController = windowController;
        this.inputListener = inputListener;
        this.livesLeft = INITIAL_LIVES;
        Vector2 windowDimensions = windowController.getWindowDimensions();
        brickCounter = new Counter();

        Renderable paddleImage = imageReader.readImage(PADDLE_ASSET_PATH , true);

        createUserPaddle(inputListener, paddleImage, windowDimensions);

        createWalls(windowDimensions);

        createBackground(imageReader, windowDimensions);

        createBall(imageReader, soundReader, windowDimensions);

        addBricks(imageReader);

        createLivesIndicators(imageReader);
    }


    /**
     * Initializes the heart icons (graphicLife) and numeric counter (numericLife)
     * representing remaining lives in the top-left corner of the screen.
     *
     * @param imageReader The image reader to load the heart asset from.
     */
    private void createLivesIndicators(ImageReader imageReader) {
        Renderable heartImage = imageReader.readImage(HEART_ASSET_PATH ,
                true);
        graphicLife = new GraphicLife(
                new Vector2(HEART_SIZE, HEART_SIZE),
                heartImage,
                gameObjects(),
                INITIAL_LIVES
        );
        numericLife = new NumericLife(new Vector2(HEART_OFFSET_X, NUMERIC_LIFE_OFFSET_Y),
                gameObjects(), INITIAL_LIVES);
    }

    /**
     * Creates the user's paddle and adds it to the game.
     * @param inputListener Listener for keyboard inputs.
     * @param paddleImage Image to render the paddle.
     * @param windowDimensions Dimensions of the game window, used for centering the paddle.
     */
    private void createUserPaddle(UserInputListener inputListener,
                                  Renderable paddleImage, Vector2 windowDimensions) {
        userPaddle = new Paddle(Vector2.ZERO, USER_PADDLE_SIZE,
                paddleImage, inputListener, width);
        userPaddle.setCenter(new Vector2(windowDimensions.x()/ CENTER_DIVISOR,
                windowDimensions.y()-PADDLE_BOTTOM_OFFSET));
        gameObjects().addGameObject(userPaddle);
    }

    /**
     * Creates and adds the background image to the game window.
     * The background image spans the entire window and is rendered
     * behind all other game layers using camera coordinates.
     *
     * @param imageReader       Used to load the background image from disk.
     * @param windowDimensions  The dimensions of the game window.
     */
    private void createBackground(ImageReader imageReader, Vector2 windowDimensions) {
        Renderable backgroundImage = imageReader.readImage(BACKGROUND_IMAGE_PATH ,
                false);
        GameObject background = new GameObject(Vector2.ZERO,
                windowDimensions, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }

    /**
     * Creates the main ball in the game, assigns it a random direction,
     * sets its initial position to the center of the screen, and adds it to the game.
     *
     * @param imageReader       Used to load the image for the ball.
     * @param soundReader       Used to load the sound played upon ball collisions.
     * @param windowDimensions  The dimensions of the game window, used for centering the ball.
     */
    private void createBall(ImageReader imageReader,
                            SoundReader soundReader, Vector2 windowDimensions) {
        Renderable ballImage = imageReader.readImage(BALL_IMAGE_PATH, true);
        collisionSound = soundReader.readSound(BALL_SOUND_PATH);
        Ball mainBall = new Ball(Vector2.ZERO, BALL_DIMENSIONS, ballImage, collisionSound);
        mainBall.setCenter(windowDimensions.mult(BALL_INITIAL_POSITION_FACTOR));
        gameObjects().addGameObject(mainBall);
        float ballVelX = BALL_SPD;
        float ballVelY = BALL_SPD;
        Random rand = new Random();
        if(rand.nextBoolean()) {
            ballVelX *= DIRECTION_INVERSION;
        }
        if(rand.nextBoolean()) {
            ballVelY *= DIRECTION_INVERSION;
        }
        mainBall.setVelocity(new Vector2(ballVelX, ballVelY));
        this.mainBall = mainBall;
    }

    /**
     * Called every frame to update game logic.
     * Handles win/loss conditions, ball reset, and user prompt when necessary.
     *
     * @param deltaTime Time since last frame, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        double ballHeight = mainBall.getCenter().y();
        Vector2 middle = new Vector2(width / CENTER_DIVISOR,  height / CENTER_DIVISOR);
        String prompt = EMPTY;

        if(inputListener.isKeyPressed(KeyEvent.VK_W)){
            prompt = WIN;
        }

        if(brickCounter.value() == 0){
            prompt = WIN;
        }
        if(ballHeight < 0){
            prompt = WIN;
        }
        prompt = checkLose(ballHeight, middle, prompt);
        if (prompt == null) return;
        handleGameEnd(prompt);
    }

    /**
     * Checks whether the player has lost a life or the game.
     * If the ball has fallen below the bottom of the window, the player loses a life.
     * - If lives remain, the ball is reset to the center and the life counters are updated.
     * - If no lives remain, the game is considered lost and a "LOSE" message is returned.
     *
     * @param ballHeight The current Y-position of the ball.
     * @param middle     The center of the window, used to reset the ball's position.
     * @param prompt     The current game-end prompt message (empty if no condition met).
     * @return "You Lose!" if no lives remain, or {@code null} if game should continue.
     */
    private String checkLose(double ballHeight, Vector2 middle, String prompt) {
        if(ballHeight > height){
            livesLeft--;
                if (livesLeft > 0) {
                mainBall.setCenter(middle);
                graphicLife.updateLives(livesLeft);
                numericLife.updateLives(livesLeft);
                    return null;
            }
            else{
                prompt = LOSE;
            }
        }
        return prompt;
    }


    /**
     * Handles the end-of-game scenario based on the provided prompt.
     * If the prompt is not empty, it shows a dialog asking the player whether
     * they want to play again.
     * If the player chooses to play again, the game is reset with initial lives.
     * Otherwise, the game window is closed.
     *
     * @param prompt The message to display to the user at the end of the game (e.g., win/lose).
     */
    private void handleGameEnd(String prompt) {
        if(!prompt.isEmpty()){
            prompt += PLAY_AGAIN;
            if(windowController.openYesNoDialog(prompt)){
                livesLeft = INITIAL_LIVES;
                windowController.resetGame();
            }
            else{
                windowController.closeWindow();
            }
        }
    }


    /**
     * Removes a game object from the game's active object collection.
     *
     * @param obj The GameObject to remove.
     * @return true if the object was successfully removed; false otherwise.
     */
    public boolean removeGameObjectFromGame(GameObject obj) {
        return gameObjects().removeGameObject(obj);
    }

    /**
     * Adds a game object to the game's active object collection.
     *
     * @param obj The GameObject to add.
     */
    public void addGameObjectFromGame(GameObject obj) {
        gameObjects().addGameObject(obj);
    }

    /**
     * Retrieves the dimensions of the game window.
     *
     * @return A Vector2 representing the width and height of the window.
     */
    public Vector2 getWindowDimensions() {
        return windowController.getWindowDimensions();
    }

    /**
     * Adds bricks to the game board during initialization.
     * Calculates brick dimensions and positions based on window size and spacing constants.
     *
     * @param imageReader Used to load the brick image asset.
     */
    public void addBricks(ImageReader imageReader){
        Renderable brickImage = imageReader.readImage(BRICK_ASSET_PATH,
                false);

        // calculate precisely the dimensions of each brick
        float netWidth = width - (BORDER_HEIGHT * BORDER_WIDTH) -
                ((bricksPerRow + ADD_SPACES) * SPACE_BETWEEN_BRICKS);
        float bricksLength = netWidth / bricksPerRow;
        Vector2 dimension = new Vector2(bricksLength, BRICK_HEIGHT);

        // adding the bricks
        float currentWidth = BORDER_WIDTH + SPACE_BETWEEN_BRICKS;
        StrategyFactory factory = new StrategyFactory(this, brickCounter,
                collisionSound, userPaddle, imageReader, mainBall);

        for(int col = 0; col < bricksPerRow; col++){
            int currentHeight = BORDER_WIDTH;

            for(int row = 0; row < rows; row++){
                Brick current = new Brick(new Vector2(currentWidth, currentHeight),
                        dimension, brickImage, factory.randomStrategy());

                gameObjects().addGameObject(current);
                brickCounter.increment();
                currentHeight += BRICK_HEIGHT + SPACE_BETWEEN_BRICKS;
            }
            currentWidth += SPACE_BETWEEN_BRICKS + bricksLength;
        }
    }

    /**
     * Creates three boundary walls (left, right, and top) to keep the ball within the screen.
     * The bottom is intentionally left open to allow the ball to fall and trigger a life loss.
     *
     * @param windowDimensions The width and height of the game window.
     */
    private void createWalls(Vector2 windowDimensions) {
        final Vector2 wallSizeVertical = new Vector2(BORDER_WIDTH, windowDimensions.y());
        final Vector2 leftWallPosition = Vector2.ZERO;
        final Vector2 rightWallPosition = new Vector2(windowDimensions.x() - BORDER_WIDTH, 0);

        final Vector2 topWallSize = new Vector2(windowDimensions.x(), BORDER_WIDTH);
        final Vector2 topWallPosition = Vector2.ZERO;

        RectangleRenderable wallRenderable = new RectangleRenderable(BORDER_COLOR);
        GameObject leftWall = new GameObject(leftWallPosition, wallSizeVertical, wallRenderable);
        GameObject rightWall = new GameObject(rightWallPosition, wallSizeVertical, wallRenderable);
        GameObject topWall = new GameObject(topWallPosition, topWallSize, wallRenderable);

        gameObjects().addGameObject(leftWall);
        gameObjects().addGameObject(rightWall);
        gameObjects().addGameObject(topWall);
    }

    /**
     * Returns the main user-controlled paddle.
     *
     * @return the user paddle GameObject.
     */
    public Paddle getUserPaddle(){
        return userPaddle;
    }

    /**
     * Adds one life to the player, up to a maximum limit.
     * Updates both the graphic and numeric life indicators accordingly.
     */
    public void addLife() {
        if(livesLeft > 0){
            livesLeft++;
            if(livesLeft > MAX_LIVES){
                livesLeft = MAX_LIVES;
            }
            graphicLife.updateLives(livesLeft );
            numericLife.updateLives(livesLeft );
        }
    }


    /**
     * Main entry point for the Bricker game.
     * If no command-line arguments are given, the game will run
     * with default board size and configuration.
     * If two arguments are given, they are interpreted as number of
     * bricks per row and number of rows, respectively.
     *
     * @param args Command-line arguments:
     *             args[0] - number of bricks per row (int),
     *             args[1] - number of rows (int).
     */
    public static void main(String[] args) {
        if(args.length == NUM_ARGS){
            BrickerGameManager gameManager =
                    new BrickerGameManager(BRICKER,new Vector2(BOARD_WIDTH,
                            BOARD_HEIGHT), Integer.parseInt(args[0]),
                            Integer.parseInt(args[1]));
            gameManager.run();
        }
        else {
            BrickerGameManager gameManager =
                    new BrickerGameManager(BRICKER,new Vector2(BOARD_WIDTH,
                            BOARD_HEIGHT));
            gameManager.run();
        }
    }
}
