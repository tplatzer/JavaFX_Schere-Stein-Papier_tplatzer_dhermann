package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    /*private static final int maxButtonWidth = 100;
    private static final int maxButtonHeight = 100;*/
    private static final int maxHboxWidth = 1000;
    private static final int maxHboxHeight = 700;
    private static final int maxProgressIndicatorWidth = 200;
    private static final int maxProgressIndicatorHeight = 100;

    private String playerChoice = null;
    private String aiChoice = null;
    private String winner = null;
    private int playerWins = 0;
    private int aiWins = 0;
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
    private VBox winsCounterBox = new VBox();
    private HBox playerWinsCounterBox;
    private HBox aiWinsCounterBox;

    private HBox progressBox = new HBox();
    private HBox enemyBox = new HBox();
    private HBox tableBox = new HBox();
    private HBox playerBox = new HBox();

    public static int getMaxHboxWidth() {
        return maxHboxWidth;
    }

    public static int getMaxHboxHeight() {
        return maxHboxHeight;
    }

    public static int getMaxProgressIndicatorWidth() {
        return maxProgressIndicatorWidth;
    }

    public static int getMaxProgressIndicatorHeight() {
        return maxProgressIndicatorHeight;
    }

    public void start(Stage stage, Controller controller) {
        setDefaultValues();
        setStage(stage);
        setController(controller);
        initializeUserElements();

        showWindow();
    }

    private void play() {
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
                System.out.println(e.getMessage());
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
        getRoot().setAlignment(Pos.CENTER); // Zentriere alle Kinder
        getRoot().setMinSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        getRoot().setMaxSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        getRoot().prefWidthProperty().bind(getStage().widthProperty());
        getRoot().prefHeightProperty().bind(getStage().heightProperty()); // Binde Höhe an die Stage
        getRoot().getChildren().addAll(getProgressBox(), getEnemyBox(), getTableBox(), getPlayerBox(), getWinsCounterBox());
        getRoot().getStylesheets().add("file:resources/style.css");

        Scene scene = new Scene(getRoot());
        scene.getStylesheets().add("file:resources/style.css");
        getStage().setScene(scene);
        getStage().setTitle("Schere Stein Papier");
        getStage().setHeight(900);
        getStage().show();
    }

    private void adjustElements() {
        ReadOnlyDoubleProperty stageWidthProperty = getStage().widthProperty();
        ReadOnlyDoubleProperty stageHeightProperty = getStage().heightProperty();

        for (Node node : getPlayerBox().getChildren()) {
            if (node instanceof Button button) {
                button.prefWidthProperty().bind(stageWidthProperty);
                button.prefHeightProperty().bind(stageHeightProperty);
            }
        }

    }

    private void showGameEndScreen() {
        initializeGameEndBox();
        getWinsCounterBox().setVisible(true);

        Platform.runLater(() -> getRoot().getChildren().add(getGameEndBox()));
    }

    private void restartGame()
    {
        setDefaultValues();

        for (Node node : getPlayerBox().getChildren())
        {
            if (node == getPlayerHand()) continue;
            node.setVisible(true);
        }
        getPlayerHand().setVisible(false);

        getWinsCounterBox().setVisible(false);

        getRoot().getChildren().remove(getGameEndBox());

        getComputerHand().setImage(new Image("file:resources/masterHand_default.png"));

        getEnemieProgressIndicator().setVisible(false);
    }

    private void handleButtonClick(ActionEvent event) throws InterruptedException {
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

        Thread removeplayerBoxThread = new Thread(this::removePlayerBoxes);
        removeplayerBoxThread.setDaemon(true);
        removeplayerBoxThread.start();

        play();
    }

    /**
     * 0 = Unentschieden
     * 1 = Player
     * 2 = AI
     */
    private int evaluateWinner() {
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

    private void selectWinner() {
        switch (evaluateWinner())
        {
            case 1 -> setWinner("Player");
            case 2 -> setWinner("AI");
            case 0 -> setWinner("No Winner");
        }
        incrementWins();

        updateWinsCounterBoxes();
        updateGameEndText(getWinner() + " has won the Game!");

        showGameEndScreen();
    }

    private void updatePlayerHand() {
        switch (getPlayerChoice())
        {
            case Rock.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_rock.png"));
            case Paper.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_paper.png"));
            case Scissors.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_scissors.png"));
            case Well.id -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_well.png"));
            default -> getPlayerHand().setImage(new Image("file:resources/img/hovered/player_default.png"));
        }
    }

    private void updateComputerHand() {
        switch (getAiChoice())
        {
            case Rock.id -> getComputerHand().setImage(new Image("file:resources/masterHand_rock.png"));
            case Paper.id -> getComputerHand().setImage(new Image("file:resources/masterHand_paper.png"));
            case Scissors.id -> getComputerHand().setImage(new Image("file:resources/masterHand_scissors.png"));
            case Well.id -> getComputerHand().setImage(new Image("file:resources/masterHand_well.png"));
            default -> getComputerHand().setImage(new Image("file:resources/masterHand_default.png"));
        }
    }

    private void updateWinsCounterBoxes()
    {
        updateWinsCounterBox(getPlayerWinsCounterBox(), getPlayerWins());
        updateWinsCounterBox(getAiWinsCounterBox(), getAiWins());
    }

    private void updateWinsCounterBox(HBox box, int wins)
    {
        if (!box.getChildren().isEmpty())
        {
            if (box.getChildren().getLast() instanceof Label winsCounterLabel)
            {
                winsCounterLabel.setText(String.valueOf(wins));
            }
        }
    }

    private void setDefaultValues() {
        setPlayerChoice(null);
        setAiChoice(-1);
        setWinner(null);
    }

    private void updateGameEndText(String newWinnerMessage) {
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

    private void incrementWins()
    {
        if (getWinner().equals("Player"))
        {
            setPlayerWins(getPlayerWins() + 1);
        } else if (getWinner().equals("AI"))
        {
            setAiWins(getAiWins() + 1);
        }
    }

    private void initializeGameEndBox() {
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

    private HBox initializeWinsCounterPane(Pos position, String user)
    {
        HBox box = new HBox();
        Label counterLabel = new Label();
        Label userLabel = new Label(user);

        box.setAlignment(position);
        box.setSpacing(10);
        box.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8); -fx-padding: 10;");
        box.getChildren().addAll(counterLabel, userLabel);
        box.setVisible(false);

        getWinsCounterBox().getChildren().add(box);

        return box;
    }

    private void initializeUserElements() {
        getPlayStonesIDs().put(0, Rock.getId());
        getPlayStonesIDs().put(1, Paper.getId());
        getPlayStonesIDs().put(2, Scissors.getId());
        getPlayStonesIDs().put(3, Well.getId());

        setPlayerWinsCounterBox(initializeWinsCounterPane(Pos.TOP_RIGHT, "Player"));
        setAiWinsCounterBox(initializeWinsCounterPane(Pos.TOP_LEFT, "AI"));

        setRockButton(initializeButton(getPlayStonesIDs().getOrDefault(0, null), new Image("file:resources/img/stone.png")));
        setPaperButton(initializeButton(getPlayStonesIDs().getOrDefault(1, null), new Image("file:resources/img/paper.png")));
        setScissorsButton(initializeButton(getPlayStonesIDs().getOrDefault(2, null), new Image("file:resources/img/scissors.png")));
        setWellButton(initializeButton(getPlayStonesIDs().getOrDefault(3, null), new Image("file:resources/img/player_well.png")));
        setEnemieProgressIndicator(initializeProgressIndicator());

        initializePlayerBox();
        addProgressIndicatorToBox(getProgressBox(), getEnemieProgressIndicator());

        setComputerHand(initializeImageView(true, new Image("file:resources/masterHand_default.png"), .1, .2));
        setTable(initializeImageView(true, new Image("file:resources/table.png"), 1, .3));
        setPlayerHand(initializeImageView(false, new Image("file:resources/player_default.png"), .1, .2));

        getPlayerBox().getStyleClass().add("hbox");
        getEnemyBox().getStyleClass().add("hbox");
        getTableBox().getStyleClass().add("hbox");
        getProgressBox().getStyleClass().add("hbox");

        addImageViewsToBoxes(getEnemyBox(), getComputerHand());
        addImageViewsToBoxes(getTableBox(), getTable());
    }

    private Button initializeButton(String id, Image image) {
        Button button = new Button();
        button.setPrefHeight(getMaxHboxHeight());
        button.setId(id);
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

    private ImageView initializeImageView(boolean visibility, Image image, double widthFactor, double heightFactor) {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setVisible(visibility);
        imageView.fitWidthProperty().bind(getStage().widthProperty().multiply(widthFactor));
        imageView.fitHeightProperty().bind(getStage().heightProperty().multiply(heightFactor));

        return imageView;
    }

    private void initializeImageBox(HBox imageBox, ImageView imageView)
    {
        imageBox.setAlignment(Pos.CENTER); // Inhalte zentrieren
        imageBox.prefWidthProperty().bind(getStage().widthProperty());
        imageBox.prefHeightProperty().bind(getStage().heightProperty().multiply(0.3)); // Höhe dynamisch anpassen

        imageView.setPreserveRatio(true); // Bildverhältnis beibehalten
        imageView.fitWidthProperty().bind(imageBox.widthProperty().multiply(0.8));
        imageView.fitHeightProperty().bind(imageBox.heightProperty().multiply(0.8));

        if (!imageBox.getChildren().contains(imageView))
        {
            imageBox.getChildren().add(imageView);
        }
    }

    private void initializePlayerBox() {
        getPlayerBox().setFillHeight(false); // Inhalte nicht in die Höhe ziehen
        getPlayerBox().prefWidthProperty().bind(getStage().widthProperty()); // Breite der HBox an Fensterbreite binden
        getPlayerBox().setAlignment(Pos.CENTER); // Buttons in der Mitte zentrieren
        getPlayerBox().setSpacing(20); // Abstand zwischen Buttons

        // Button-Einstellungen
        Button[] buttons = {getRockButton(), getPaperButton(), getScissorsButton(), getWellButton()};
        for (Button button : buttons) {
            HBox.setHgrow(button, Priority.ALWAYS); // Button in HBox wachsen lassen
            button.prefWidthProperty().bind(getPlayerBox().widthProperty().divide(4).subtract(20)); // gleichmäßige Breite
            button.prefHeightProperty().bind(getPlayerBox().heightProperty().multiply(0.8)); // Höhe relativ zur HBox
        }
    }

    private ProgressIndicator initializeProgressIndicator() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setId("enemie_progress_indicator");
//        progressIndicator.setMinSize(getMaxButtonWidth(), getMaxButtonHeight());
        progressIndicator.setVisible(false);

        return progressIndicator;
    }

    private void removePlayerBoxes()
    {
        Platform.runLater(() ->
        {
            for (Node node : getPlayerBox().getChildren())
            {
                if (node == getPlayerHand()) continue;
                node.setVisible(false);
            }
            getPlayerHand().setVisible(true);
        });
    }

    private void aiTurn() {
        Random random = new Random();
        setAiChoice(random.nextInt(4));
    }

    private void addImageViewsToBoxes(HBox box, ImageView image) {
//        initializeImageBox(box, image);

        box.setAlignment(Pos.CENTER); // Zentriere ImageView
        box.setSpacing(20); // Optional: Abstand für mehrere Elemente
        box.prefWidthProperty().bind(getStage().widthProperty()); // Passe Breite an Fensterbreite an
        box.getChildren().add(image);
    }

    private void addProgressIndicatorToBox(HBox box, ProgressIndicator progressIndicator) {
        box.getChildren().add(progressIndicator);
    }

    public String getPlayerChoice() {
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

    public void setAiChoice(int aiChoice) {
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

    public int getPlayerWins()
    {
        return playerWins;
    }

    public void setPlayerWins(int playerWins)
    {
        this.playerWins = playerWins;
    }

    public int getAiWins()
    {
        return aiWins;
    }

    public void setAiWins(int aiWins)
    {
        this.aiWins = aiWins;
    }

    public HBox getPlayerWinsCounterBox()
    {
        return playerWinsCounterBox;
    }

    public void setPlayerWinsCounterBox(HBox playerWinsPane)
    {
        this.playerWinsCounterBox = playerWinsPane;
    }

    public HBox getAiWinsCounterBox()
    {
        return aiWinsCounterBox;
    }

    public void setAiWinsCounterBox(HBox aiWinsCounterPane)
    {
        this.aiWinsCounterBox = aiWinsCounterPane;
    }

    public VBox getWinsCounterBox()
    {
        return winsCounterBox;
    }

    public void setWinsCounterBox(VBox winsCounterBox)
    {
        this.winsCounterBox = winsCounterBox;
    }
}
