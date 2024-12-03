package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

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
    private HashMap<Integer, String> playStonesIDs = new HashMap<>();
    private Controller controller;
    private VBox root = new VBox();
    private Stage stage;
    private Button rockButton;
    private Button paperButton;
    private Button scissorsButton;
    private Button wellButton;
    private ProgressIndicator enemieProgressIndicator;
    private ImageView computerHand;

    private HBox progressBox = new HBox();
    private HBox enemyBox = new HBox();
    private HBox tableBox = new HBox();
    private HBox buttonBox = new HBox();

    public void start(Stage stage, Controller controller)
    {
        setStage(stage);
        setController(controller);
        initializeUserElements();

        showWindow();
    }

    private void play()
    {
        Thread sleepThread = new Thread(() ->
        {
            try
            {
                Thread.sleep(3000);
                aiTurn();
                updateComputerHand();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        });
        sleepThread.setDaemon(true);
        sleepThread.start();

        System.out.println();
        System.out.println(getAiChoice());
    }

    private void showWindow()
    {
        getRoot().setSpacing(10);
        getRoot().setMinSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        getRoot().setMaxSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        getRoot().getChildren().addAll(getProgressBox(), getEnemyBox(), getTableBox(), getButtonBox());
        getRoot().getStylesheets().add("file:resources/style.css");
        getRoot().prefWidthProperty().bind(stage.widthProperty());

        Group group = new Group(getRoot());
        group.setAutoSizeChildren(true);
        group.getStylesheets().add("file:resources/style.css");

        Scene scene = new Scene(group);
        scene.getStylesheets().add("file:resources/style.css");
        getStage().setScene(scene);
        getStage().setTitle("Schere Stein Papier");
        stage.setHeight(900);
        getStage().show();
    }

    private void handleButtonClick(ActionEvent event) throws InterruptedException
    {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();

        System.out.println(buttonId);

        switch (buttonId)
        {
            case Rock.id -> setUserChoice(getPlayStonesIDs().getOrDefault(0, null));
            case Paper.id -> setUserChoice(getPlayStonesIDs().getOrDefault(1, null));
            case Scissors.id -> setUserChoice(getPlayStonesIDs().getOrDefault(2, null));
            case Well.id -> setUserChoice(getPlayStonesIDs().getOrDefault(3, null));
        }

        Thread removeButtonBoxThread = new Thread(this::removeButtonBoxes);
        removeButtonBoxThread.setDaemon(true);
        removeButtonBoxThread.start();

        play();
    }

    private void updateComputerHand()
    {
        switch (getAiChoice())
        {
            case Rock.id -> getComputerHand().setImage(new Image("file:resources/masterHand_rock.png"));
            case Paper.id -> getComputerHand().setImage(new Image("file:resources/masterHand_paper.png"));
            case Scissors.id -> getComputerHand().setImage(new Image("file:resources/masterHand_scissors.png"));
            case Well.id -> getComputerHand().setImage(new Image("file:resources/masterHand_well.png"));
            default -> getComputerHand().setImage(new Image("file:resources/masterHand_default.png"));
        }
    }

    private void initializeUserElements()
    {
        getPlayStonesIDs().put(0, Rock.getId());
        getPlayStonesIDs().put(1, Paper.getId());
        getPlayStonesIDs().put(2, Scissors.getId());
        getPlayStonesIDs().put(3, Well.getId());

        setRockButton(initializeButton(getPlayStonesIDs().getOrDefault(0, null), new Image("file:resources/img/stein.png")));
        setPaperButton(initializeButton(getPlayStonesIDs().getOrDefault(1, null), new Image("file:resources/img/papier.png")));
        setScissorsButton(initializeButton(getPlayStonesIDs().getOrDefault(2, null), new Image("file:resources/img/schere.png")));
        setWellButton(initializeButton(getPlayStonesIDs().getOrDefault(3, null), new Image("file:resources/img/brunnen.png")));
        setEnemieProgressIndicator(initializeProgressIndicator());

        initializeButtonBox(10);
        addProgressIndicatorToBox(getProgressBox(), getEnemieProgressIndicator());

        setComputerHand(initializeImageView(new Image("file:resources/masterHand_default.png"), .1, .2));
        ImageView table = initializeImageView(new Image("file:resources/table.png"), 1, .5);

        addImageViewsToBoxes(getEnemyBox(), getComputerHand());
        addImageViewsToBoxes(getTableBox(), table);
    }

    private Button initializeButton(String id, Image image)
    {
        Button button = new Button();
        button.setMinSize(getMaxButtonWidth(), getMaxButtonHeight());
        button.setMaxSize(getMaxButtonWidth(), getMaxButtonHeight());
        button.setId(id);
//        button.setText(id);
        ImageView iv = new ImageView(image);
        iv.fitWidthProperty().bind(button.widthProperty());
        iv.fitHeightProperty().bind(button.heightProperty());
        button.setGraphic(iv);
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

    private ImageView initializeImageView(Image image, double widthFactor, double heightFactor)
    {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.fitWidthProperty().bind(getStage().widthProperty().multiply(widthFactor));
        imageView.fitHeightProperty().bind(getStage().heightProperty().multiply(heightFactor));

        return imageView;
    }

    private void initializeImageBox(HBox imageBox, ImageView imageView)
    {
        // Center the content in the HBox
        imageBox.setAlignment(Pos.CENTER);

        // Make HBox grow/shrink with window
        imageBox.prefWidthProperty().bind(getStage().widthProperty());
        imageBox.prefHeightProperty().bind(getStage().heightProperty());

        // Center the ImageView itself
        imageView.setPreserveRatio(true);

        // Make ImageView resize with window
        imageView.fitWidthProperty().bind(imageBox.widthProperty().multiply(0.8)); // 80% of box width
        imageView.fitHeightProperty().bind(imageBox.heightProperty().multiply(0.8)); // 80% of box height

        // Allow HBox to grow
        HBox.setHgrow(imageBox, Priority.ALWAYS);

        // Add ImageView to HBox if not already added
        if (!imageBox.getChildren().contains(imageView))
        {
            imageBox.getChildren().add(imageView);
        }
    }


    private void initializeButtonBox(int spacing)
    {
        getButtonBox().setSpacing(spacing); // Abstand zwischen Buttons
        getButtonBox().setAlignment(Pos.CENTER); // Inhalte zentrieren
        getButtonBox().setFillHeight(false); // Inhalte nicht in die Höhe ziehen
        getButtonBox().prefWidthProperty().bind(getStage().widthProperty()); // Breite der HBox anpassen

        // Buttons flexibel machen
        HBox.setHgrow(getRockButton(), Priority.ALWAYS);
        HBox.setHgrow(getPaperButton(), Priority.ALWAYS);
        HBox.setHgrow(getScissorsButton(), Priority.ALWAYS);
        HBox.setHgrow(getWellButton(), Priority.ALWAYS);

        // Buttons maximale Flexibilität geben
        getRockButton().setMaxWidth(Double.MAX_VALUE);
        getPaperButton().setMaxWidth(Double.MAX_VALUE);
        getScissorsButton().setMaxWidth(Double.MAX_VALUE);
        getWellButton().setMaxWidth(Double.MAX_VALUE);

        // Buttons zur HBox hinzufügen
        getButtonBox().getChildren().addAll(getRockButton(), getPaperButton(), getScissorsButton(), getWellButton());
    }


    private ProgressIndicator initializeProgressIndicator()
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setId("enemie_progress_indicator");
        progressIndicator.setMinSize(getMaxButtonWidth(), getMaxButtonHeight());

        return progressIndicator;
    }

    private void removeButtonBoxes()
    {
        Platform.runLater(() -> getButtonBox().setVisible(false));
    }

    private void aiTurn()
    {
        Random random = new Random();
        setAiChoice(random.nextInt(4));
    }

    private void addImageViewsToBoxes(HBox box, ImageView image)
    {
//        initializeImageBox(box, image);

        box.setAlignment(Pos.CENTER); // Zentriere ImageView
        box.setSpacing(20); // Optional: Abstand für mehrere Elemente
        box.prefWidthProperty().bind(getStage().widthProperty()); // Passe Breite an Fensterbreite an
        box.getChildren().add(image);
    }


    private void addProgressIndicatorToBox(HBox box, ProgressIndicator progressIndicator)
    {
        box.getChildren().add(progressIndicator);
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

    public HashMap<Integer, String> getPlayStonesIDs()
    {
        return playStonesIDs;
    }

    public void setPlayStonesIDs(HashMap<Integer, String> playStonesIDs)
    {
        this.playStonesIDs = playStonesIDs;
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

    public ImageView getComputerHand()
    {
        return computerHand;
    }

    public void setComputerHand(ImageView computerHand)
    {
        this.computerHand = computerHand;
    }
}
