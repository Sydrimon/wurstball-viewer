package wurstball;

import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import wurstball.data.PictureElement;
import wurstball.data.WurstballData;
import static javafx.application.Application.launch;

/**
 *
 * @author Sydrimon
 */
public class Wurstball extends Application {

    private static final Logger LOGGER = Logger.getLogger(Wurstball.class.getName());

    public static final int MAX_RETRIES = 4;

    /**
     * the address of the site to get the picture from
     */
    public static final String ADDRESS = "http://wurstball.de/random/";

    /**
     * the tag of the picture on the website
     */
    public static final String PIC_TAG = "div[id=content-main] > img";

    public static final int SCREEN_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    public static final int SCREEN_HEIGHT = Toolkit.getDefaultToolkit().getScreenSize().height;

    public static PictureElement currentPic;
    public static Clipboard clipboard = Clipboard.getSystemClipboard();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("Wurstball-Viewer 2.0");
        primaryStage.setMaximized(true);

        final WurstballData wData = WurstballData.getInstance();
        currentPic = wData.getNextPic();

        // ImageView
        final ImageView imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);

        // StackPane
        Pane stackP = new StackPane(imageView);
        stackP.setStyle("-fx-background: rgb(0,0,0);");

        // ScrollPane
        ScrollPane scrollP = new ScrollPane(stackP);
        scrollP.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollP.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollP.setStyle("-fx-background: rgb(0,0,0);");

        // Scene
        Scene scene = new Scene(scrollP);
        scene.setFill(Color.BLACK);
        primaryStage.setScene(scene);

        // set sizes
        stackP.setMinSize(scene.getWidth() - 20, scene.getHeight() - 20);
        scrollP.setMaxSize(scene.getWidth(), scene.getHeight());
        scrollP.setPrefSize(scene.getWidth(), scene.getHeight());
        scrollP.setPrefViewportWidth(scene.getWidth() - 20);
        scrollP.setPrefViewportHeight(scene.getHeight() - 20);

        changePic(imageView, currentPic.getImage(), scene.getWidth() - 20, scene.getHeight() - 20);

        scene.setOnKeyTyped((KeyEvent event) -> {
            switch (event.getCharacter()) {
                case "r": // next random picture
                    currentPic = wData.getNextPic();
                    changePic(imageView, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
                    break;
                case "+": // zoom in
                    if (imageView.getFitWidth() < imageView.getImage().getWidth() * 3) {
                        imageView.setFitHeight(0);
                        imageView.setFitWidth(imageView.getFitWidth() + 50);
                    }
                    break;
                case "-": // zoom out
                    if (imageView.getFitWidth() > 200) {
                        imageView.setFitHeight(0);
                        imageView.setFitWidth(imageView.getFitWidth() - 50);
                    }
                    break;
                case "m": //resets the size of the picture
                    changePic(imageView, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
                    break;
                default:
                    break;
            }
        });

        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.isControlDown()) {
                PictureElement newPictureElement;
                switch (event.getCode()) {
                    case S: // save picture as a file
                        LOGGER.log(Level.INFO, "Calling savePic");
                        currentPic.savePic();
                        break;
                    case LEFT: // show previous picture
                        newPictureElement = wData.getPreviousPic(true);
                        if (newPictureElement != null) {
                            currentPic = newPictureElement;
                            changePic(imageView, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
                        }
                        break;
                    case RIGHT: // show next picture
                        newPictureElement = wData.getPreviousPic(false);
                        if (newPictureElement != null) {
                            currentPic = newPictureElement;
                            changePic(imageView, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
                        }
                        break;
                    case C: // copy image url to clipboard
                        LOGGER.info("Copy url to clipboard");
                        ClipboardContent content = new ClipboardContent();
                        content.putString(currentPic.getImageURL());
                        clipboard.setContent(content);
                        break;
                    case F: // switch to full screen mode
                        if (!primaryStage.isFullScreen()) {
                            LOGGER.info("Entering full screen mode");
                            primaryStage.setFullScreen(true);
                        } else {
                            LOGGER.info("Leaving full screen mode");
                            primaryStage.setFullScreen(false);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                stackP.setMinSize(scene.getWidth() - 20, scene.getHeight() - 20);
                scrollP.setMaxSize(scene.getWidth(), scene.getHeight());
                scrollP.setPrefSize(scene.getWidth(), scene.getHeight());
                scrollP.setPrefViewportWidth(scene.getWidth() - 20);
                scrollP.setPrefViewportHeight(scene.getHeight() - 20);

                changePic(imageView, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
            }
        });

        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                stackP.setMinSize(scene.getWidth() - 20, scene.getHeight() - 20);
                scrollP.setMaxSize(scene.getWidth(), scene.getHeight());
                scrollP.setPrefSize(scene.getWidth(), scene.getHeight());
                scrollP.setPrefViewportWidth(scene.getWidth() - 20);
                scrollP.setPrefViewportHeight(scene.getHeight() - 20);

                changePic(imageView, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
            }
        });

        // show Stage
        primaryStage.show();
        scrollP.requestFocus();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Changes the Image of the ImageView and sets the size to fit the screen
     *
     * @param view
     * @param image
     * @param width
     * @param height
     */
    public static void changePic(ImageView view, Image image, double width, double height) {
        view.setImage(image);
        view.setFitHeight(0);
        if (image.getWidth() > width) {
            view.setFitWidth(width);
        } else if (image.getHeight() > height && image.getHeight() < height + 150) {
            view.setFitWidth(0);
            view.setFitHeight(height);
        } else {
            view.setFitWidth(image.getWidth());
        }
    }
}
