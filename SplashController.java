package controller;

import aircraft.Aircraft;
import aircraft.AircraftName;
import aircraft.AircraftType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import state.Context;
import state.Globe;
import state.State;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class SplashController implements Initializable {


    @FXML
    private ImageView receiverImageView;
    @FXML
    private ImageView tankerImageView;
    @FXML
    private ComboBox<AircraftModel> tankerComboBox;
    @FXML
    private ComboBox<AircraftModel> receiverComboBox;

    @FXML
    private Button startButton;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        receiverComboBox.valueProperty().addListener((observable, oldValue, newValue) -> receiverImageView.setImage(new Image("./resources/images/aircraft/" + newValue.aircraftName.toString().toLowerCase() + ".png")));
        tankerComboBox.valueProperty().addListener((observable, oldValue, newValue) -> tankerImageView.setImage(new Image("./resources/images/aircraft/" + tankerComboBox.getValue().aircraftName.toString().toLowerCase() + ".png")));
        receiverComboBox.getSelectionModel().selectFirst();
        tankerComboBox.getSelectionModel().selectFirst();


        ObservableList<AircraftModel> aircraftModels = FXCollections.observableArrayList();
        aircraftModels.add(new AircraftModel(AircraftName.Boeing_KC_135, AircraftType.TANKER_FLYING_BOOM));
        aircraftModels.add(new AircraftModel(AircraftName.A220, AircraftType.TANKER_FLYING_BOOM));
        aircraftModels.add(new AircraftModel(AircraftName.F16, AircraftType.RECEIVER));


        receiverComboBox.getItems().addAll(aircraftModels.filtered(aircraftModel -> aircraftModel.aircraftType == AircraftType.RECEIVER));
        tankerComboBox.getItems().addAll(aircraftModels.filtered(aircraftModel -> aircraftModel.aircraftType == AircraftType.TANKER_FLYING_BOOM));
        startButton.setOnMouseClicked(this::handle);

        aboutMenuItem.setOnAction(this::openAboutDialog);
    }

    private void handle(MouseEvent event) {
        AircraftModel receiverComboBoxValue = receiverComboBox.getValue();
        AircraftModel tankerComboBoxValue = tankerComboBox.getValue();

        setUpContexts(receiverComboBoxValue, tankerComboBoxValue);
        try {
            openATCRadar();
            openAircraftWindow("tankerAircraft");
            openAircraftWindow("receiverAircraft");
            ((Stage) startButton.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class AircraftModel {
        public AircraftModel(AircraftName aircraftName, AircraftType aircraftType) {
            this.aircraftName = aircraftName;
            this.aircraftType = aircraftType;
        }

        public AircraftName aircraftName;
        public AircraftType aircraftType;

        @Override
        public String toString() {
            return aircraftName.toString();
        }
    }

    private static void setUpContexts(AircraftModel receiverComboBoxValue, AircraftModel tankerComboBoxValue) {

        Aircraft tankerAircraft = new Aircraft
                .Builder()
                .withAircraftName(tankerComboBoxValue.aircraftName)
                .withAircraftType(tankerComboBoxValue.aircraftType)
                .build();

        Aircraft receiverAircraft = new Aircraft
                .Builder()
                .withAircraftName(receiverComboBoxValue.aircraftName)
                .withAircraftType(receiverComboBoxValue.aircraftType)
                .build();

        Context skyContext = new Context();
        State aircraftState = new State();
        aircraftState.putItem("tankerAircraft", tankerAircraft);
        aircraftState.putItem("receiverAircraft", receiverAircraft);
        skyContext.putState("aircraftState", aircraftState);
        Globe.getGlobe().putContext("skyContext", skyContext);
    }


    private void openATCRadar() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../view/radar-view.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Application");
        Scene scene2 = new Scene(root);
        stage.setScene(scene2);
        stage.setMaximized(true);
        stage.getIcons().add(new Image("./resources/images/aircraft/airplane.png"));
        stage.show();
    }

    private void openAircraftWindow(String aircraftId) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/aircraft-view.fxml"));
        Parent root = (Parent) loader.load();
        AircraftController secController = loader.getController();
        secController.setControlledAircraftId(aircraftId);
        Stage stage = new Stage();
        stage.setMaximized(true);
        stage.setScene(new Scene(root));
        stage.getIcons().add(new Image("./resources/images/aircraft/airplane.png"));
        stage.show();
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

}







