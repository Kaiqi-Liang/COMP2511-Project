package unsw.dungeon;

import java.util.Random;

public class MoveThrough extends Decorator {

    MoveThrough(Dungeon dungeon, Character character, Strategy strategy) {
        super(dungeon, character, strategy);
    }

    @Override
    boolean canMove() {
        super.canMove();
        return true;
    }

    @Override
    void move() {
        if (player.getPrevPosition() != player.getCurrPosition()) { // the gnome only moves when the player moves successfully and not get blocked
            Random move = new Random();
            switch (move.nextInt(4)) {
                case 0:
                    moveUp();
                    break;
                case 1:
                    moveDown();
                    break;
                case 2:
                    moveLeft();
                    break;
                case 3:
                    moveRight();
                    break;
                default:
                    break;
            }
        }
    }
}