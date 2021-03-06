package unsw.ui;

import java.io.File;
import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import unsw.DungeonApplication;
import unsw.dungeon.Observer;
import unsw.dungeon.Subject;

public class MainMenuScene implements Subject {
    private Scene scene;
    private Stage stage;
    private StackPane root;
    private Observer application;
    private String level;
    private final String style;
    private final double width;
    private final double height;
    private final double buttonWidth = 200;
    private final double buttonHeight;
    private final double prefDimension;
    private final double imageDimension;

    public String getLevel() {
        return level;
    }

    public MainMenuScene(DungeonApplication application) throws IOException {
        stage = application.getStage();
        attach(application);

        width = application.getWidth();
        height = application.getHeight();
        prefDimension = application.getPrefDimension();
        buttonHeight = prefDimension;
        imageDimension = width / 5;
        style = application.getButtonStyle();

        root = new StackPane();
        root.setStyle("-fx-font-family: 'serif', 'arial', 'helvetica'");
        root.setAlignment(Pos.CENTER);

        GridPane gridPane = new GridPane();
        DungeonControllerLoader.loadBackground((int) (width / DungeonControllerLoader.getWidth()), (int) (height / DungeonControllerLoader.getHeight()), gridPane);

        root.getChildren().addAll(gridPane, createLevels());
        scene = new Scene(root, width, height);
    }

    private VBox createLevels() {
        Button start = new GameButton(buttonWidth, buttonHeight, "Start", style);
        Button help = new GameButton(buttonWidth, buttonHeight, "Help", style);
        Button exit = new GameButton(buttonWidth, buttonHeight, "Exit", style);
        VBox vBox = createInstructions();

        Label title = new Label("Dungeon Escape");
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, chocolate 0%, orchid 50%);");
        title.setFont(Font.loadFont("file:src/fonts/Ghostz-77qw.ttf", 100));

        StackPane logo = new StackPane(new ImageView((new Image((new File("src/images/sand_castle.png")).toURI().toString()))), title);

        HBox hBox = new HBox(start, help, exit);
        hBox.setAlignment(Pos.CENTER);

        VBox group = new VBox(logo, hBox);
        group.setAlignment(Pos.CENTER);
        group.setSpacing(height/20);
        hBox.setSpacing((width / 2 - buttonWidth*3) / 2);

        HBox levels = new HBox(createLevel("maze"), createLevel("boulders"), createLevel("advanced"), createLevel("master"));
        levels.setAlignment(Pos.CENTER);
        levels.setSpacing((imageDimension - buttonWidth / 2) / 5);

        Button back = ((DungeonApplication) application).backButton();
        back.setOnAction(event -> {
            root.getChildren().removeAll(back, levels, vBox);
            root.getChildren().add(group);
        });

        start.setOnAction(event -> {
            root.getChildren().remove(group);
            root.getChildren().addAll(levels, back);
        });

        help.setOnAction(event -> {
            root.getChildren().remove(group);
            root.getChildren().addAll(vBox, back);
        });

        exit.setOnAction(event -> {
            System.exit(0);
        });
        return group;
    }

    private VBox createInstructions() {
        VBox instructions = new VBox();
        VBox vBox = new VBox(7);
        HBox hBox = new HBox();
        Label label1 = createLabel("Watch out for the fire!", new ImageView(DungeonControllerLoader.fireImage));
        Label label2 = createLabel("Use it to open doors.", new ImageView(DungeonControllerLoader.keyImage));
        Label label3 = createLabel("SHIFT + KEY to move these out of your way!", new ImageView(DungeonControllerLoader.boulderImage));
        Label label4 = createLabel("Have some medicine to boost your health.", new ImageView(DungeonControllerLoader.medicineImage));
        Label label5 = createLabel("Drink this potion and be invincible for 5 seconds.", new ImageView(DungeonControllerLoader.potionImage));
        Label label6 = createLabel("Sword can kill up to 5 enemies.", new ImageView(DungeonControllerLoader.swordImage));
        Label label7 = createLabel("Mans best friend will sacrifice themselves for you.", new ImageView(DungeonControllerLoader.houndImage));
        Label label8 = createLabel("Try to kill the enemy before it kills YOU!", new ImageView(DungeonControllerLoader.enemyImage));
        Label label9 = createLabel("Gnomes can move everywhere, can only be killed by drinking potions.", new ImageView(DungeonControllerLoader.gnomeImage));
        Label help = new Label("HELP");

        vBox.getChildren().addAll(label1, label2, label3, label4, label5, label6, label7, label8, label9);
        hBox.getChildren().add(help);

        hBox.setAlignment(Pos.CENTER);
        vBox.setMaxSize(width/4*3, height/3*2);
        label6.setWrapText(true);
        help.setStyle("-fx-text-fill: mediumseagreen;");
        help.setFont(Font.loadFont("file:src/fonts/Ghostz-77qw.ttf", 120));

        vBox.setStyle("-fx-border-color: chocolate; -fx-border-insets: 5; -fx-border-width: 3; -fx-background-color: blanchedalmond; -fx-border-radius: 20; -fx-background-radius: 20;");
        vBox.setPadding(new Insets(prefDimension));
        instructions.getChildren().addAll(hBox, vBox);
        instructions.setAlignment(Pos.CENTER);
        return instructions;
    }

    private Label createLabel(String text, ImageView view){
        Label label = new Label(text, view);
        view.setFitWidth(prefDimension);
        view.setFitHeight(prefDimension);
        label.setStyle("-fx-font-size: 2em;");
        label.setGraphicTextGap(prefDimension);
        return label;
    }

    private Button createLevel(String level) {
        Button button = new GameButton(imageDimension + buttonHeight/2, imageDimension + buttonHeight, level, new ImageView(new Image((new File("examples/" + level + ".png")).toURI().toString(), imageDimension, imageDimension, true, true)), style);
        button.setOnAction(event -> {
            this.level = level + ".json";
            notifyObservers();
        });
        return button;
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