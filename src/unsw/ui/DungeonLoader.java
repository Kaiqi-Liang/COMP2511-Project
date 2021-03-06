package unsw.ui;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import unsw.DungeonApplication;
import unsw.dungeon.And;
import unsw.dungeon.Boulder;
import unsw.dungeon.Component;
import unsw.dungeon.Composite;
import unsw.dungeon.Door;
import unsw.dungeon.Dungeon;
import unsw.dungeon.Enemy;
import unsw.dungeon.Entity;
import unsw.dungeon.Exit;
import unsw.dungeon.Fire;
import unsw.dungeon.Gnome;
import unsw.dungeon.GoalBoulders;
import unsw.dungeon.GoalEnemies;
import unsw.dungeon.GoalExit;
import unsw.dungeon.GoalTreasure;
import unsw.dungeon.Hound;
import unsw.dungeon.Key;
import unsw.dungeon.Medicine;
import unsw.dungeon.Observer;
import unsw.dungeon.Or;
import unsw.dungeon.Player;
import unsw.dungeon.Portal;
import unsw.dungeon.Potion;
import unsw.dungeon.Switch;
import unsw.dungeon.Sword;
import unsw.dungeon.Treasure;
import unsw.dungeon.Wall;

/**
 * Loads a dungeon from a .json file.
 *
 * By extending this class, a subclass can hook into entity creation. This is
 * useful for creating UI elements with corresponding entities.
 *
 * @author Robert Clifton-Everest
 *
 */
public abstract class DungeonLoader implements Observer {

    private JSONObject json;
    private Component goal;
    private Map<Integer, Portal> portals = new HashMap<>();
    private Map<Integer, Door> doors = new HashMap<>();
    private Map<Integer, Key> keys = new HashMap<>();
    private DungeonApplication application;

    public DungeonLoader(DungeonApplication application) throws FileNotFoundException {
        this.application = application;
        json = new JSONObject(new JSONTokener(new FileReader("dungeons/" + application.getLevel())));
    }

    /**
     * Parses the JSON to create a dungeon.
     * @return
     */
    public Dungeon load() {
        Dungeon dungeon = new Dungeon((int) (application.getWidth()/DungeonControllerLoader.getWidth()), (int) (application.getHeight()/DungeonControllerLoader.getHeight()));

        JSONArray jsonEntities = json.getJSONArray("entities");

        loadGoals(json.getJSONObject("goal-condition"), null);

        for (int i = 0; i < jsonEntities.length(); i++) {
            loadEntity(dungeon, jsonEntities.getJSONObject(i));
        }

        initialise(dungeon);
        return dungeon;
    }

    private void initialise(Dungeon dungeon) {
        dungeon.attach(application);
        dungeon.attach(this);

        dungeon.initialise();
        dungeon.setGoal(goal);
    }

    private void loadGoals(JSONObject goals, Composite condition) {
        String goal = goals.getString("goal");

        switch (goal) {
            case "AND":
                Composite and = new And();
                if (condition == null) this.goal = and;
                else condition.add(and);
                condition(goals.getJSONArray("subgoals"), and);
                break;
            case "OR":
                Composite or = new Or();
                if (condition == null) this.goal = or;
                else condition.add(or);
                condition(goals.getJSONArray("subgoals"), or);
                break;
            default:
                Component leaf = null;
                switch(goal) {
                    case "exit":
                        leaf = new GoalExit();
                        break;
                    case "enemies":
                        leaf = new GoalEnemies();
                        break;
                    case "boulders":
                        leaf = new GoalBoulders();
                        break;
                    case "treasure":
                        leaf = new GoalTreasure();
                        break;
                    default:
                        break;
                }
                if (condition == null) this.goal = leaf;
                else condition.add(leaf);
                break;
        }

    }

    private void condition(JSONArray subgoals, Composite condition) {
        for (int i = 0; i < subgoals.length(); i++) {
            loadGoals(subgoals.getJSONObject(i), condition);
        }
    }

    private void loadEntity(Dungeon dungeon, JSONObject json) {
        String type = json.getString("type");
        int x = json.getInt("x");
        int y = json.getInt("y");

        Entity entity = null;
        switch (type) {
        case "player":
            Player player = new Player(dungeon, x, y);
            dungeon.setPlayer(player);
            onLoad(player);
            entity = player;
            break;
        case "wall":
            Wall wall = new Wall(x, y);
            onLoad(wall);
            entity = wall;
            break;
        case "exit":
            Exit exit = new Exit(x, y);
            onLoad(exit);
            entity = exit;
            break;
        case "treasure":
            Treasure treasure = new Treasure(x, y);
            onLoad(treasure);
            entity = treasure;
            break;
        case "door":
            Door door = new Door(x, y);
            onLoad(door);
            entity = door;

            int doorId = json.getInt("id");
            if (keys.containsKey(doorId)) {
                keys.get(doorId).setDoor(door);
            } else {
                doors.put(doorId, door);
            }
            break;
        case "key":
            Key key = new Key(x, y);
            onLoad(key);
            entity = key;

            int keyId = json.getInt("id");
            if (doors.containsKey(keyId)) {
                key.setDoor(doors.get(keyId));
            } else {
                keys.put(keyId, key);
            }
            break;
        case "boulder":
            Boulder boulder = new Boulder(x, y);
            onLoad(boulder);
            entity = boulder;
            break;
        case "switch":
            Switch floorSwitch = new Switch(x, y);
            onLoad(floorSwitch);
            entity = floorSwitch;
            break;
        case "portal":
            Portal portal = new Portal(x, y);
            onLoad(portal);
            entity = portal;

            int portalId = json.getInt("id");
            if (portals.containsKey(portalId)) {
                portal.setPortal(portals.get(portalId));
                portals.get(portalId).setPortal(portal);
            } else {
                portals.put(portalId, portal);
            }
            break;
        case "enemy":
            Enemy enemy = new Enemy(dungeon, x, y);
            onLoad(enemy);
            entity = enemy;
            break;
        case "sword":
            Sword sword = new Sword(x, y);
            onLoad(sword);
            entity = sword;
            break;
        case "invincibility":
            Potion potion = new Potion(x, y);
            onLoad(potion);
            entity = potion;
            break;
        case "medicine":
            Medicine medicine = new Medicine(x, y);
            onLoad(medicine);
            entity = medicine;
            break;
        case "hound":
            Hound hound = new Hound(dungeon, x, y);
            onLoad(hound);
            entity = hound;
            break;
        case "gnome":
            Gnome gnome = new Gnome(dungeon, x, y);
            onLoad(gnome);
            entity = gnome;
            break;
        case "fire":
            Fire fire = new Fire(x, y);
            onLoad(fire);
            entity = fire;
            break;
        default:
            System.out.println("Invalid entity");
            break;
        }
        if (entity != null) dungeon.addEntity(entity);
    }

    public abstract void onLoad(Entity player);

    public abstract void onLoad(Wall wall);

    public abstract void onLoad(Exit exit);

    public abstract void onLoad(Portal portal);

    public abstract void onLoad(Key key);

    public abstract void onLoad(Door door);

    public abstract void onLoad(Switch floorSwitch);

    public abstract void onLoad(Boulder boulder);

    public abstract void onLoad(Treasure treasure);

    public abstract void onLoad(Enemy enemy);

    public abstract void onLoad(Sword sword);

    public abstract void onLoad(Potion potion);

    public abstract void onLoad(Medicine medicine);

    public abstract void onLoad(Hound hound);

    public abstract void onLoad(Gnome gnome);

    public abstract void onLoad(Fire fire);
}