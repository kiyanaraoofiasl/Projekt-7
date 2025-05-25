package controller;

import aircraft.Aircraft;
import aircraft.AircraftName;
import aircraft.AircraftType;
import aircraft.Message;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.stage.Stage;
import javafx.util.Duration;
import state.Globe;
import state.State;

import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ResourceBundle;

import static javafx.scene.input.KeyCode.*;

import java.io.*;

import javafx.concurrent.Task;

import javax.sound.sampled.*;
import javax.swing.*;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.scene.media.*;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;

import javax.swing.*;

public class AircraftController implements Initializable {


    private static JavaSoundRecorder javaSoundRecorder;
    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private MenuItem keyboardShortcutsMenuItem;

    @FXML
    private AnchorPane skyAnchorPane;

    @FXML
    private Label aircraftNameLabel;
    @FXML
    private Label aircraftControleModeLabel;

    @FXML
    private ScrollPane skyScrollPane;

    @FXML
    private ImageView horizonGroundSkyImageView;

    @FXML
    private ImageView turnCoordinatorAircraftImageView;

    @FXML
    private ImageView turnCoordinatorBallImageView;

    @FXML
    private ImageView speedNeedleImageView;

    @FXML
    private ImageView HeadingWealImageView;

    @FXML
    private ImageView altitudeIndicatorNeedleImageView;

    @FXML
    private Label altitudeIndicatorLabel;
    @FXML
    private ImageView background2;

    @FXML
    private ImageView background1;

    @FXML
    private ImageView edg1NeedleImageView;
    @FXML
    private ImageView edg2NeedleImageView;
    @FXML
    private ImageView n11NeedleImageView;
    @FXML
    private ImageView n12NeedleImageView;

    @FXML
    private Label edg1Label;
    @FXML
    private Label edg2Label;
    @FXML
    private Label n12Label;
    @FXML
    private Label n11Label;

    private Aircraft receiverAircraft;
    private Aircraft tankerAircraft;
    private Aircraft controlledAircraft;

    private String controlledAircraftId;

    private ParallelTransition parallelTransition;

    final int BACKGROUND_WIDTH = 2000;
    private AudioClip mediaPlayer;


    @FXML
    private VBox messagesVbox;

    @FXML
    private Button recordButton;
    private State messagesState;

    private LinkedList<Message> messages;

    public void setControlledAircraftId(String controlledAircraftId) {
        System.out.println(" setControlledAircraftId" + controlledAircraftId);
        this.controlledAircraftId = controlledAircraftId;
    }


    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            retrieveState();

            setUpBackgroundAnimation();

            skyScrollPane.requestFocus();
            skyScrollPane.setCursor(Cursor.NONE);

            bindAircraftToUI(receiverAircraft);
            bindAircraftToUI(tankerAircraft);

            bindAircraftToIndicators(controlledAircraft);

            skyScrollPane.setOnKeyPressed(event -> handleKeyPressed(event, controlledAircraft));
            skyScrollPane.requestFocus();

