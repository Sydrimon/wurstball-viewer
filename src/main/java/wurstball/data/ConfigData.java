package wurstball.data;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sydrimon
 */
public class ConfigData {

    private static final ConfigData INSTANCE = loadConfigData();
    private static final String CONFIG_PATH = System.getProperty("user.home") + "/.wurstball.config";

    public static final int DEFAULT_MAX_RETRIES = 4;
    public static final int DEFAULT_PREVIOUS_PIC_MAX = 10;
    public static final int DEFAULT_PIC_BUFFER_SIZE = 5;
    public static final int DEFAULT_PRESENTATION_SPEED_VALUE = 2;
    public static final TimeUnit DEFAULT_PRESENTATION_SPEED_UNIT = TimeUnit.SECONDS;
    public static final String DEFAULT_SAVE_PICTURE_PATH = System.getProperty("user.home");

    private int maxRetries;
    private int previousPicMax;
    private int picBufferSize;
    private int presentationSpeedValue;
    private TimeUnit presentaionSpeedUnit;

    private String savePicturePath;

    public ConfigData() {
        maxRetries = DEFAULT_MAX_RETRIES;
        previousPicMax = DEFAULT_PREVIOUS_PIC_MAX;
        picBufferSize = DEFAULT_PIC_BUFFER_SIZE;
        presentationSpeedValue = DEFAULT_PRESENTATION_SPEED_VALUE;
        presentaionSpeedUnit = DEFAULT_PRESENTATION_SPEED_UNIT;
        savePicturePath = DEFAULT_SAVE_PICTURE_PATH;
    }

    public static ConfigData getInstance() {
        return INSTANCE;
    }

    private static ConfigData loadConfigData() {
        try {
            return readConfigData();
        } catch (IOException e) {
            Logger.getLogger(ConfigData.class.getName()).log(Level.SEVERE, "Failed reading the config file", e);
        }
        return new ConfigData();
    }

    private static ConfigData readConfigData() throws IOException {
        try (FileReader configReader = new FileReader(CONFIG_PATH)) {
            Gson gson = new Gson();
            ConfigData configData = gson.fromJson(configReader, ConfigData.class);
            return configData;
        } catch (FileNotFoundException fileException) {
            return new ConfigData();
        }
    }

    public void writeConfigFile() throws IOException {
        try (FileWriter configWriter = new FileWriter(CONFIG_PATH)) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(this);
            configWriter.write(jsonString);
            Logger.getLogger(ConfigData.class.getName()).log(Level.FINER, "jsonString={0}", jsonString);
        }
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public int getPreviousPicMax() {
        return previousPicMax;
    }

    public void setPreviousPicMax(int previousPicMax) {
        this.previousPicMax = previousPicMax;
    }

    public int getPicBufferSize() {
        return picBufferSize;
    }

    public void setPicBufferSize(int picBufferSize) {
        this.picBufferSize = picBufferSize;
    }

    public String getSavePicturePath() {
        return savePicturePath;
    }

    public void setSavePicturePath(String savePicturePath) {
        this.savePicturePath = savePicturePath;
    }

    public int getPresentationSpeedValue() {
        return presentationSpeedValue;
    }

    public void setPresentationSpeedValue(int presentationSpeedValue) {
        this.presentationSpeedValue = presentationSpeedValue;
    }

    public TimeUnit getPresentaionSpeedUnit() {
        return presentaionSpeedUnit;
    }

    public void setPresentaionSpeedUnit(TimeUnit presentaionSpeedUnit) {
        this.presentaionSpeedUnit = presentaionSpeedUnit;
    }

    public void resetSavePicturePath() {
        this.savePicturePath = DEFAULT_SAVE_PICTURE_PATH;
    }

    public void resetPicBufferSize() {
        this.picBufferSize = DEFAULT_PIC_BUFFER_SIZE;
    }

    public void resetPreviousPicMax() {
        this.previousPicMax = DEFAULT_PREVIOUS_PIC_MAX;
    }

    public void resetMaxRetries() {
        this.maxRetries = DEFAULT_MAX_RETRIES;
    }

    public void resetPresentationSpeedValue() {
        this.presentationSpeedValue = DEFAULT_PRESENTATION_SPEED_VALUE;
    }
    
    public void resetPresentationSpeedUnit() {
        this.presentaionSpeedUnit = DEFAULT_PRESENTATION_SPEED_UNIT;
    }
    
    public void resetAll() {
        resetMaxRetries();
        resetPicBufferSize();
        resetPreviousPicMax();
        resetSavePicturePath();
        resetPresentationSpeedValue();
        resetPresentationSpeedUnit();
    }
}
