package unsw.dungeon;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The player entity
 * 
 * @author Robert Clifton-Everest
 *
 */
public class Player extends Entity {

    private Dungeon dungeon;

    /**
     * Create a player positioned in square (x,y)
     * @param x
     * @param y
     */
    public Player(Dungeon dungeon, int x, int y) {
        super(x, y);
        this.dungeon = dungeon;
    }

    private List<Entity> getWalls() {
        return dungeon.getEntities().stream().filter(entity -> entity instanceof Wall).collect(Collectors.toList());
    }

    private boolean canMove() {
        if(getWalls().contains(this)) return false;
        return true;
    }

    public void moveUp() {
        if (getY() > 0);
            y().set(getY() - 1);
        if (!canMove()) y().set(getY() + 1);
    }

    public void moveDown() {
        if (getY() < dungeon.getHeight() - 1)
            y().set(getY() + 1);
        if (!canMove()) y().set(getY() - 1);
    }

    public void moveLeft() {
        if (getX() > 0)
            x().set(getX() - 1);
        if (!canMove()) x().set(getX() + 1);
    }

    public void moveRight() {
        if (getX() < dungeon.getWidth() - 1)
            x().set(getX() + 1);
        if (!canMove()) x().set(getX() - 1);
    }
}
