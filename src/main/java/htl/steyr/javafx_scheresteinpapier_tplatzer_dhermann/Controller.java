package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Controller
{
    /**
     * Rock
     * Paper
     * Sissores
     * Spring
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

    public void initializeUserElements()
    {
        playStonesIDs.put(0, Rock.getId());
        playStonesIDs.put(1, Paper.getId());
        playStonesIDs.put(2, Scissors.getId());
        playStonesIDs.put(3, Well.getId());

        setRockButton(initializeButton(playStonesIDs.getOrDefault(0, null)));
        setPaperButton(initializeButton(playStonesIDs.getOrDefault(1, null)));
        setScissorsButton(initializeButton(playStonesIDs.getOrDefault(2, null)));
        setWellButton(initializeButton(playStonesIDs.getOrDefault(3, null)));
        setEnemieProgressIndicator(initializeProgressIndicator());
    }

    public void start(Stage stage, Controller controller)
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
        getRoot().getChildren().addAll(rockButton, paperButton, scissorsButton, wellButton, enemieProgressIndicator);

        Scene scene = new Scene(getRoot());
        getStage().setScene(scene);
        getStage().setTitle("Schere Stein Papier");
        getStage().show();
    }

    public Button initializeButton(String id)
    {
        Button button = new Button();
        button.setMinSize(getMaxButtonWidth(), getMaxButtonHeight());
        button.setMaxSize(getMaxButtonWidth(), getMaxButtonHeight());
        button.setId(id);
        button.setText(id);
        button.setOnAction(this::handleButtonClick);
        return button;
    }

    public ProgressIndicator initializeProgressIndicator()
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setId("enemie_progress_indicator");
        progressIndicator.setMinSize(maxButtonWidth, maxButtonHeight);

        return progressIndicator;
    }

    public void handleButtonClick(ActionEvent event)
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

    private void play()
    {
        aiTurn();
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
    public void setEnemieProgressIndicator(ProgressIndicator enemieProgressIndicator) {
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
            case 0 -> this.aiChoice = playStonesIDs.getOrDefault(0, null);
            case 1 -> this.aiChoice = playStonesIDs.getOrDefault(1, null);
            case 2 -> this.aiChoice = playStonesIDs.getOrDefault(2, null);
            case 3 -> this.aiChoice = playStonesIDs.getOrDefault(3, null);
        }

    }
}
