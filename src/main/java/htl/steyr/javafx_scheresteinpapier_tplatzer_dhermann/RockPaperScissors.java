package htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class RockPaperScissors extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Haupt-Layout
        Pane root = new Pane();
        root.setStyle("-fx-background-color: white;");
        Scene scene = new Scene(root, 1920, 1080);

        // Schwarze Balken
        Rectangle topBar = new Rectangle(800, 100, Color.BLACK);
        Rectangle bottomBar = new Rectangle(800, 100, Color.BLACK);
        topBar.setTranslateY(-100); // Start außerhalb des Bildschirms
        bottomBar.setTranslateY(600); // Start außerhalb des Bildschirms

        // Hände
        Text playerHand = new Text("✋");
        Text computerHand = new Text("✋");
        playerHand.setStyle("-fx-font-size: 50px;");
        computerHand.setStyle("-fx-font-size: 50px;");
        playerHand.setTranslateX(350); // Startposition Mitte
        playerHand.setTranslateY(200);
        computerHand.setTranslateX(450);
        computerHand.setTranslateY(200);

        root.getChildren().addAll(topBar, bottomBar, playerHand, computerHand);

        // Animation für die Balken
        TranslateTransition topBarAnimation = new TranslateTransition(Duration.seconds(1), topBar);
        topBarAnimation.setToY(0); // Bewege den oberen Balken in den sichtbaren Bereich

        TranslateTransition bottomBarAnimation = new TranslateTransition(Duration.seconds(1), bottomBar);
        bottomBarAnimation.setToY(500); // Bewege den unteren Balken in den sichtbaren Bereich

        // Animation für die Hände
        TranslateTransition playerHandAnimation = new TranslateTransition(Duration.seconds(1), playerHand);
        playerHandAnimation.setToX(50); // Bewege in die linke Ecke

        TranslateTransition computerHandAnimation = new TranslateTransition(Duration.seconds(1), computerHand);
        computerHandAnimation.setToX(650); // Bewege in die rechte Ecke

        // Starte die Animationen
        topBarAnimation.play();
        bottomBarAnimation.play();
        playerHandAnimation.play();
        computerHandAnimation.play();

        // Fenster anzeigen
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rock Paper Scissors");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
