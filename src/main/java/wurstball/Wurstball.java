package wurstball;

import java.awt.Toolkit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
import javafx.stage.WindowEvent;
import wurstball.data.PictureElement;
import wurstball.data.WurstballData;

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

    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
    public static final ImageView IMAGE_VIEW = new ImageView();

    public static PictureElement currentPic;
    public static Clipboard clipboard ;
    private ScheduledFuture<?> future;

    @Override
    public void start(Stage primaryStage) {
        clipboard = Clipboard.getSystemClipboard();

        primaryStage.setTitle("Wurstball-Viewer 2.0");
        primaryStage.setMaximized(true);
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            ImageLoader.EXECUTOR.shutdownNow();
        });
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setFullScreen(true);
        
        final WurstballData wData = WurstballData.getInstance();
        currentPic = wData.getNextPic();

        // ImageView
        IMAGE_VIEW.setPreserveRatio(true);
        IMAGE_VIEW.setSmooth(true);
        IMAGE_VIEW.setCache(true);

        // StackPane
        Pane stackP = new StackPane(IMAGE_VIEW);
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

        changePic(IMAGE_VIEW, currentPic.getImage(), scene.getWidth() - 20, scene.getHeight() - 20);

        scene.setOnKeyTyped((KeyEvent event) -> {
            switch (event.getCharacter().toLowerCase()) {
                case "r":
                    randomPicture();
                    break;
                case "+":
                    rescaleUp();
                    break;
                case "-":
                    rescaleDown();
                    break;
                case "m": //resets the size of the picture
                    changePic(currentPic.getImage());
                    break;
                case " ":
                    togglePresentationMode();
                    break;
                default:
                    break;
            }
        });

        scene.setOnKeyPressed((KeyEvent event) -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case S:
                        savePictureToFile();
                        break;
                    case LEFT:
                        showPreviousPicture();
                        break;
                    case RIGHT:
                        showNextPicture();
                        break;
                    case C:
                        copyPictureUrl();
                        break;
                    case F:
                        toggleFullscreen(primaryStage);
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

                changePic(IMAGE_VIEW, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
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

                changePic(IMAGE_VIEW, currentPic.getImage(), stackP.getWidth(), stackP.getHeight());
            }
        });

        // show Stage
        primaryStage.show();
        scrollP.requestFocus();
    }

    /**
     * shows the next reandom picture
     */
    private void randomPicture() {
        currentPic = WurstballData.getInstance().getNextPic();
        changePic(currentPic.getImage());
        pausePresentation();
    }

    /**
     * rescales the current picture up
     */
    private void rescaleUp() {
        pausePresentation();
        if (IMAGE_VIEW.getFitWidth() < IMAGE_VIEW.getImage().getWidth() * 3) {
            if (IMAGE_VIEW.getFitWidth() == 0) {
                IMAGE_VIEW.setFitWidth(currentPic.getImage().getWidth());
            }
            IMAGE_VIEW.setFitHeight(0);
            IMAGE_VIEW.setFitWidth(IMAGE_VIEW.getFitWidth() + 50);
        }
    }

    /**
     * rescales the current picture down
     */
    private void rescaleDown() {
        pausePresentation();
        if (IMAGE_VIEW.getFitWidth() > 200) {
            if (IMAGE_VIEW.getFitWidth() == 0) {
                IMAGE_VIEW.setFitWidth(currentPic.getImage().getWidth());
            }
            IMAGE_VIEW.setFitHeight(0);
            IMAGE_VIEW.setFitWidth(IMAGE_VIEW.getFitWidth() - 50);
        }
    }

    /**
     * toggles presentation mode
     */
    private void togglePresentationMode() {
        if (future != null && !future.isCancelled()) {
            pausePresentation();
        } else {
            future = EXECUTOR.scheduleAtFixedRate(() -> {
                Wurstball.changePic(WurstballData.getInstance().getNextPic().getImage());
            }, 0, 2, TimeUnit.SECONDS);
        }
    }

    /**
     * toggles fullscreen mode
     * @param primaryStage 
     */
    private void toggleFullscreen(Stage primaryStage) {
        primaryStage.setFullScreen(!primaryStage.isFullScreen());
    }

    /**
     * save current picture to file
     */
    private void savePictureToFile() {
        // save picture as a file
        pausePresentation();
        LOGGER.log(Level.INFO, "Calling savePic");
        currentPic.savePic();
    }

    /**
     * copys the URL of the current picture to the clipboard
     */
    private void copyPictureUrl() {
        LOGGER.info("Copy url to clipboard");
        ClipboardContent content = new ClipboardContent();
        content.putString(currentPic.getImageURL());
        clipboard.setContent(content);
    }

    /**
     * sets the next picture of the previous viewed pictures as current picture
     */
    private void showNextPicture() {
        PictureElement newPictureElement;
        newPictureElement = WurstballData.getInstance().getPreviousPic(false);
        if (newPictureElement != null) {
            currentPic = newPictureElement;
            changePic(currentPic.getImage());
        }
    }

    /**
     * sets the previous picture of the previous viewed pictures as current
     * picture
     */
    private void showPreviousPicture() {
        PictureElement newPictureElement;
        newPictureElement = WurstballData.getInstance().getPreviousPic(true);
        if (newPictureElement != null) {
            pausePresentation();
            currentPic = newPictureElement;
            changePic(currentPic.getImage());
        }
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
        view.setFitWidth(0);
        if (image.getWidth() > width) {
            view.setFitWidth(width);
        }
        if (image.getHeight() > height) {
            view.setFitHeight(height);
        }
    }

    /**
     * Changes the Image of the ImageView and sets the size to fit the screen
     *
     * @param image
     */
    public static void changePic(Image image) {
        changePic(IMAGE_VIEW, image, (SCREEN_WIDTH -SCREEN_WIDTH * 0.01), (SCREEN_HEIGHT - SCREEN_HEIGHT* 0.01));
    }

    /**
     * pauses the presentatiom mode
     */
    private void pausePresentation() {
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
        }
    }
}