            aircraftNameLabel.setText(controlledAircraft.getAircraftName().toString().replace('_', ' '));
            aircraftControleModeLabel
                    .textProperty()
                    .bind(Bindings.when(controlledAircraft.onAutoControlProperty()).then("On autopilot").otherwise("On manual mode"));

        });
        playMusic();

        new Thread(() -> {
            System.out.println("Started Thread");
            while (true) {
                if (messages != null)
                    updateMessages();

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        aboutMenuItem.setOnAction(this::openAboutDialog);
        keyboardShortcutsMenuItem.setOnAction(this::openkeyboardShortcutsDialog);
        recordButton.setOnAction(event -> {
            if (recordButton.getText().equals("Record message")) {
                javaSoundRecorder = new JavaSoundRecorder();
                Thread thread = new Thread(javaSoundRecorder);
                thread.start();

                recordButton.setText("Stop");
            } else {
                String recordFilePath = javaSoundRecorder.finish();
                messages.add(new Message(recordFilePath, "Message from " + controlledAircraft.getAircraftName() + " at " + LocalTime.now()));
                javaSoundRecorder.cancel();
                recordButton.setText("Record message");
            }

        });
    }

    private void updateMessages() {
        System.out.println("messages : " + messages.size());
        Platform.runLater(() -> {
            messagesVbox.getChildren().clear();

            messages.forEach(message -> {

                final Button playbutton = new Button();
                playbutton.setText("Play message " + message.getTitle());
                playbutton.setOnAction(event1 -> {

                    String bip = message.getFileName();
                    Media hit = new Media(Paths.get(bip).toUri().toString());
                    mediaPlayer = new AudioClip(hit.getSource());
                    mediaPlayer.play();
                });
                messagesVbox.getChildren().add(playbutton);
            });
        });

    }


    private void playMusic() {
        String bip = "src/resources/sounds/aircraftSoundEffect.mp3";
        Media hit = new Media(Paths.get(bip).toUri().toString());
        mediaPlayer = new AudioClip(hit.getSource());
        mediaPlayer.setVolume(0.1);
        mediaPlayer.play();
    }


    private void retrieveState() {
        State aircraftState = Globe
                .getGlobe()
                .getContext("skyContext")
                .getState("aircraftState");

        tankerAircraft = (Aircraft) aircraftState
                .getItem("tankerAircraft");

        receiverAircraft = (Aircraft) aircraftState
                .getItem("receiverAircraft");

        messagesState = Globe
                .getGlobe()
                .getContext("messagesContext")
                .getState("messagesState");
        messages = (LinkedList<Message>) messagesState.getItem("messages");

        controlledAircraft = (Aircraft) aircraftState
                .getItem(controlledAircraftId);

    }

    private void bindAircraftToIndicators(Aircraft aircraft) {
        System.out.println(" bindAircraftToIndicators " + controlledAircraftId);

        HeadingWealImageView.rotateProperty().bind(aircraft.headingProperty());
        horizonGroundSkyImageView.rotateProperty().bind(aircraft.rollAngleProperty());
        turnCoordinatorBallImageView.rotateProperty().bind(aircraft.turnQualityProperty().multiply(-1));
        speedNeedleImageView.rotateProperty().bind(aircraft.speedProperty().multiply(-1).add(150));

        altitudeIndicatorLabel.textProperty().bind(aircraft.altitudeProperty().asString());

        altitudeIndicatorNeedleImageView.rotateProperty().bind(
                aircraft.altitudeProperty().multiply(-1).divide(28));


        edg1Label.textProperty().bind(aircraft.engine1TemperatureProperty().asString());
        edg1NeedleImageView.rotateProperty().bind(aircraft.engine1TemperatureProperty().divide(-2.1).add(-90));
        edg2Label.textProperty().bind(aircraft.engine1TemperatureProperty().asString());
        edg2NeedleImageView.rotateProperty().bind(aircraft.engine1TemperatureProperty().divide(-2.1).add(-90));

        n11Label.textProperty().bind(aircraft.engine1N1Property().asString());
        n11NeedleImageView.rotateProperty().bind(aircraft.engine1N1Property().multiply(-3.7).add(-10));
        n12Label.textProperty().bind(aircraft.engine2N1Property().asString());
        n12NeedleImageView.rotateProperty().bind(aircraft.engine2N1Property().multiply(-3.7).add(-10));


        turnCoordinatorAircraftImageView.rotateProperty().bind(aircraft.turnRateProperty());

        parallelTransition.rateProperty().bind(aircraft.speedProperty());
    }

    private void bindAircraftToUI(Aircraft aircraft) {
        ImageView aircraftImageView = new ImageView();
        aircraftImageView.setImage(new Image("resources/images/aircraft/" + aircraft.getAircraftName().toString().toLowerCase() + ".png"));
        aircraftImageView.xProperty()
                .bind(aircraft.positionXProperty()
                        .add(controlledAircraft.speedProperty()
                                .multiply(-1)
                                .add(aircraft.speedProperty())
                                .multiply(10)
                        ));
        aircraftImageView.yProperty().bind(aircraft.positionYProperty());
        aircraftImageView.rotateProperty().bind(aircraft.pitchAngleProperty().multiply(-1));

        if (!aircraft.getAircraftType().equals(AircraftType.RECEIVER)) {
            aircraftImageView.setFitWidth(200);
            aircraftImageView.setFitHeight(100);
        } else {
            aircraftImageView.setFitWidth(60);
            aircraftImageView.setFitHeight(30);
        }

        skyAnchorPane.getChildren().add(aircraftImageView);
    }

    private void setUpBackgroundAnimation() {
        TranslateTransition translateTransition;
        TranslateTransition translateTransition2;

        translateTransition = new TranslateTransition(Duration.millis(10000), background1);
        translateTransition2 = new TranslateTransition(Duration.millis(10000), background2);

        translateTransition.setFromX(0);
        translateTransition.setToX(-1 * BACKGROUND_WIDTH);
        translateTransition.setInterpolator(Interpolator.LINEAR);

        translateTransition2.setFromX(0);
        translateTransition2.setToX(-1 * BACKGROUND_WIDTH);
        translateTransition2.setInterpolator(Interpolator.LINEAR);

        parallelTransition =
                new ParallelTransition(translateTransition, translateTransition2);
        parallelTransition.setCycleCount(Animation.INDEFINITE);

        parallelTransition.play();
    }

    private void handleKeyPressed(KeyEvent event, Aircraft aircraft) {
        final KeyCode code = event.getCode();

        if (code == RIGHT) {
            aircraft.rollRight();
            aircraft.headingRight();
        } else if (code == LEFT) {
            aircraft.rollLeft();
            aircraft.headingLeft();
        } else if (code == PAGE_UP) aircraft.speedUp();
        else if (code == PAGE_DOWN) aircraft.speedDown();
        else if (code == UP) aircraft.pitchUp();
        else if (code == DOWN) aircraft.pitchDown();
        else if (code == Z) aircraft.goHigher();
        else if (code == S) aircraft.goLower();
        else if (code == D) aircraft.goForward();
        else if (code == Q) aircraft.goBackward();
        else if (code == A) aircraft.onAutoControlProperty().setValue(true);
        else if (code == M) aircraft.onAutoControlProperty().setValue(false);
        else if (code.toString().startsWith("F") & code.toString().length() == 2)
            aircraft.headToLevel(Integer.parseInt(code.toString().substring(1)));

        // TODO: 09/03/2020 reconsiders a more dynamic approach
        horizonGroundSkyImageView.setViewport(new Rectangle2D(0, 209 - aircraft.getPitchAngle() * 4, 244, 300));

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
                        "RIGHT : rollRight"+" \n" +
                        "LEFT : rollLeft"+" \n" +
                        "PAGE_UP : speedUp"+" \n" +
                        "PAGE_DOWN : speedDown"+" \n" +
                        "HOME : pitchUp"+" \n" +
                        "END : pitchDown"+" \n" +
                        "Z : Go Higher"+" \n" +
                        "S : Go Lower"+" \n" +
                        "D : Go Forward"+" \n" +
                        "Q : Go Backward"+" \n" +
                        "A : Auto ControlProperty ON"+" \n" +
                        "M : Auto ControlProperty OFF"+" \n" +
                        "F2 : Level 2 " + "\n" +
                        "F3 : Level 3 " + "\n" +
                        "F4 : Level 4 " + "\n" +
                        "F5 : Level 5 " + "\n" +
                        "F6 : Level 6 " + "\n" +
                        "F7 : Level 7 " + "\n");
        alert.showAndWait();
    }


    public static class JavaSoundRecorder extends Task<Void> {

        static final long RECORD_TIME = 60000;  // 1 minute

        private final String fileName = System.currentTimeMillis() + ".wav";

        private final File wavFile = new File(fileName);

        private final AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

        TargetDataLine line;

        @Override
        protected Void call() throws Exception {
            try {
                AudioFormat format = getAudioFormat();
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

                // checks if system supports the data line
                if (!AudioSystem.isLineSupported(info)) {
                    System.out.println("Line not supported");
                    System.exit(0);
                }
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();   // start capturing

                System.out.println("Start capturing...");

                AudioInputStream ais = new AudioInputStream(line);

                System.out.println("Start recording...");

                AudioSystem.write(ais, fileType, wavFile);

            } catch (LineUnavailableException | IOException ex) {
                ex.printStackTrace();
            }

            return null;
        }


        AudioFormat getAudioFormat() {
            float sampleRate = 16000;
            int sampleSizeInBits = 8;
            int channels = 2;
            boolean signed = true;
            boolean bigEndian = true;
            AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
                    channels, signed, bigEndian);
            return format;
        }

        String finish() {
            line.stop();
            line.close();
            System.out.println("Finished");
            return fileName;
        }

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        mediaPlayer.stop();
    }
}







