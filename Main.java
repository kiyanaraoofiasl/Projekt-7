import aircraft.Message;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import state.Context;
import state.Globe;
import state.State;

import java.io.IOException;
import java.util.LinkedList;

public class Main extends Application {

    private static final SimpleIntegerProperty timeProperty = new SimpleIntegerProperty();


    @Override
    public void start(Stage primaryStage) throws Exception {
        openSplashScreen(primaryStage);
    }

    private void openSplashScreen(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("view/splash-view.fxml"));
        stage.setTitle("Splash");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        stage.getIcons().add(new Image("./resources/images/aircraft/airplane.png"));

    }


    public static void main(String[] args) {
        setUpContexts();
        new Thread(Main::incrementTime).start();
        launch(args);
    }

    private static void setUpContexts() {

        Context timeContext = new Context();
        State currentTimeState = new State();

        timeContext.putState("currentTimeState", currentTimeState);
        currentTimeState.putItem("currentTime", timeProperty);
        Globe.getGlobe().putContext("timeContext", timeContext);




        Context messagesContext = new Context();
        State messagesState = new State();

        LinkedList<Message> messages = new LinkedList<Message>();

        messagesContext.putState("messagesState", messagesState);
        messagesState.putItem("messages", messages);
        Globe.getGlobe().putContext("messagesContext", messagesContext);
    }

    private synchronized static void incrementTime() {
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeProperty.setValue(timeProperty.getValue() + 1);
        }
    }

}






