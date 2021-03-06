package wurstball.ui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import wurstball.ThreadController;
import wurstball.Wurstball;
import wurstball.data.PictureElement;
import wurstball.data.WurstballData;

/**
 * Created by franziskah on 11.10.16.
 */
public class WurstballViewerController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(Wurstball.class.getName());
    private static final int OUTER_PADDING = 20;

    @FXML
    private ImageView imagecontainer;
    @FXML
    private ScrollPane maincontainer;
    private DoubleProperty currentImageZoom;
    private final WurstballData wurstballData = WurstballData.getInstance();

    private static PictureElement currentPic;
    private static Clipboard clipboard;
    private static Future future;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clipboard = Clipboard.getSystemClipboard();
        currentPic = wurstballData.getPicFromBuffer();
        currentImageZoom = new SimpleDoubleProperty(1);
        imagecontainer.setPreserveRatio(true);

        currentImageZoom.addListener(new ZoomChangeListener());

        setCurrentImage();
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        KeyCode pressed = event.getCode();
        switch (pressed) {
            case R:
                pausePresentation();
                randomPicture();
                break;
            case M:
                pausePresentation();
                resetImageScale();
                break;
            case SPACE:
                togglePresentationMode();
                break;
            case S:
                pausePresentation();
                savePictureToFile();
                break;
            case LEFT:
                pausePresentation();
                showPreviousPicture();
                break;
            case RIGHT:
                pausePresentation();
                showNextPicture();
                break;
            case C:
                copyPictureUrl();
                break;
            case F:
                toggleFullscreen();
                break;
            case PLUS:
                pausePresentation();
                rescaleUp();
                break;
            case MINUS:
                pausePresentation();
                rescaleDown();
                break;
            default:
                //Because of a bug, that javafx thinks a pressed +/- is some else key
                String pressedKeyText = event.getText();
                if (pressedKeyText.equals("-")) {
                    rescaleDown();
                }
                if (pressedKeyText.equals("+")) {
                    rescaleUp();
                }
                break;
        }
    }

    /**
     * Set the current image as the shown one
     */
    private void setCurrentImage() {
        if (currentPic != null) {
            imagecontainer.setImage(currentPic.getImage());
            resetImageScale();
        }
        resetImageScale();
        fitSizeOfImageContainer();
    }

    public void resetImageScale() {
        imagecontainer.setFitHeight(currentPic.getImage().getHeight());
        imagecontainer.setFitWidth(currentPic.getImage().getWidth());
    }

    public void fitSizeOfImageContainer() {
        //Match the height and width
        Scene scene = imagecontainer.getScene();
        if (scene != null) {
//            imagecontainer.fitWidthProperty().bind( maincontainer.
//                    widthProperty().subtract( OUTER_PADDING ) );
//            imagecontainer.fitHeightProperty().bind( maincontainer.
//                    heightProperty().subtract( OUTER_PADDING ) );
        }
    }

    /**
     * shows the next reandom picture
     */
    public void randomPicture() {
        PictureElement newPic;
        newPic = WurstballData.getInstance().pollPicFromBuffer();
        if (newPic == null) {
            ThreadController.getInstance().checkThreads();
        } else {
            currentPic = newPic;
            setCurrentImage();
        }
    }

    /**
     * rescales the current picture up
     */
    public void rescaleUp() {
        currentImageZoom.set(currentImageZoom.get() * 2.0);
    }

    /**
     * rescales the current picture down
     */
    public void rescaleDown() {
        currentImageZoom.set(currentImageZoom.get() * 0.5);
    }

    /**
     * @deprecated on behalf of ZoomChangeListener
     * @param scaleValue
     */
    private void scaleImage(Double scaleValue) {
        imagecontainer.setScaleX(imagecontainer.getScaleX() * scaleValue);
        imagecontainer.setScaleY(imagecontainer.getScaleY() * scaleValue);
    }

    /**
     * toggles presentation mode
     */
    public void togglePresentationMode() {
        if (future != null && !future.isCancelled()) {
            pausePresentation();
        } else {
            future = ThreadController.getInstance().initPresentation(() -> {
                WurstballViewerController.this.randomPicture();
            });
        }
    }

    /**
     * toggles fullscreen mode
     */
    public void toggleFullscreen() {
        Stage stage = (Stage) maincontainer.getScene().getWindow();
        stage.setFullScreen(!stage.isFullScreen());
    }

    /**
     * save current picture to file
     */
    public void savePictureToFile() {
        LOGGER.log(Level.INFO, "Calling savePic");
        currentPic.savePic();
    }

    /**
     * copys the URL of the current picture to the clipboard
     */
    public void copyPictureUrl() {
        LOGGER.info("Copy url to clipboard");
        ClipboardContent content = new ClipboardContent();
        content.putString(currentPic.getImageURL());
        clipboard.setContent(content);
    }

    /**
     * sets the next picture of the previous viewed pictures as current picture
     */
    public void showNextPicture() {
        currentPic = wurstballData.getNextPic();
        if (currentPic != null) {
            setCurrentImage();
        }
    }

    /**
     * sets the previous picture of the previous viewed pictures as current
     * picture
     */
    public void showPreviousPicture() {
        currentPic = wurstballData.getPreviousPic();
        if (currentPic != null) {
            setCurrentImage();
        }
    }

    /**
     * pauses the presentatiom mode
     */
    public void pausePresentation() {
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
        }
    }

    /**
     * Created by J. Pichardo on 10/14/16 Listener to handle zoom value changes
     */
    private class ZoomChangeListener implements ChangeListener {

        @Override
        public void changed(ObservableValue ov, Object t, Object t1) {

            //Ratio of change.
            double zoomRatio = Math.abs((double) t1 / (double) t);

            //Set ImageScale
            imagecontainer.setFitHeight(imagecontainer.getFitHeight() * zoomRatio);
            imagecontainer.setFitWidth(imagecontainer.getFitWidth() * zoomRatio);
        }

    }

}
