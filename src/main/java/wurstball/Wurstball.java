package wurstball;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Sydrimon
 */
public class Wurstball extends Application {

    private static final Logger LOGGER = Logger.getLogger(Wurstball.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = FXMLLoader.load(getClass().getResource("/wurstballviewer.fxml"));

        // Set Scene
        Scene scene = new Scene(root);
        scene.setFill(Color.BLACK);
        scene.getStylesheets().add("/style.css");
        stage.setScene(scene);

        initPrimaryStage(stage);
    }

    private void initPrimaryStage(Stage stage) {
        stage.setTitle("Wurstball-Viewer 2.0");
        stage.setMaximized(true);
        stage.setOnCloseRequest((WindowEvent event) -> {
            ThreadController.shutdown();
        });
        stage.setMinHeight(600);
        stage.setMinWidth(800);
        stage.setFullScreen(true);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
