package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class App extends Application implements Initializable
{
    Controller controller = new Controller();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
    }

    @Override
    public void start(Stage stage)
    {
        controller.start(stage, controller);
    }

    public static void main(String[] args)
    {
        launch();
    }
}
