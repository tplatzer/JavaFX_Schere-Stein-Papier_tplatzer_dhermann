package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application
{
    @Override
    public void start(Stage stage)
    {
        new Controller().start(stage);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
