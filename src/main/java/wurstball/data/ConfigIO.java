package wurstball.data;

import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author simon
 */
public class ConfigIO {
    private static final Logger LOGGER = Logger.getLogger(ConfigData.class.getName());
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
            LOGGER.log(Level.FINER, "jsonString={0}", jsonString);
            Files.setAttribute(Paths.get(CONFIG_PATH), "dos:hidden", true);
        }
    }
    
    public static ConfigData loadOrDefault() {
        try {
            return readConfigData();
        } catch (FileNotFoundException ex ) {
            LOGGER.log(Level.INFO, "Config file not found - using defaults ({0})", 
                                        ex.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed reading the config file", e);
        }
        return new ConfigData();
    }
}
