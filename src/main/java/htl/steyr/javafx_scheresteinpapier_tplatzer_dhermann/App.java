package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileNotFoundException;

public class App extends Application
{
    Controller controller = new Controller();

    @Override
    public void start(Stage stage)
    {
        controller.start(stage);
    }

    public static void main(String[] args)
    {
        launch();
    }
}
