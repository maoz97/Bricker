package bricker.gameobjects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a secondary ball (Puck) that appears as a bonus.
 * the puck behaves like the main ball in terms of bouncing,
 * but does not affect the lives of the player, and are removed once they fall off-screen
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 */
public class Puck extends Ball {
    /** sound played when the puck collides with another object.*/
    private final Sound collisionSound;

    /** the game manage, used to access the game logic.*/
    private final BrickerGameManager gameManager;

    /**
     * Constructor for a puck ball.
     *
     * @param topLeftCorner  The position of the puck.
     * @param dimensions     The size (should be 3/4 of main ball).
     * @param renderable     The visual representation of the puck(mockBall.png).
     * @param collisionSound The same sound as the main ball.
     * @param gameManager    Reference to the game manager (used to remove the puck).
     */
    public Puck(Vector2 topLeftCorner, Vector2 dimensions,
                Renderable renderable, Sound collisionSound,
                BrickerGameManager gameManager) {
        super(topLeftCorner, dimensions, renderable, collisionSound);
        this.gameManager = gameManager;
        this.collisionSound = collisionSound;
    }

    /**
     * Update puck every frame: remove it from the game if it falls below the screen.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float screenHeight = gameManager.getWindowDimensions().y();
        if (getTopLeftCorner().y() > screenHeight) {
            gameManager.removeGameObjectFromGame(this);
        }
    }

    /**
     * handles collisions by playing a sound and bouncing off other objects.
     * Contrary to the main ball, this ball does not increment any collision counter,
     * as to not affect the turbo strategy
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        Vector2 newVelocity = getVelocity().flipped(collision.getNormal());
        setVelocity(newVelocity);
        collisionSound.play();
    }
}
