package wurstball.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wurstball.ImageLoader;
import static wurstball.Wurstball.ADDRESS;
import static wurstball.Wurstball.PIC_TAG;

/**
 *
 * @author Sydrimon
 */
public class WurstballData {

    private static final Logger LOGGER = Logger.getLogger(WurstballData.class.getName());

    public static final int PREVIOUS_PIC_MAX = 10;
    public static final int PIC_BUFFER_MAX_SIZE = 5;

    public final ArrayBlockingQueue<PictureElement> picBuffer;
    public final ArrayList<PictureElement> prevPics;

    private int currentPicIndex;

    private static final WurstballData INSTANCE = new WurstballData();
    private static ConfigData config;
    
    private WurstballData() {
        picBuffer = new ArrayBlockingQueue<>(PIC_BUFFER_MAX_SIZE, true);
        prevPics = new ArrayList<>(PREVIOUS_PIC_MAX);
        fillBuffer();
        config = ConfigData.getInstance();
    }

    /**
     *
     * @return the only instance of {@link WurstballData WurstballData}
     */
    public static WurstballData getInstance() {
        return INSTANCE;
    }

    public ConfigData getConfig() {
        return config;
    }
    
    /**
     * returns the URL of the picture from {@link #ADDRESS ADDRESS} with the tag
     * {@link #PIC_TAG PIC_TAG}
     *
     * @return URL of the picture
     */
    public String getPicUrl() {
        for (int i = 0; i < config.getMaxRetries(); i++) {
            try {
                Document doc = Jsoup.connect(ADDRESS).get();
                Element content = doc.select(PIC_TAG).first();

                String picURL = content.absUrl("src");
                return picURL;
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Bild nicht gefunden. Versuch: " + (i + 1), e);
            }
        }
        return null;
    }

    /**
     * fills the {@link #picBuffer picBuffer} with PictureElements and returns
     * the first one
     *
     * @return the first {@link wurstball.data.PictureElement PictureElement} in
     * the buffer
     */
    public PictureElement getNextPic() {
        for (int i = 0; i < config.getMaxRetries(); i++) {
            try {
                PictureElement pic = picBuffer.take();
                addPreviousPic(pic);
                return pic;
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Interrupted on waiting for pictures", ex);
            }
        }
        return null;
    }

    /**
     * fills the picture buffer with
     * {@link wurstball.data.PictureElement PictureElements}
     */
    private void fillBuffer() {
        for (int i = 0; i < ImageLoader.THREAD_POOL_SIZE; i++) {
            ImageLoader.EXECUTOR.execute(new ImageLoader());
        }
    }

    /**
     * adds a picture to the list of the previous pictures and removes the first
     * if the number of elements in the list reached
     * {@link #PREVIOUS_PIC_MAX PREVIOUS_PIC_MAX}
     *
     * @param image the picture to add to the list of the previous pictures
     */
    public void addPreviousPic(PictureElement image) {
        if (prevPics.size() < config.getPreviousPicMax()) {
            prevPics.add(image);
        } else {
            prevPics.remove(0);
            prevPics.add(image);
        }
        currentPicIndex = prevPics.size() - 1;
    }

    /**
     *
     * @param previous if true returns previous pic else next pic
     * @return the previous or next pic in the list or null if there is no other
     * pic in the list
     */
    public PictureElement getPreviousPic(boolean previous) {
        if (previous) {
            if (!prevPics.isEmpty() && currentPicIndex != 0) {
                return prevPics.get(--currentPicIndex);
            }
        } else if (!prevPics.isEmpty() && currentPicIndex != prevPics.size() - 1) {
            return prevPics.get(++currentPicIndex);
        }
        return null;
    }
}
