package unsw.dungeon;

public interface Component {
    public boolean complete(Player player);
    public String getDescription(int depth);
}