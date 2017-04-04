/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wurstball;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static wurstball.ImageLoader.THREAD_POOL_SIZE;

/**
 *
 * @author sydrimon
 */
public class ThreadController implements Observer {

    private int runningThreads = ImageLoader.THREAD_POOL_SIZE;

    private static final ScheduledExecutorService EXECUTOR
            = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);

    private static final ThreadController INSTANCE = new ThreadController();

    public static ThreadController getInstance() {
        return INSTANCE;
    }

    //todo javadoc
    public static void startImageLoader() {
        startImageLoader(ImageLoader.THREAD_POOL_SIZE);
    }

    //todo javadoc
    public static void startImageLoader(int deadThreads) {
        for (int i = 0; i < deadThreads; i++) {
            EXECUTOR.execute(new ImageLoader());
        }
    }

    //todo javadoc
    public static void shutdown() {
        EXECUTOR.shutdownNow();
    }

    synchronized private void decrementRunningThreads() {
        runningThreads--;
    }

    synchronized private void resetRunningThreads() {
        runningThreads = ImageLoader.THREAD_POOL_SIZE;
    }

    @Override
    public void update(Observable o, Object o1) {
        if (runningThreads < ImageLoader.THREAD_POOL_SIZE) {
            decrementRunningThreads();
            try {
                //todo test connection
                Document doc = Jsoup.connect("http://wurstball.de/").get();
                //if still connected
                startImageLoader(ImageLoader.THREAD_POOL_SIZE - runningThreads);
                resetRunningThreads();
            } catch (IOException ex) {
                Logger.getLogger(ThreadController.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            decrementRunningThreads();
        }
    }
    
    public void checkConnection() {
        this.update(null, null);
    }
}
