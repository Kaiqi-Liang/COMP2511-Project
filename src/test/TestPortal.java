package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Test;

import unsw.dungeon.Dungeon;
import unsw.dungeon.Player;
import unsw.dungeon.Portal;

public class TestPortal {
    private Dungeon dungeon = new Dungeon(4, 4);
    private Player player = new Player(dungeon, 0, 0);
    private Portal portal1 = new Portal(0, 1);
    private Portal portal2 = new Portal(1, 0);

    public void initialise() {
        dungeon.setPlayer(player);
        portal1.setPortal(portal2);
        portal2.setPortal(portal1);
        dungeon.addEntity(portal1);
        dungeon.addEntity(portal2);
    }

    /**
     * Given a player goes through a portal.
     * Then the player appears on the corresponding portal.
     */
    @Test
    public void testPlayer() {
        initialise();
        player.moveDown();
        assertEquals(player.getX(), 1);
        assertEquals(player.getY(), 0);
    }
}