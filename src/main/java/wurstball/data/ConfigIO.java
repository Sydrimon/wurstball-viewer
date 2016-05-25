package wurstball.data;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class ConfigIO {

    private static final String CONFIG_PATH = System.getProperty("user.home") + "/.wurstball.config";

    public static ConfigData readConfigData() throws IOException {
        try (FileReader configReader = new FileReader(CONFIG_PATH)) {
            Gson gson = new Gson();
            ConfigData configData = gson.fromJson(configReader, ConfigData.class);
            return configData;
        }
    }

    public static void writeConfigFile(ConfigData data) throws IOException {
        try (FileWriter configWriter = new FileWriter(CONFIG_PATH)) {
            Gson gson = new Gson();
            String jsonString = gson.toJson(data);
            configWriter.write(jsonString);
            Logger.getLogger(ConfigData.class.getName()).log(Level.FINER, "jsonString={0}", jsonString);
        }
    }
    
    public static ConfigData loadOrDefault() {
        try {
            return readConfigData();
        } catch (FileNotFoundException ex ) {
            Logger.getLogger(ConfigData.class.getName()).log(Level.INFO, "Config file not found - using defaults ({0})", ex.getLocalizedMessage());
        } catch (IOException e) {
            Logger.getLogger(ConfigData.class.getName()).log(Level.SEVERE, "Failed reading the config file", e);
        }
        return new ConfigData();
    }
}
