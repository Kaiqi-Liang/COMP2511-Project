package unsw.dungeon;

public class Hound extends Entity implements Observer {
    private Strategy strategy;

    public Hound(Dungeon dungeon, int x, int y) {
        super(x, y);
        strategy = new MoveWith(dungeon, this);
    }

    void initialise(Player player) {
        strategy.setPlayer(player);
        player.attach(this);
    }

    @Override
    public void update(Subject subject) {
        strategy.move();
    }
}