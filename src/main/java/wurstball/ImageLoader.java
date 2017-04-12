package wurstball;

import java.util.Observable;
import wurstball.data.PictureElement;
import wurstball.data.WurstballData;

/**
 * A runnable class to load a picture from the internet in a thread
 *
 * @author Sydrimon
 */
public class ImageLoader extends Observable implements Runnable {

    public static final int THREAD_POOL_SIZE = 8;

    @Override
    public void run() {
        addObserver(ThreadController.getInstance());
        while (true) {
            try {
                String url = WurstballData.getInstance().getPicUrl();
                if (url != null) {
                    WurstballData.getInstance().addPicToBuffer(
                            new PictureElement(url));
                } else {
                    //todo notify ThreadController that thread is dead
                    notifyObservers();
                    return;
                }
            } catch (InterruptedException ex) {
                //todo notify ThreadController that thread is dead
                notifyObservers();
                return;
            }
        }
    }
}
