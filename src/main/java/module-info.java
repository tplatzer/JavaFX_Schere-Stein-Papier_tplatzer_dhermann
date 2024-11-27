module htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires java.desktop;

    opens htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann to javafx.fxml;
    exports htl.steyr.javafx_scheresteinpapier_tplatzer_dhermann;
}