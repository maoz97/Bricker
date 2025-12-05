package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Displays graphical hearts in the top-left corner of the screen to represent the number of
 * lives that are left.
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * @see bricker.main.BrickerGameManager
 */
public class GraphicLife extends GameObject {
    /** The maximum number of hearts that can be displayed. */
    private static final int MAX_LIVES = 4;

    /** The pixel size of each heart icon. */
    private static final int SIZE_OF_HEART = 30;

    /** The distance between each heart. */
    private static final float DISTANCE_BETWEEN_HEARTS = 5f;

    /** A vector representing the width and height of each heart. */
    private static final Vector2 HEART_SIZE = new Vector2(SIZE_OF_HEART, SIZE_OF_HEART);

    /**
     * private members
     * hearts - an array containing the hearts that are on display
     * gameObjects - the collection of all the objects that are in the game
     */
    private final Heart[] hearts;
    private final GameObjectCollection gameObjects;
    private int currentLives;


    /**
     * Constructor for graphic life counter
     * @param position - position on screen
     * @param heartRenderable - the image of the heart
     * @param gameObjects - the game's object collection
     * @param initialLives - number of lives to show initially
     */
    public GraphicLife(Vector2 position,
                       Renderable heartRenderable,
                       GameObjectCollection gameObjects,
                       int initialLives) {
        super(position, Vector2.ZERO, null);
        this.gameObjects = gameObjects;
        this.hearts = new Heart[MAX_LIVES];
        this.currentLives = initialLives;

        // Create and add heart GameObjects
        createHearts(position, heartRenderable, gameObjects, initialLives);
    }


    private void createHearts(Vector2 position, Renderable heartRenderable,
                              GameObjectCollection gameObjects, int initialLives) {
        for (int i = 0; i < MAX_LIVES; i++) {
            Vector2 heartPosition = position.add(new Vector2(i *
                    (SIZE_OF_HEART + DISTANCE_BETWEEN_HEARTS), 0));
            Heart heart = new Heart(heartPosition, heartRenderable);
            hearts[i] = heart;
            if (i < initialLives) {
                gameObjects.addGameObject(heart, Layer.UI);
            }
        }
    }


    /**
     * Updates how many hearts are visible
     * @param livesRemaining number of lives to show
     */
    public void updateLives(int livesRemaining) {
        if (livesRemaining == currentLives) return;

        for (int i = 0; i < MAX_LIVES; i++) {
            if (i < livesRemaining && i >= currentLives) {
                gameObjects.addGameObject(hearts[i], Layer.UI);
            } else if (i >= livesRemaining && i < currentLives) {
                gameObjects.removeGameObject(hearts[i], Layer.UI);
            }
        }
        currentLives = livesRemaining;
    }

}
