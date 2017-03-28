package wurstball;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import wurstball.data.PictureElement;
import wurstball.data.WurstballData;

/**
 * A runnable class to load a picture from the internet in a thread
 *
 * @author Sydrimon
 */
public class ImageLoader implements Runnable {

    public static final int THREAD_POOL_SIZE = 8;
    public static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

    @Override
    public void run() {
        while (true) {
            try {
                String url = WurstballData.getInstance().getPicUrl();
                if (url != null) {
                    WurstballData.getInstance().picBuffer.put(new PictureElement(url));
                } else {
                    return;
                }
            } catch (InterruptedException ex) {
                return;
            }
        }
    }
}
