package bricker.brick_strategies;
import danogl.GameObject;


/**
 * An interface for defining custom collision behaviors between a brick and another game object.
 * Implementing this interface allows different collision responses when a brick is hit.
 *
 * @author Ishay Shaul
 * @author Maoz Bar Shimon
 * */
public interface CollisionStrategy {

    /**
     * Defines the behavior when a collision occurs between the brick (thisObj)
     * and another game object (otherObj).
     *
     * @param thisObj  The brick being hit.
     * @param otherObj The object that hit the brick (e.g., the ball or puck).
     */
    void onCollision(GameObject thisObj, GameObject otherObj);
}
