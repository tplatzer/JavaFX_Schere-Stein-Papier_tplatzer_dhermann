package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Controller
{
    /**
     * Rock
     * Paper
     * Sissores
     * Well
     */
    private static final int maxButtonWidth = 200;
    private static final int maxButtonHeight = 50;
    private static final int maxHboxWidth = 1000;
    private static final int maxHboxHeight = 700;
    private static final int maxProgressIndicatorWidth = 200;
    private static final int maxProgressIndicatorHeight = 100;

    private String userChoice = null;
    private String aiChoice = null;
    private final HashMap<Integer, String> playStonesIDs = new HashMap<>();
    private Controller controller;
    private VBox root = new VBox();
    private Stage stage;
    private Button rockButton;
    private Button paperButton;
    private Button scissorsButton;
    private Button wellButton;
    private ProgressIndicator enemieProgressIndicator;

    private HBox progressBox = new HBox();
    private HBox enemyBox = new HBox();
    private HBox tableBox = new HBox();
    private HBox buttonBox = new HBox();

    public void initializeUserElements()
    {
        playStonesIDs.put(0, Rock.getId());
        playStonesIDs.put(1, Paper.getId());
        playStonesIDs.put(2, Scissors.getId());
        playStonesIDs.put(3, Well.getId());

        setRockButton(initializeButton(playStonesIDs.getOrDefault(0, null), new Image("file:resources/img/hovered/stone.png")));
        setPaperButton(initializeButton(playStonesIDs.getOrDefault(1, null), new Image("file:resources/img/hovered/paper.png")));
        setScissorsButton(initializeButton(playStonesIDs.getOrDefault(2, null), new Image("file:resources/img/hovered/scissors.png")));
        setWellButton(initializeButton(playStonesIDs.getOrDefault(3, null), new Image("file:resources/img/hovered/stone.png")));
        setEnemieProgressIndicator(initializeProgressIndicator());

        initializeButtonBox(10);

        ImageView computerHand = initializeImageView(new Image("file:resources/masterHand_default.png"));
        ImageView table = initializeImageView(new Image("file:resources/table.png"));

//        ImageView computerHand = new ImageView();
        computerHand.setImage(new Image("file:resources/masterHand_default.png"));
        computerHand.fitWidthProperty().bind(getStage().widthProperty().multiply(0.1)); // 10% der Fensterbreite
        computerHand.fitHeightProperty().bind(getStage().heightProperty().multiply(0.2)); // 20% der Fensterhöhe        computerHand.setFitHeight(200);
        enemyBox.getChildren().add(computerHand);

//        ImageView table = new ImageView();
        table.setImage(new Image("file:resources/table.png"));
        table.fitWidthProperty().bind(getStage().widthProperty().multiply(1));
        table.fitHeightProperty().bind(getStage().heightProperty().multiply(.5));
        tableBox.getChildren().add(table);

        addImageViewsToBoxes(getEnemyBox(), computerHand);
        addImageViewsToBoxes(getTableBox(), table);
    }

    public void start(Stage stage, Controller controller) throws FileNotFoundException
    {
        setStage(stage);
        setController(controller);
        initializeUserElements();

        showWindow();
    }

    public void showWindow()
    {
        getRoot().setSpacing(10);
        getRoot().setMinSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        getRoot().setMaxSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
//        getRoot().getChildren().addAll(rockButton, paperButton, scissorsButton, wellButton, enemieProgressIndicator);

        root.getChildren().addAll(progressBox, enemyBox, tableBox, buttonBox);

        Group group = new Group(root);
        group.setAutoSizeChildren(true);

        Scene scene = new Scene(group);
        scene.getStylesheets().add("file:resources/style.css");
        stage.setScene(scene);
        stage.setTitle("Schere Stein Papier");
//        stage.setMaxHeight(900);
        stage.show();
    }

    private Button initializeButton(String id, Image image)
    {
        Button button = new Button();
        button.setMinSize(getMaxButtonWidth(), getMaxButtonHeight());
        button.setMaxSize(getMaxButtonWidth(), getMaxButtonHeight());
        button.setId(id);
        button.setText(id);
        button.setGraphic(new ImageView(image));
        button.setOnAction(event ->
        {
            try
            {
                handleButtonClick(event);
            } catch (InterruptedException e)
            {
                throw new RuntimeException(e);
            }
        });
        return button;
    }

    private void addImageViewsToBoxes(HBox box, ImageView image)
    {
        box.getChildren().add(image);
    }

    private ImageView initializeImageView(Image image)
    {
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        return imageView;
    }

    private void initializeButtonBox(int spacing)
    {
        buttonBox.setSpacing(spacing);
        buttonBox.setMinSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        buttonBox.setMaxSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        buttonBox.getChildren().addAll(getRockButton(), getPaperButton(), getScissorsButton(), getWellButton());
    }

    private ProgressIndicator initializeProgressIndicator()
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setId("enemie_progress_indicator");
        progressIndicator.setMinSize(maxButtonWidth, maxButtonHeight);

        return progressIndicator;
    }

    private void handleButtonClick(ActionEvent event) throws InterruptedException
    {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();

        System.out.println(buttonId);

        switch (buttonId)
        {
            case Rock.id -> setUserChoice(playStonesIDs.getOrDefault(0, null));
            case Paper.id -> setUserChoice(playStonesIDs.getOrDefault(1, null));
            case Scissors.id -> setUserChoice(playStonesIDs.getOrDefault(2, null));
            case Well.id -> setUserChoice(playStonesIDs.getOrDefault(3, null));
        }

        play();
    }

    private void play() throws InterruptedException
    {
        aiTurn();

        Thread.sleep(10000);


        System.out.println(getAiChoice());
        System.out.println();
    }

    private void aiTurn()
    {
        Random random = new Random();
        setAiChoice(random.nextInt(4));
    }


    public static int getMaxButtonWidth()
    {
        return maxButtonWidth;
    }

    public static int getMaxButtonHeight()
    {
        return maxButtonHeight;
    }

    public static int getMaxHboxWidth()
    {
        return maxHboxWidth;
    }

    public static int getMaxHboxHeight()
    {
        return maxHboxHeight;
    }

    public static int getMaxProgressIndicatorWidth()
    {
        return maxProgressIndicatorWidth;
    }

    public static int getMaxProgressIndicatorHeight()
    {
        return maxProgressIndicatorHeight;
    }

    public String getUserChoice()
    {
        return userChoice;
    }

    public void setUserChoice(String userChoice)
    {
        this.userChoice = userChoice;
    }

    public Button getRockButton()
    {
        return rockButton;
    }

    public void setRockButton(Button rockButton)
    {
        this.rockButton = rockButton;
    }

    public Button getPaperButton()
    {
        return paperButton;
    }

    public void setPaperButton(Button paperButton)
    {
        this.paperButton = paperButton;
    }

    public Button getScissorsButton()
    {
        return scissorsButton;
    }

    public void setScissorsButton(Button scissorsButton)
    {
        this.scissorsButton = scissorsButton;
    }

    public Button getWellButton()
    {
        return wellButton;
    }

    public void setWellButton(Button wellButton)
    {
        this.wellButton = wellButton;
    }

    public ProgressIndicator getEnemieProgressIndicator()
    {
        return enemieProgressIndicator;
    }

    public void setEnemieProgressIndicator(ProgressIndicator enemieProgressIndicator)
    {
        this.enemieProgressIndicator = enemieProgressIndicator;
    }

    public VBox getRoot()
    {
        return this.root;
    }

    public void setRoot(VBox root)
    {
        this.root = root;
    }

    public Stage getStage()
    {
        return this.stage;
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    public Controller getController()
    {
        return controller;
    }

    public void setController(Controller controller)
    {
        this.controller = controller;
    }

    public String getAiChoice()
    {
        return aiChoice;
    }

    public void setAiChoice(int aiChoice)
    {
        switch (aiChoice)
        {
            case 0 -> this.aiChoice = playStonesIDs.getOrDefault(0, null);
            case 1 -> this.aiChoice = playStonesIDs.getOrDefault(1, null);
            case 2 -> this.aiChoice = playStonesIDs.getOrDefault(2, null);
            case 3 -> this.aiChoice = playStonesIDs.getOrDefault(3, null);
        }

    }

    public HBox getProgressBox()
    {
        return progressBox;
    }

    public void setProgressBox(HBox progressBox)
    {
        this.progressBox = progressBox;
    }

    public HBox getEnemyBox()
    {
        return enemyBox;
    }

    public void setEnemyBox(HBox enemyBox)
    {
        this.enemyBox = enemyBox;
    }

    public HBox getTableBox()
    {
        return tableBox;
    }

    public void setTableBox(HBox tableBox)
    {
        this.tableBox = tableBox;
    }

    public HBox getButtonBox()
    {
        return buttonBox;
    }

    public void setButtonBox(HBox buttonBox)
    {
        this.buttonBox = buttonBox;
    }
}
