package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.application.Application;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class App extends Application implements Initializable
{
    HBox root = new HBox();
    public Button rockButton = Controller.initializeButton(Rock.getId());
    ;
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
        enemieProgressIndicator = new ProgressIndicator();
        enemieProgressIndicator.setProgress(0.5);
        enemieProgressIndicator.setPrefSize(400, 100);

        root.setSpacing(10);
        root.setMinSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        root.setMaxSize(Controller.getMaxHboxWidth(), Controller.getMaxHboxHeight());
        root.getChildren().addAll(rockButton, paperButton, scissorsButton, springButton, enemieProgressIndicator);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Schere Stein Papier");
        stage.show();
    }

    public static void main(String[] args)
    {
        launch();
    }
}
