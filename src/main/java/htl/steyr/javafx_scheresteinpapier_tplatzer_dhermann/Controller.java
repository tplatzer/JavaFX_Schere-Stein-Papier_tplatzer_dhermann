package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class Controller
{
    /**
     * Rock
     * Paper
     * Scissors
     * Well
     */
    private static final int maxHBoxWidth = 1000;
    private static final int maxHBoxHeight = 700;
    private static final int maxButtonWidth = 200;
    private static final int maxButtonHeight = 100;
    private final HashMap<Integer, String> playStonesIDs = new HashMap<>();
    private final VBox root = new VBox();
    private final HBox gameEndBox = new HBox();
    private final HBox progressBox = new HBox();
    private final HBox enemyBox = new HBox();
    private final HBox tableBox = new HBox();
    private final HBox playerBox = new HBox();
    private String playerChoice = null;
    private String aiChoice = null;
    private String winner = null;
    private int playerWins = 0;
    private int aiWins = 0;
    private Rectangle rTop = new Rectangle(100, 100, Color.BLACK);
    private TranslateTransition topBarAnimation;
    private Rectangle rBot = new Rectangle(100, 100, Color.BLACK);
    private TranslateTransition bottomBarAnimation;
    private Stage stage;
    private Stage gameEndedPopupStage;
    private Button rockButton;
    private Button paperButton;
    private Button scissorsButton;
    private Button wellButton;
    private ProgressIndicator enemieProgressIndicator;
    private ImageView table;
    private ImageView computerHand;
    private ImageView playerHand;
    private VBox playerWinsCounterBox;
    private VBox aiWinsCounterBox;
    private MusicPlayer backgroundMusicPlayer;
    private MusicPlayer drumrollPlayer;

    public void start(Stage stage)
    {
        setDefaultValues();
        setStage(stage);
        initializeUserElements();

        playBackgroundMusic();
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
        getRoot().setMinSize(Controller.getMaxHBoxWidth(), Controller.getMaxHBoxHeight());
        getRoot().setMaxSize(Controller.getMaxHBoxWidth(), Controller.getMaxHBoxHeight());
        getRoot().prefWidthProperty().bind(getStage().widthProperty());
        getRoot().prefHeightProperty().bind(getStage().heightProperty()); // Binde Höhe an die Stage
        getRoot().getChildren().addAll(getProgressBox(), getEnemyBox(), getTableBox(), getPlayerBox());
        getRoot().getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        Scene scene = new Scene(getRoot());
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        getStage().getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/img/masterHand_default.png"))));
        getStage().setScene(scene);
        getStage().setTitle("Schere Stein Papier");
        getStage().setHeight(900);
        getStage().isFullScreen();
        getStage().show();
    }

    private void showGameEndScreen()
    {
        getGameEndedPopupStage().show();
    }

    private Stage initializeGameEndedPopup()
    {
        initializeGameEndBox();

        Stage stage = new Stage();
        stage.initOwner(getStage());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(getGameEndBox()));
        stage.setMinHeight(200);
        stage.setMinWidth(550);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> restartGame());
        
        return stage;
    }

    private void restartGame()
    {
        getGameEndedPopupStage().close();

        setDefaultValues();

        for (Node node : getPlayerBox().getChildren())
        {
            node.setVisible(true);
        }
        getPlayerHand().setVisible(false);

        getRoot().getChildren().remove(getGameEndBox());

        getComputerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/masterHand_default.png")).toExternalForm()));

        getEnemieProgressIndicator().setVisible(false);

        playBackgroundMusic();

        resetAnimation();
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

        Thread removeplayerBoxThread = new Thread(this::removePlayerBoxes);
        removeplayerBoxThread.setDaemon(true);
        removeplayerBoxThread.start();

        prepareAnimation();

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
            case 1 ->
            {
                setWinner("Player");
                new Thread(() -> getBackgroundMusicPlayer().playMusicShort((Objects.requireNonNull(getClass().getResource("/sound/player_win.wav")).toExternalForm().replace("file:/", "/")))).start();
            }
            case 2 ->
            {
                setWinner("AI");
                new Thread(() -> getBackgroundMusicPlayer().playMusicShort((Objects.requireNonNull(getClass().getResource("/sound/player_lose.wav")).toExternalForm().replace("file:/", "/")))).start();
            }
            case 0 -> setWinner("No Winner");
        }
        incrementWins();

        updateWinsCounterBoxes();
        updateGameEndText(getWinner() + " has won the Game!");

        showGameEndScreen();
    }

    private void updatePlayerHand()
    {
        switch (getPlayerChoice())
        {
            case Rock.id -> getPlayerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/player_rock.png")).toExternalForm()));
            case Paper.id -> getPlayerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/player_paper.png")).toExternalForm()));
            case Scissors.id -> getPlayerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/player_scissors.png")).toExternalForm()));
            case Well.id -> getPlayerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/player_well.png")).toExternalForm()));
            default -> getPlayerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/player_default.png")).toExternalForm()));
        }
    }

    private void updateComputerHand()
    {
        switch (getAiChoice())
        {
            case Rock.id -> getComputerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/masterHand_rock.png")).toExternalForm()));
            case Paper.id -> getComputerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/masterHand_paper.png")).toExternalForm()));
            case Scissors.id -> getComputerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/masterHand_scissors.png")).toExternalForm()));
            case Well.id -> getComputerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/masterHand_well.png")).toExternalForm()));
            default -> getComputerHand().setImage(new Image(Objects.requireNonNull(getClass().getResource("/img/masterHand_default.png")).toExternalForm()));
        }
    }

    private void updateWinsCounterBoxes()
    {
        updateWinsCounterBox(getPlayerWinsCounterBox(), getPlayerWins());
        updateWinsCounterBox(getAiWinsCounterBox(), getAiWins());
    }

    private void updateWinsCounterBox(VBox box, int wins)
    {
        if (!box.getChildren().isEmpty())
        {
            if (box.getChildren().getLast() instanceof Label winsCounterLabel)
            {
                winsCounterLabel.setText(String.valueOf(wins));
            }
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

    private void playBackgroundMusic()
    {
        getBackgroundMusicPlayer().playMusic((Objects.requireNonNull(getClass().getResource("/sound/background_music.wav")).toExternalForm().replace("file:/", "/")));
    }

    private void stopBackgroundMusic()
    {
        getBackgroundMusicPlayer().stopMusic();
    }

    private void playDrumroll()
    {
        getDrumrollPlayer().playMusicShort((Objects.requireNonNull(getClass().getResource("/sound/drum_roll.wav")).toExternalForm().replace("file:/", "/")));

    }

    private void initializeGameEndBox()
    {
        getGameEndBox().setSpacing(25);
        getGameEndBox().setAlignment(Pos.CENTER);
        getGameEndBox().getStyleClass().add("gameEndBox");

        Label winnerMessage = new Label(getWinner() + " has won the Game!");
        winnerMessage.setFocusTraversable(false);
        winnerMessage.getStyleClass().add("winnerMessage");

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(event -> Platform.exit());
        exitButton.setFocusTraversable(false);
        exitButton.getStyleClass().add("button");
        exitButton.getStyleClass().add("hidden-on-hover");

        Button playAgainButton = new Button("Play Again");
        playAgainButton.setOnAction(event -> restartGame());
        playAgainButton.setFocusTraversable(false);
        playAgainButton.getStyleClass().add("button");

        getGameEndBox().getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/gameEndBox.css")).toExternalForm());
        getPlayerWinsCounterBox().getStyleClass().add("winsCounterBox");
        getAiWinsCounterBox().getStyleClass().add("winsCounterBox");

        VBox buttonBox = new VBox(10); // 10 is the spacing between buttons
        buttonBox.setAlignment(Pos.CENTER); // Center the buttons
        buttonBox.getChildren().addAll(exitButton, playAgainButton);

        getGameEndBox().getChildren()
                .addAll(getPlayerWinsCounterBox(), winnerMessage, buttonBox, getAiWinsCounterBox());


    }

    private VBox initializeWinsCounterPane(Pos position, String user)
    {
        VBox box = new VBox();
        Label counterLabel = new Label();
        Label userLabel = new Label(user + " Wins");

        box.setMaxHeight(100);
        box.setMinHeight(100);
        box.setPrefHeight(100);
        box.setMaxWidth(100);
        box.setMinWidth(100);
        box.setPrefWidth(100);

        box.setAlignment(position);
        box.setSpacing(10);
        box.getStyleClass().add("box");
        box.setFocusTraversable(false);
        box.getChildren().addAll(userLabel, counterLabel);
        box.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        counterLabel.setFocusTraversable(false);
        userLabel.getStyleClass().add("userLabel");
        userLabel.setFocusTraversable(false);
        userLabel.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        return box;
    }

    private void initializeUserElements()
    {
        getPlayStonesIDs().put(0, Rock.getId());
        getPlayStonesIDs().put(1, Paper.getId());
        getPlayStonesIDs().put(2, Scissors.getId());
        getPlayStonesIDs().put(3, Well.getId());

        setBackgroundMusicPlayer(new MusicPlayer());
        setDrumrollPlayer(new MusicPlayer());

        setPlayerWinsCounterBox(initializeWinsCounterPane(Pos.TOP_RIGHT, "Player"));
        setAiWinsCounterBox(initializeWinsCounterPane(Pos.TOP_LEFT, "AI"));

        setGameEndedPopupStage(initializeGameEndedPopup());

        setRockButton(initializeButton(getPlayStonesIDs().getOrDefault(0, null)));
        setPaperButton(initializeButton(getPlayStonesIDs().getOrDefault(1, null)));
        setScissorsButton(initializeButton(getPlayStonesIDs().getOrDefault(2, null)));
        setWellButton(initializeButton(getPlayStonesIDs().getOrDefault(3, null)));
        setEnemieProgressIndicator(initializeProgressIndicator());

        initializePlayerBox();
        addProgressIndicatorToBox(getProgressBox(), getEnemieProgressIndicator());

        setComputerHand(initializeImageView(true, new Image(Objects.requireNonNull(getClass().getResource("/img/masterHand_default.png")).toExternalForm()), .2, .3));
        setTable(initializeImageView(true, new Image(Objects.requireNonNull(getClass().getResource("/img/table.png")).toExternalForm()), 1, .2));
        setPlayerHand(initializeImageView(false, null, .1, .2));

        getPlayerBox().getStyleClass().add("hbox");
        getEnemyBox().getStyleClass().add("hbox");
        getTableBox().getStyleClass().add("hbox");
        getProgressBox().getStyleClass().add("hbox");

        addImageViewsToBoxes(getEnemyBox(), getComputerHand());
        addImageViewsToBoxes(getTableBox(), getTable());
        getPlayerBox().getChildren().removeAll();
        getPlayerBox().getChildren().addAll(getRockButton(), getPaperButton(), getScissorsButton(), getWellButton());
        getPlayerHand().prefHeight(getRockButton().heightProperty().getValue());
        getPlayerHand().prefWidth(getRockButton().widthProperty().getValue());
    }

    private Button initializeButton(String id)
    {
        Button button = new Button();
        button.setMinSize(getMaxButtonWidth(), getMaxButtonHeight());
        button.setId(id);
        button.setFocusTraversable(false);
        button.setOnAction(event -> new Thread(() ->
        {
            try
            {
                handleButtonClick(event);
            } catch (InterruptedException e)
            {
                System.out.println(e.getMessage());
            }
        }).start());
        return button;
    }

    private ImageView initializeImageView(boolean visibility, Image image, double widthFactor, double heightFactor)
    {
        ImageView imageView = new ImageView();
        imageView.setImage(image);
        imageView.setVisible(visibility);
        imageView.setFocusTraversable(false);
        imageView.fitWidthProperty().bind(getStage().widthProperty().multiply(widthFactor));
        imageView.fitHeightProperty().bind(getStage().heightProperty().multiply(heightFactor));

        return imageView;
    }

    private void initializePlayerBox()
    {
        getPlayerBox().setFillHeight(false); // Inhalte nicht in die Höhe ziehen
        getPlayerBox().prefWidthProperty().bind(getStage().widthProperty()); // Breite der HBox an Fensterbreite binden
        getPlayerBox().setAlignment(Pos.CENTER); // Buttons in der Mitte zentrieren
        getPlayerBox().setSpacing(20); // Abstand zwischen Buttons

        // Button-Einstellungen
        Button[] buttons = {getRockButton(), getPaperButton(), getScissorsButton(), getWellButton()};
        for (Button button : buttons)
        {
            HBox.setHgrow(button, Priority.ALWAYS); // Button in HBox wachsen lassen
            button.prefWidthProperty().bind(getPlayerBox().widthProperty().divide(4).subtract(20)); // gleichmäßige Breite
            button.prefHeightProperty().bind(getPlayerBox().heightProperty().multiply(0.8)); // Höhe relativ zur HBox
            button.setFocusTraversable(false);
        }
    }

    private ProgressIndicator initializeProgressIndicator()
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setId("enemie_progress_indicator");
        progressIndicator.setMinSize(100, 50);
        progressIndicator.setFocusTraversable(false);
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
            getPlayerHand().prefWidth(getPlayerHand().getFitWidth() * 2);
            getPlayerHand().prefHeight(getPlayerHand().getFitHeight() * 2);
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
        box.setAlignment(Pos.CENTER); // Zentriere ImageView
        box.setSpacing(20); // Optional: Abstand für mehrere Elemente
        box.prefWidthProperty().bind(getStage().widthProperty()); // Passe Breite an Fensterbreite an
        box.getChildren().add(image);
    }

    private void addProgressIndicatorToBox(HBox box, ProgressIndicator progressIndicator)
    {
        box.getChildren().add(progressIndicator);
    }

    private void prepareAnimation()
    {
        // Schwarze Balken
        setrTop(new Rectangle(getRoot().getWidth(), 100, Color.BLACK));
        setrBot(new Rectangle(getRoot().getWidth(), 100, Color.BLACK));
        getrTop().setTranslateY(-100);  // Start außerhalb des Bildschirms
        getrBot().setTranslateY(100);   // Start außerhalb des Bildschirms

        stopBackgroundMusic();
        startAnimation();
    }

    private void startAnimation()
    {
        playDrumroll();
        Platform.runLater(() ->
        {
            getStage().setResizable(false);

            getRoot().getChildren().clear(); // use clear() instead of removeAll()
            getRoot().getChildren().addAll(getrTop(), getComputerHand(), getTableBox(), getPlayerHand(), getrBot());

            setTopBarAnimation(new TranslateTransition(Duration.seconds(3), getrTop()));
            setBottomBarAnimation(new TranslateTransition(Duration.seconds(3), getrBot()));

            if (getRoot().getHeight() < 900)
            {
                getTopBarAnimation().setToY(0);
                getBottomBarAnimation().setToY(0);
            } else
            {
                getTopBarAnimation().setToY((-1) * (getRoot().getHeight() / 50));
                getBottomBarAnimation().setToY((getRoot().getHeight() / 50));
            }

            TranslateTransition playerHandTranslateAnimation = new TranslateTransition(Duration.seconds(3), getPlayerHand());
            playerHandTranslateAnimation.setToX((-1) * (getRoot().getWidth() / 2.5));
            playerHandTranslateAnimation.setToY((-1) * (getRoot().getHeight() / 2.2));

            ParallelTransition parallelTransition = getParallelTransition(playerHandTranslateAnimation);
            parallelTransition.play();
        });
    }

    private ParallelTransition getParallelTransition(TranslateTransition playerHandTranslateAnimation)
    {
        TranslateTransition computerHandAnimation = new TranslateTransition(Duration.seconds(3), getComputerHand());
        computerHandAnimation.setToX((getRoot().getWidth() / 2.5));
        computerHandAnimation.setToY((getRoot().getHeight() / 13.5));

        TranslateTransition progressIndicatorAnimation = new TranslateTransition(Duration.seconds(3),
                getEnemieProgressIndicator());
        computerHandAnimation.setToX((getRoot().getWidth() / 2.5));
        computerHandAnimation.setToY((getRoot().getHeight() / 13.5));
//        progressIndicatorAnimation.setToY((-1)*(getRoot().getWidth() / 2.5));


        ScaleTransition playerHandScaleAnimation = new ScaleTransition(Duration.seconds(3), getPlayerHand());
        playerHandScaleAnimation.setFromX(1.0);
        playerHandScaleAnimation.setToX(2);
        playerHandScaleAnimation.setFromY(1);
        playerHandScaleAnimation.setFromY(2);

        ScaleTransition tableAnimation = new ScaleTransition(Duration.seconds(3), getTable());
        tableAnimation.setFromX(1.0);
        tableAnimation.setToX(.6);
        tableAnimation.setFromY(1);
        tableAnimation.setFromY(1.5);

        return new ParallelTransition(getTopBarAnimation(),
                getBottomBarAnimation(),
                playerHandTranslateAnimation,
                playerHandScaleAnimation,
                computerHandAnimation,
                tableAnimation,
                progressIndicatorAnimation);
    }

    private void resetAnimation()
    {
        Platform.runLater(() ->
        {
            // Animiert die Balken zurück außerhalb des Bildschirms
            TranslateTransition resetTopBarAnimation = new TranslateTransition(Duration.seconds(3), getrTop());
            resetTopBarAnimation.setToY(-100);

            TranslateTransition resetBottomBarAnimation = new TranslateTransition(Duration.seconds(3), getrBot());
            resetBottomBarAnimation.setToY(100);

            // Spielerhand zurück zur Mitte
            TranslateTransition resetPlayerHandAnimation = new TranslateTransition(Duration.seconds(3),
                    getPlayerHand());
            resetPlayerHandAnimation.setToX(0);
            resetPlayerHandAnimation.setToY(0);

            // Skalierung der Spielerhand zurück auf Standard
            ParallelTransition resetParallelTransition = getParallelTransition(resetTopBarAnimation, resetBottomBarAnimation, resetPlayerHandAnimation);

            // Nach der Animation die Root-Elemente auf Standard zurücksetzen
            resetParallelTransition.setOnFinished(event ->
            {
                getEnemyBox().getChildren().add(getComputerHand());
                getRoot().getChildren().clear();
                getRoot().getChildren()
                        .addAll(getProgressBox(), getEnemyBox(), getTableBox(), getPlayerBox());
            });

            getStage().setResizable(true);
        });
    }

    private ParallelTransition getParallelTransition(TranslateTransition resetTopBarAnimation, TranslateTransition resetBottomBarAnimation, TranslateTransition resetPlayerHandAnimation)
    {
        ScaleTransition resetPlayerHandScaleAnimation = new ScaleTransition(Duration.seconds(3), getPlayerHand());
        resetPlayerHandScaleAnimation.setToX(1.0);
        resetPlayerHandScaleAnimation.setToY(1.0);

        // Gegnerhand zurück zur Mitte
        TranslateTransition resetComputerHandAnimation = new TranslateTransition(Duration.seconds(3),
                getComputerHand());
        resetComputerHandAnimation.setToX(0);
        resetComputerHandAnimation.setToY(0);

        // Tisch zurück auf Standardgröße
        ScaleTransition resetTableAnimation = new ScaleTransition(Duration.seconds(3), getTable());
        resetTableAnimation.setToX(1.0);
        resetTableAnimation.setToY(1.0);

        // Fortschrittsanzeige zurück zur Mitte und unsichtbar
        TranslateTransition resetProgressIndicatorAnimation = new TranslateTransition(Duration.seconds(3),
                getEnemieProgressIndicator());
        resetProgressIndicatorAnimation.setToX(0);
        resetProgressIndicatorAnimation.setToY(0);

        // ParallelTransition erstellt eine Animation, die alle Rücksetz-Animationen zusammenführt
        ParallelTransition resetParallelTransition = new ParallelTransition(resetTopBarAnimation,
                resetBottomBarAnimation,
                resetPlayerHandAnimation,
                resetPlayerHandScaleAnimation,
                resetComputerHandAnimation,
                resetTableAnimation,
                resetProgressIndicatorAnimation);

        // Spielt die Animation ab
        resetParallelTransition.play();
        return resetParallelTransition;
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

    public Stage getStage()
    {
        return this.stage;
    }

    public void setStage(Stage stage)
    {
        this.stage = stage;
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

    public static int getMaxHBoxWidth()
    {
        return maxHBoxWidth;
    }

    public static int getMaxHBoxHeight()
    {
        return maxHBoxHeight;
    }

    public static int getMaxButtonWidth()
    {
        return maxButtonWidth;
    }

    public static int getMaxButtonHeight()
    {
        return maxButtonHeight;
    }

    public HBox getProgressBox()
    {
        return progressBox;
    }

    public HBox getEnemyBox()
    {
        return enemyBox;
    }

    public HBox getTableBox()
    {
        return tableBox;
    }

    public HBox getPlayerBox()
    {
        return playerBox;
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

    public VBox getPlayerWinsCounterBox()
    {
        return playerWinsCounterBox;
    }

    public void setPlayerWinsCounterBox(VBox playerWinsPane)
    {
        this.playerWinsCounterBox = playerWinsPane;
    }

    public VBox getAiWinsCounterBox()
    {
        return aiWinsCounterBox;
    }

    public void setAiWinsCounterBox(VBox aiWinsCounterPane)
    {
        this.aiWinsCounterBox = aiWinsCounterPane;
    }

    public Stage getGameEndedPopupStage()
    {
        return gameEndedPopupStage;
    }

    public void setGameEndedPopupStage(Stage gameEndedPopupStage)
    {
        this.gameEndedPopupStage = gameEndedPopupStage;
    }

    public Rectangle getrTop()
    {
        return rTop;
    }

    public void setrTop(Rectangle rTop)
    {
        this.rTop = rTop;
    }

    public Rectangle getrBot()
    {
        return rBot;
    }

    public void setrBot(Rectangle rBot)
    {
        this.rBot = rBot;
    }

    public TranslateTransition getTopBarAnimation()
    {
        return topBarAnimation;
    }

    public void setTopBarAnimation(TranslateTransition topBarAnimation)
    {
        this.topBarAnimation = topBarAnimation;
    }

    public TranslateTransition getBottomBarAnimation()
    {
        return bottomBarAnimation;
    }

    public void setBottomBarAnimation(TranslateTransition bottomBarAnimation)
    {
        this.bottomBarAnimation = bottomBarAnimation;
    }

    public MusicPlayer getBackgroundMusicPlayer()
    {
        return backgroundMusicPlayer;
    }

    public void setBackgroundMusicPlayer(MusicPlayer backgroundMusicPlayer)
    {
        this.backgroundMusicPlayer = backgroundMusicPlayer;
    }

    public MusicPlayer getDrumrollPlayer()
    {
        return drumrollPlayer;
    }

    public void setDrumrollPlayer(MusicPlayer drumrollPlayer)
    {
        this.drumrollPlayer = drumrollPlayer;
    }
}
