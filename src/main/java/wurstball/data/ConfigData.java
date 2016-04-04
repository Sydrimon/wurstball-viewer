package wurstball.data;

/**
 *
 * @author sydrimon
 */
public class ConfigData {
    
    private static final ConfigData INSTANCE = loadConfigData();
    
    private String savePicturePath;
    private int numberPreloadItems;
    private int numberLastSeenItems;
    
    public ConfigData () {
        
    }
    
    public static ConfigData getInstance() {
        return INSTANCE;
    }
    
    private static ConfigData loadConfigData() {
        //TODO
        return new ConfigData();
    }
    
    public boolean writeConfigFile() {
        //TODO
        return false;
    }
}
