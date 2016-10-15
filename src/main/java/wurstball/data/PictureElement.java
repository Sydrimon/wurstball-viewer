package wurstball.data;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

/**
 * Contains the actual picture and the URL of the picture
 *
 * @author Sydrimon
 */
public class PictureElement {

    private Image image;
    private String imageURL;

    public PictureElement(String url) {
        this.imageURL = url;
        image = new Image(url);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * saves the picture as a file
     */
    public void savePic() {

        Stage fileChooserStage = new Stage();
        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Select an Image");
        fileChooser.getExtensionFilters().add(new FileChooser.
                ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.setInitialFileName(imageURL.substring(imageURL.length() - 13));

        File file = fileChooser.showSaveDialog(fileChooserStage);

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), imageURL.substring(imageURL.length() - 3), file);
        } catch (IOException ex) {
            Logger.getLogger(PictureElement.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
