package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;

public class App extends Application
{
    public Button rockButton;
    public Button paperButton;
    public Button scissorsButton;
    public Button springButton;
    public ProgressIndicator kiProgressIndicator;

    @Override
    public void start(Stage stage)
    {

    }

    public static void main(String[] args)
    {
        launch();
    }
}
