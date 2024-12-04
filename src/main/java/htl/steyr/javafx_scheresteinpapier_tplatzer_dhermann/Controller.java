package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Random;

public class Controller
{
    /**
     * Rock
     * Paper
     * Scissors
     * Well
     */
    private static final int maxButtonWidth = 200;
    private static final int maxButtonHeight = 50;
    private static final int maxHboxWidth = 1000;
    private static final int maxHboxHeight = 700;
    private static final int maxProgressIndicatorWidth = 200;
    private static final int maxProgressIndicatorHeight = 100;

    private String playerChoice = null;
    private String aiChoice = null;
    private String winner = null;
    private HashMap<Integer, String> playStonesIDs = new HashMap<>();
    private Controller controller;
    private VBox root = new VBox();
    private HBox gameEndBox = new HBox();
    private Stage stage;
    private Button rockButton;
    private Button paperButton;
    private Button scissorsButton;
    private Button wellButton;
    private ProgressIndicator enemieProgressIndicator;
    private ImageView table;
    private ImageView computerHand;
    private ImageView playerHand;

    private HBox progressBox = new HBox();
    private HBox enemyBox = new HBox();
    private HBox tableBox = new HBox();
    private HBox playerBox = new HBox();

    public void start(Stage stage, Controller controller)
    {
        setDefaultValues();
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
                getEnemieProgressIndicator().setVisible(false);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            } finally
            {
                Platform.runLater(this::selectWinner);
            }
        });
        sleepThread.setDaemon(true);
        sleepThread.start();
    }

    private void showWindow()
    {
        getRoot().setSpacing(10);
        getRoot().setMinSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        getRoot().setMaxSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        getRoot().getChildren().addAll(getProgressBox(), getEnemyBox(), getTableBox(), getPlayerBox());
        getRoot().getStylesheets().add("file:resources/style.css");
        getRoot().prefWidthProperty().bind(getStage().widthProperty());

        Group group = new Group(getRoot());
        group.setAutoSizeChildren(true);
        group.getStylesheets().add("file:resources/style.css");

        Scene scene = new Scene(group);
        scene.getStylesheets().add("file:resources/style.css");
        getStage().setScene(scene);
        getStage().setTitle("Schere Stein Papier");
        getStage().setHeight(900);
        getStage().show();
    }

    private void showGameEndScreen()
    {
        initializeGameEndBox();

        Platform.runLater(() -> getRoot().getChildren().add(getGameEndBox()));
    }


    private void restartGame()
    {
        setDefaultValues();

        getPlayerBox().setVisible(true);

        getRoot().getChildren().remove(getGameEndBox());

        getComputerHand().setImage(new Image("file:resources/masterHand_default.png"));

        getEnemieProgressIndicator().setVisible(false);
    }

    private void handleButtonClick(ActionEvent event) throws InterruptedException
    {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();

        switch (buttonId)
        {
            case Rock.id -> setPlayerChoice(getPlayStonesIDs().getOrDefault(0, null));
            case Paper.id -> setPlayerChoice(getPlayStonesIDs().getOrDefault(1, null));
            case Scissors.id -> setPlayerChoice(getPlayStonesIDs().getOrDefault(2, null));
            case Well.id -> setPlayerChoice(getPlayStonesIDs().getOrDefault(3, null));
        }

        updatePlayerHand();

        getEnemieProgressIndicator().setVisible(true);

        Thread removeButtonBoxThread = new Thread(this::removeButtonBoxes);
        removeButtonBoxThread.setDaemon(true);
        removeButtonBoxThread.start();

        play();
    }

    /**
     * 0 = Unentschieden
     * 1 = Player
     * 2 = AI
     */
    private int evaluateWinner()
    {
        if (getPlayerChoice().equals(getAiChoice())) return 0;
        if (getPlayerChoice().equals(Scissors.id))
        {
            switch (getAiChoice())
            {
                case Paper.id ->
                {
                    return 1;
                }
                case Rock.id, Well.id ->
                {
                    return 2;
                }
            }
        }
        if (getPlayerChoice().equals(Rock.id))
        {
            switch (getAiChoice())
            {
                case Scissors.id ->
                {
                    return 1;
                }
                case Paper.id, Well.id ->
                {
                    return 2;
                }
            }
        }
        if (getPlayerChoice().equals(Paper.id))
        {
            switch (getAiChoice())
            {
                case Rock.id, Well.id ->
                {
                    return 1;
                }
                case Scissors.id ->
                {
                    return 2;
                }
            }
        }
        if (getPlayerChoice().equals(Well.id))
        {
            switch (getAiChoice())
            {
                case Scissors.id, Rock.id ->
                {
                    return 1;
                }
                case Paper.id ->
                {
                    return 2;
                }
            }
        }

        return 0;
    }

    private void selectWinner()
    {
        switch (evaluateWinner())
        {
            case 1 -> setWinner("Player");
            case 2 -> setWinner("AI");
            case 0 -> setWinner("No Winner");
        }

        updateGameEndText(getWinner() + " has won the Game!");

        showGameEndScreen();
    }

    private void updatePlayerHand()
    {
        switch (getPlayerChoice())
        {
            case Rock.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_rock.png"));
            case Paper.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_paper.png"));
            case Scissors.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_scissors.png"));
            case Well.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_well.png"));
            default -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_default.png"));
        }
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

    private void setDefaultValues()
    {
        setPlayerChoice(null);
        setAiChoice(-1);
        setWinner(null);
    }

    private void updateGameEndText(String newWinnerMessage)
    {
        if (!getGameEndBox().getChildren().isEmpty())
        {
            for (Node node : getGameEndBox().getChildren())
            {
                if (node instanceof Label winnerMessageLabel)
                {
                    winnerMessageLabel.setText(newWinnerMessage);
                    break;
                }
            }
        }
    }

    private void initializeGameEndBox()
    {
        if (!getGameEndBox().getChildren().isEmpty())
        {
            return;
        }

        getGameEndBox().setSpacing(10);
        getGameEndBox().setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 20;");
        getGameEndBox().setAlignment(Pos.CENTER);

        Label winnerMessage = new Label(getWinner() + " has won the Game!");
        winnerMessage.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> Platform.exit());

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setOnAction(event -> restartGame());

        getGameEndBox().getChildren().addAll(winnerMessage, exitButton, playAgainButton);
    }

    private void initializeUserElements()
    {
        getPlayStonesIDs().put(0, Rock.getId());
        getPlayStonesIDs().put(1, Paper.getId());
        getPlayStonesIDs().put(2, Scissors.getId());
        getPlayStonesIDs().put(3, Well.getId());

        setRockButton(initializeButton(getPlayStonesIDs().getOrDefault(0, null), new Image("file:resources/img/stone.png")));
        setPaperButton(initializeButton(getPlayStonesIDs().getOrDefault(1, null), new Image("file:resources/img/paper.png")));
        setScissorsButton(initializeButton(getPlayStonesIDs().getOrDefault(2, null), new Image("file:resources/img/scissors.png")));
        setWellButton(initializeButton(getPlayStonesIDs().getOrDefault(3, null), new Image("file:resources/img/player_well.png")));
        setEnemieProgressIndicator(initializeProgressIndicator());

        initializePlayerBox(10);
        addProgressIndicatorToBox(getProgressBox(), getEnemieProgressIndicator());

        setComputerHand(initializeImageView(true, new Image("file:resources/masterHand_default.png"), .1, .2));
        setTable(initializeImageView(true, new Image("file:resources/table.png"), 1, .5));
        setPlayerHand(initializeImageView(false, new Image("file:resources/player_default.png"), .1, .2));

        addImageViewsToBoxes(getEnemyBox(), getComputerHand());
        addImageViewsToBoxes(getTableBox(), getTable());
        addImageViewsToBoxes(getPlayerBox(), getPlayerHand());
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
            new Thread(() ->
            {
                try
                {
                    handleButtonClick(event);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }).start();
        });
        return button;
    }

    private ImageView initializeImageView(boolean visibility, Image image, double widthFactor, double heightFactor)
    {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setVisible(visibility);
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


    private void initializePlayerBox(int spacing)
    {
        getPlayerBox().setSpacing(spacing); // Abstand zwischen Buttons
        getPlayerBox().setAlignment(Pos.CENTER); // Inhalte zentrieren
        getPlayerBox().setFillHeight(false); // Inhalte nicht in die Höhe ziehen
        getPlayerBox().prefWidthProperty().bind(getStage().widthProperty()); // Breite der HBox anpassen

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
        getPlayerBox().getChildren().addAll(getRockButton(), getPaperButton(), getScissorsButton(), getWellButton());
    }

    private ProgressIndicator initializeProgressIndicator()
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setId("enemie_progress_indicator");
        progressIndicator.setMinSize(getMaxButtonWidth(), getMaxButtonHeight());
        progressIndicator.setVisible(false);

        return progressIndicator;
    }

    private void removeButtonBoxes()
    {
        Platform.runLater(() ->
        {
            for (Node node : getPlayerBox().getChildren())
            {
                node.setVisible(false);
            }
            getPlayerHand().setVisible(true);
        });
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

    public String getPlayerChoice()
    {
        return playerChoice;
    }

    public void setPlayerChoice(String playerChoice)
    {
        this.playerChoice = playerChoice;
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
            case -1 -> this.aiChoice = null;
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

    public HBox getPlayerBox()
    {
        return playerBox;
    }

    public void setPlayerBox(HBox playerBox)
    {
        this.playerBox = playerBox;
    }

    public ImageView getComputerHand()
    {
        return computerHand;
    }

    public void setComputerHand(ImageView computerHand)
    {
        this.computerHand = computerHand;
    }

    public HBox getGameEndBox()
    {
        return gameEndBox;
    }

    public void setGameEndBox(HBox gameEndBox)
    {
        this.gameEndBox = gameEndBox;
    }

    public String getWinner()
    {
        return winner;
    }

    public void setWinner(String winner)
    {
        this.winner = winner;
    }

    public ImageView getTable()
    {
        return table;
    }

    public void setTable(ImageView table)
    {
        this.table = table;
    }

    public ImageView getPlayerHand()
    {
        return playerHand;
    }

    public void setPlayerHand(ImageView playerHand)
    {
        this.playerHand = playerHand;
    }
}
