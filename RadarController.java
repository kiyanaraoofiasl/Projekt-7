package controller;

import aircraft.Aircraft;
import aircraft.AircraftType;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import state.Globe;
import state.State;

import java.net.URL;
import java.util.Collections;
import java.util.ResourceBundle;

public class RadarController implements Initializable {

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem keyboardShortcutsMenuItem;

    @FXML
    private AnchorPane skyAnchorPane;

    private Aircraft tankerAircraft;

    private Aircraft receiverAircraft;

    private static SimpleIntegerProperty timeProperty;


    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        aboutMenuItem.setOnAction(this::openAboutDialog);
        keyboardShortcutsMenuItem.setOnAction(this::openkeyboardShortcutsDialog);

        skyAnchorPane.setBackground(
                new Background(
                        Collections.singletonList(new BackgroundFill(
                                Color.WHITE,
                                new CornerRadii(0),
                                new Insets(10))),
                        Collections.singletonList(new BackgroundImage(
                                new Image("resources/images/backgrounds/map.png",1100 , 800, false, true),
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundRepeat.NO_REPEAT,
                                BackgroundPosition.DEFAULT,
                                BackgroundSize.DEFAULT))));


        State aircraftState = Globe
                .getGlobe()
                .getContext("skyContext")
                .getState("aircraftState");

        tankerAircraft = (Aircraft) aircraftState
                .getItem("tankerAircraft");

        receiverAircraft = (Aircraft) aircraftState
                .getItem("receiverAircraft");

        timeProperty = (SimpleIntegerProperty) Globe
                .getGlobe()
                .getContext("timeContext")
                .getState("currentTimeState").getItem("currentTime");


        bindAircraftToUI(receiverAircraft);
        bindAircraftToUI(tankerAircraft);
    }


    private void bindAircraftToUI(Aircraft aircraft) {

        VBox aircraftVBox = new VBox();
        Label aircraftNameLabel = new Label();
        Label aircraftAltitudeLabel = new Label();

        aircraftNameLabel.setText(aircraft.getAircraftName().toString());
        aircraftNameLabel.setTextAlignment(TextAlignment.CENTER);

        aircraftAltitudeLabel.textProperty().bind(aircraft.altitudeProperty().asString("%s M"));
        aircraftAltitudeLabel.setTextAlignment(TextAlignment.CENTER);

        ImageView aircraftImageView = new ImageView();
        aircraftImageView.setImage(new Image("resources/images/aircraft/aircraft_icon.png"));
        aircraftImageView.setFitHeight(50);
        aircraftImageView.setFitWidth(50);
        aircraftImageView.rotateProperty().bind(aircraft.headingProperty());

        aircraftVBox.setSpacing(5);
        if (aircraft.getAircraftType().equals(AircraftType.RECEIVER)) {

            aircraftVBox.getChildren().add(aircraftNameLabel);
            aircraftVBox.getChildren().add(aircraftAltitudeLabel);
            aircraftVBox.getChildren().add(aircraftImageView);
        } else {
            aircraftVBox.setPadding(new Insets(43,0,0,0));
            aircraftVBox.getChildren().add(aircraftImageView);
            aircraftVBox.getChildren().add(aircraftAltitudeLabel);
            aircraftVBox.getChildren().add(aircraftNameLabel);
        }

        aircraftVBox.translateXProperty()
                .bind(aircraft.positionXProperty().multiply(0.3).add(timeProperty).multiply(0.5));

        aircraftVBox.translateYProperty()
                .bind(aircraft.positionZProperty().multiply(0.2).add(350));

        skyAnchorPane.getChildren().add(aircraftVBox);
    }

    private void openAboutDialog(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("RTS Final Project ");
        alert.setHeaderText("Real time system project");
        alert.setContentText(
                "Made by : Akram Aznakour"
                        + "\n\n" +
                        "Professor :  Dr.-Ing. F. KHARROUBI ");
        alert.showAndWait();
    }

    private void openkeyboardShortcutsDialog(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("RTS Final Project ");
        alert.setHeaderText("Real time system project");
        alert.setContentText(
                "F1 : Level 1 " + "\n" +
                        "F2 : Level 2 " + "\n" +
                        "F3 : Level 3 " + "\n" +
                        "F4 : Level 4 " + "\n" +
                        "F5 : Level 5 " + "\n" +
                        "F6 : Level 6 " + "\n" +
                        "F7 : Level 7 " + "\n");
        alert.showAndWait();
    }
}







