package unsw.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import unsw.DungeonApplication;
import unsw.dungeon.Dungeon;
import unsw.dungeon.Observer;
import unsw.dungeon.Subject;

public class DungeonScene implements Subject {
    private Scene scene;
    private Stage stage;
    private Parent root;
    private Label text;
    private HBox buttons;
    private DungeonController controller;
    private Observer application;
    private List<Node> children;
    private StackPane gameOver;
    private Node squares;
    private Node setting;
    private final double width;
    private final double height;
    private final int totalLevels = 4;
    private String level;
    private List<String> levels = new ArrayList<>();
    private Dungeon dungeon;

    public DungeonScene(DungeonApplication application) throws IOException {
        stage = application.getStage();
        attach(application);

        level = application.getLevel();
        initialiseLevels();

        width = application.getWidth();
        height = application.getHeight();

        DungeonControllerLoader dungeonLoader = new DungeonControllerLoader(application);
        controller = dungeonLoader.loadController(application);
        dungeon = controller.getDungeon();   

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../DungeonView.fxml"));
        loader.setController(controller);     

        root = loader.load();
        scene = new Scene(root, application.getWidth(), application.getHeight());

        children = root.getChildrenUnmodifiable();
        squares = children.get(0);
        gameOver = (StackPane) children.get(1);
        setting = children.get(2);

        squares.requestFocus();
        layout();
    }

    public String getLevel() {
        return level;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    private void initialiseLevels() {
        levels.add("maze.json");
        levels.add("boulders.json");
        levels.add("advanced.json");
        levels.add("master.json");
    }

    private void layout() {
        gameOver.setPrefSize(width / 4 * 3, height / 3);
        gameOver.setLayoutX(width / 2 - gameOver.getPrefWidth() / 2);
        gameOver.setLayoutY(height / 2 - gameOver.getPrefHeight() / 2);

        text = (Label) gameOver.getChildren().get(0);
        StackPane.setAlignment(text, Pos.TOP_CENTER);

        buttons = (HBox) gameOver.getChildren().get(1);
        buttons.setAlignment(Pos.BOTTOM_CENTER);
    }

    public void gameOver(Dungeon dungeon) {
        String style = controller.getStyle();
        Button button = (Button) buttons.getChildren().get(1);
        if (dungeon.isComplete()) {
            int nextLevel = levels.indexOf(level) + 1;
            if (nextLevel < totalLevels) {
                level = levels.get(nextLevel);
                text.setStyle(style + "-fx-text-fill: gold");
                text.setText("You Won!");
                button.setText("Continue");
                button.setOnAction(event -> {
                    notifyObservers();
                });
            } else { // This is the last level
                text.setStyle(controller.getStyle());
                text.setText("You Completed All Levels!");
                buttons.getChildren().remove(button);
            }
            rotateText(text);
        } else {
            text.setText("You Lost!");
            text.setStyle(style + "-fx-text-fill: red");
            button.setText("Restart");
        }
        fadeTransition(text);
        dungeon.setPause();
        root.requestFocus();
        controller.blur(new GaussianBlur());
        setting.setDisable(true);
        gameOver.setVisible(true);
    }

    private void fadeTransition(Label text) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), text);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void rotateText(Label text) {
        RotateTransition rt = new RotateTransition(Duration.millis(500), text);
        rt.setByAngle(10);
        rt.setCycleCount(2);
        rt.setAutoReverse(true);
        rt.play();
    }

    public void start() {
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void attach(Observer observer) {
        application = observer;
    }

    @Override
    public void detach(Observer observer) {
        application = null;
    }

    @Override
    public void notifyObservers() {
        application.update(this);
    }
}