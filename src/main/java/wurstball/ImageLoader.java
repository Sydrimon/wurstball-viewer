package wurstball;

import java.util.logging.Level;
import java.util.logging.Logger;
import static wurstball.Wurstball.MAX_RETRIES;
import wurstball.data.PictureElement;
import wurstball.data.WurstballData;

/**
 * A runnable class to load a picture from the internet in a thread
 *
 * @author Sydrimon
 */
public class ImageLoader implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < MAX_RETRIES; i++) {
            try {
                WurstballData.getInstance().picBuffer.put(new PictureElement(WurstballData.getInstance().getPicUrl()));
                break;
            } catch (InterruptedException ex) {
                Logger.getLogger(ImageLoader.class.getName()).log(Level.SEVERE,
                        "Failed to load image at try " + (i + 1), ex);
            }
        }
    }

}
