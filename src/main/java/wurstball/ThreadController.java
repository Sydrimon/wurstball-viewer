/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wurstball;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static wurstball.ImageLoader.THREAD_POOL_SIZE;

/**
 *
 * @author sydrimon
 */
public class ThreadController implements Observer {

    private int runningThreads = ImageLoader.THREAD_POOL_SIZE;
    
    private static final int PRESENTATION_THREADS = 1;
    private static final long PRESENTATION_INTERVAL = 2;
    private static final ScheduledExecutorService EXECUTOR
            = Executors.newScheduledThreadPool(THREAD_POOL_SIZE + PRESENTATION_THREADS);

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
            checkConnection();
        } else {
            decrementRunningThreads();
        }
    }

    private void checkConnection() {
        try {
            Jsoup.connect("https://ircz.de/").get();
            //if still connected
            startImageLoader(ImageLoader.THREAD_POOL_SIZE - runningThreads);
            resetRunningThreads();
        } catch (IOException ex) {
            Logger.getLogger(ThreadController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkThreads() {
        this.checkConnection();
    }
    
    public Future initPresentation(Runnable r) {
        return EXECUTOR.scheduleAtFixedRate(r, 0, PRESENTATION_INTERVAL, TimeUnit.SECONDS);
    }
}
