package wurstball.ui;

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
import wurstball.Wurstball;
import wurstball.data.PictureElement;
import wurstball.data.WurstballData;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    private final WurstballData wurstballData = WurstballData.getInstance();

    private static PictureElement currentPic;
    private static Clipboard clipboard ;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clipboard = Clipboard.getSystemClipboard();
        currentPic = wurstballData.getNextPic();

        setCurrentImage();
    }

    @FXML
    private void handleKeyPress(KeyEvent event){
        KeyCode pressed = event.getCode();
        switch(pressed){
            case R : randomPicture();
                break;
            case M : resetImageScale();
                break;
            case SPACE: togglePresentationMode();
                break;
            case S: savePictureToFile();
                break;
            case LEFT: showPreviousPicture();
                break;
            case RIGHT: showNextPicture();
                break;
            case C: copyPictureUrl();
                break;
            case F: toggleFullscreen();
                break;
            case PLUS: rescaleUp();
                break;
            case MINUS: rescaleDown();
                break;
            default:
                //Because of a bug, that javafx thinks a pressed +/- is some else key
                String pressedKeyText = event.getText();
                if(pressedKeyText.equals("-")){
                    rescaleDown();
                }
                if(pressedKeyText.equals("+")){
                    rescaleUp();
                }
                break;
        }
    }

    /**
     * Set the current image as the shown one
     */
    private void setCurrentImage() {
        if(currentPic != null){
            imagecontainer.setImage(currentPic.getImage());
        }
        resetImageScale();
        fitSizeOfImageContainer();
    }

    private void resetImageScale() {
        imagecontainer.setScaleX(1.0);
        imagecontainer.setScaleY(1.0);
    }

    private void fitSizeOfImageContainer() {
        //Match the height and width
        Scene scene = imagecontainer.getScene();
        if(scene != null){
            imagecontainer.fitWidthProperty().bind(maincontainer.widthProperty().subtract(OUTER_PADDING));
            imagecontainer.fitHeightProperty().bind(maincontainer.heightProperty().subtract(OUTER_PADDING));
        }
    }

    /**
     * shows the next reandom picture
     */
    private void randomPicture() {
        currentPic = WurstballData.getInstance().getNextPic();
        setCurrentImage();
        pausePresentation();
    }


    /**
     * rescales the current picture up
     */
    private void rescaleUp() {
        //todo
       //pausePresentation();
        scaleImage(2.0);
    }

    private void scaleImage(Double scaleValue) {
        imagecontainer.setScaleX(imagecontainer.getScaleX() * scaleValue);
        imagecontainer.setScaleY(imagecontainer.getScaleY() * scaleValue);
    }

    /**
     * rescales the current picture down
     */
    private void rescaleDown() {
        //todo
//        pausePresentation();
        scaleImage(0.5);
    }

    /**
     * toggles presentation mode
     */
    private void togglePresentationMode() {
        //todo
//        if (future != null && !future.isCancelled()) {
//            pausePresentation();
//        } else {
//            future = EXECUTOR.scheduleAtFixedRate(() -> {
//                Wurstball.changePic(WurstballData.getInstance().getNextPic().getImage());
//            }, 0, 2, TimeUnit.SECONDS);
//        }
    }

    /**
     * toggles fullscreen mode
     */
    private void toggleFullscreen() {
        Stage stage = (Stage) maincontainer.getScene().getWindow();
        stage.setFullScreen(true);
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
        currentPic= wurstballData.getNextPic();
        setCurrentImage();
    }

    /**
     * sets the previous picture of the previous viewed pictures as current
     * picture
     */
    private void showPreviousPicture() {
        currentPic= wurstballData.getPreviousPic(true); //todo not working correct
        setCurrentImage();
    }

    /**
     * pauses the presentatiom mode
     */
    private void pausePresentation() {
        //todo
//        if (future != null && !future.isCancelled()) {
//            future.cancel(false);
//        }
    }
}
