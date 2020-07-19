package unsw.dungeon;

import java.util.Timer;
import java.util.TimerTask;

public class Enemy extends Entity implements Observer {
    private Strategy strategy;
    private Strategy moveToward;
    private Strategy moveAway;
    private Timer timer;

    public Enemy(Dungeon dungeon, int x, int y) {
        super(x, y);
        moveToward = new MoveToward(dungeon, this);
        moveAway = new MoveAway(dungeon, this);
        strategy = moveToward;
    }

    public void initilise(Player player) {
        moveToward.setPlayer(player);
        moveToward.setCurrentPosition();
        moveAway.setPlayer(player);
        moveAway.setCurrentPosition();

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                strategy.move();
            }
        }, 3000, 500);
    }

    public void cancelTimer() {
        timer.cancel();
        timer.purge();
    }

	public void collide(Player player) {
        if (player.getSword() != null || player.getPotion() != null) {
            if (player.getPotion() == null) player.hit(); // if the player doesn't have a potion they must've had a sword
            player.kill(this);
        } else {
            player.die();
        }
    }


    @Override
    public void update(Subject subject) {
        // TODO change to pull to lower the coupling with Player
        if (subject.getClass() == Player.class) strategy = moveAway;
        else if (subject.getClass() == Potion.class) strategy = moveToward;
    }

    public void reset() {
        strategy.reset();
    }
}
