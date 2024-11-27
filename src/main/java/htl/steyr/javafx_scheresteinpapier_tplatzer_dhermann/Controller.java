package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;

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
    private static String userInput = null;

    public static Button initializeButton(String id)
    {
        Button button = new Button();
        button.setMinSize(Controller.getMaxButtonWidth(), Controller.getMaxButtonHeight());
        button.setMaxSize(Controller.getMaxButtonWidth(), Controller.getMaxButtonHeight());
        button.setId(id);
        button.setOnAction(Controller::handleButtonClick);
        return button;
    }

    public static ProgressIndicator initializeProgressIndicator()
    {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setId("enemie_progress_indicator");
        progressIndicator.setMinSize(maxButtonWidth, maxButtonHeight);

        return progressIndicator;
    }

    public static void handleButtonClick(ActionEvent event) {
        Button sourceButton = (Button) event.getSource();
        String buttonId = sourceButton.getId();

        switch (buttonId)
        {
            case Rock.id -> setUserInput(Rock.getId());
            case Paper.id -> setUserInput(Paper.getId());
            case Scissors.id -> setUserInput(Scissors.getId());
            case Well.id -> setUserInput(Well.getId());
        }
    }

    public static int getMaxButtonWidth(){return maxButtonWidth;}
    public static int getMaxButtonHeight(){return maxButtonHeight;}
    public static int getMaxHboxWidth(){return maxHboxWidth;}
    public static int getMaxHboxHeight() {return maxHboxHeight;}

    public static int getMaxProgressIndicatorWidth() {return maxProgressIndicatorWidth;}
    public static int getMaxProgressIndicatorHeight() {return maxProgressIndicatorHeight;}
    public static String getUserInput() {return userInput;}
    public static void setUserInput(String userInput) {Controller.userInput = userInput;}
}
