package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class App extends Application implements Initializable
{
    VBox root = new VBox();
    HBox progressBox = new HBox();
    HBox enemyBox = new HBox();
    HBox tableBox = new HBox();
    HBox buttonBox = new HBox();


    public Button rockButton = Controller.initializeButton(Rock.getId());
    public Button paperButton = Controller.initializeButton(Paper.getId());
    public Button scissorsButton = Controller.initializeButton(Scissors.getId());
    public Button springButton = Controller.initializeButton(Well.getId());
    public ProgressIndicator enemieProgressIndicator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        Controller.setUserInput(null);
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
