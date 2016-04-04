package wurstball;

import java.util.logging.Level;
import java.util.logging.Logger;
import wurstball.data.WurstballData;

/**
 *
 * @author Sydrimon
 */
public class PresentationMode implements Runnable {

    private boolean run;
    WurstballData wData;

    public PresentationMode() {
        run = true;
        wData = WurstballData.getInstance();
    }

    @Override
    public void run() {
        while (true) {
            if (run) {
                Wurstball.changePic(wData.getNextPic().getImage());
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(PresentationMode.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void stop() {
        this.run = false;
    }

    public void go() {
        this.run = true;
    }

    public void switchState() {
        run = !run;
    }

}
