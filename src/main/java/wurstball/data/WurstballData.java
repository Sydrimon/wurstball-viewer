package wurstball.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wurstball.ThreadController;

/**
 *
 * @author Sydrimon
 */
public class WurstballData {

    private static final Logger LOGGER = Logger.getLogger(WurstballData.class.getName());

    public static final String ADDRESS = "http://wurstball.de/random/";

    /**
     * the tag of the picture on the website
     */
    private static final String PIC_TAG = "div[id=content-main] > img";

    private static final int MAX_RETRIES = 4;
    private static final int PREVIOUS_PIC_MAX = 10;
    private static final int PIC_BUFFER_MAX_SIZE = 5;

    private final ArrayBlockingQueue<PictureElement> picBuffer;
    private final ArrayList<PictureElement> prevPics;

    private int currentPicIndex;

    private static final WurstballData INSTANCE = new WurstballData();
    private static ConfigData config;
    
    private WurstballData() {
        picBuffer = new ArrayBlockingQueue<>(PIC_BUFFER_MAX_SIZE, true);
        prevPics = new ArrayList<>(PREVIOUS_PIC_MAX);
        config = ConfigData.getInstance();
        ThreadController.startImageLoader();
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

    //todo javadoc
    public void addPicToBuffer(PictureElement pic) throws InterruptedException {
        picBuffer.put(pic);
    }

    //todo javadoc
    public boolean bufferIsEmpty() {
        return picBuffer.isEmpty();
    }
    /**
     * returns the URL of the picture from
     * {@link wurstball.Wurstball#ADDRESS ADDRESS} with the tag
     * {@link wurstball.Wurstball#PIC_TAG PIC_TAG}
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
     * returns the first picture in the buffer (blocking)
     *
     * @return the first {@link wurstball.data.PictureElement PictureElement} in
     * the buffer
     */
    public PictureElement getPicFromBuffer() {
        try {
            PictureElement pic = picBuffer.take();
            addPreviousPic(pic);
            return pic;
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, "Interrupted on waiting for pictures", ex);
        }
        return null;
    }

    /**
     * returns the first picture in the buffer (non-blocking)
     *
     * @return the first {@link wurstball.data.PictureElement PictureElement} in
     * the buffer
     */
    public PictureElement pollPicFromBuffer() {
        PictureElement pic = picBuffer.poll();
        if (pic != null) {
            addPreviousPic(pic);
        }
        return pic;
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
     * @return the previous pic in the list or null if there is no other pic in
     * the list
     */
    public PictureElement getPreviousPic() {
        if (!prevPics.isEmpty() && currentPicIndex != 0) {
            return prevPics.get(--currentPicIndex);
        }
        return null;
    }

    /**
     *
     * @return the next pic in the list or null if there is no other pic in the
     * list
     */
    public PictureElement getNextPic() {
        if (!prevPics.isEmpty() && currentPicIndex != prevPics.size() - 1) {
            return prevPics.get(++currentPicIndex);
        }
        return null;
    }
}
