package edu.learn.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.learn.common.misc.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Config {

    @JsonIgnore
    private static final String CONFIG_FILE = "config.properties";

    private final Map<String, Object> runtimeObj = new HashMap<>();

    @JsonIgnore
    private static Properties props;

    @JsonIgnore
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    @JsonIgnore
    private String prefix;

    public Config(){
        if(props == null)
            loadDefault(CONFIG_FILE);
    }

    public Config(String configFileName){
        if(props == null){
            loadDefault(configFileName);
        }
    }

    public Config(boolean loadDefault) {
        if(loadDefault && props == null){
            loadDefault(CONFIG_FILE);
        }
    }

    public Config(boolean loadDefault, Map<String, String> initialConf){
        if(loadDefault && props == null){
            loadDefault(CONFIG_FILE);
        }

        for(Map.Entry<String, String> kv : initialConf.entrySet()){
            runtimeObj.put(kv.getKey(), kv.getValue());
        }
    }

    public Config withPrefix(String prefix){
        this.prefix = prefix;
        return this;
    }

    public Config nonPrefix(){
        this.prefix = null;
        return this;
    }

    public static void loadDefault(String confFile) {
        props = new Properties();

        try {
            props.load(new InputStreamReader(
                    Objects.requireNonNull(Config.class.getClassLoader()
                            .getResourceAsStream(confFile)),
                    StandardCharsets.UTF_8));

            String externalConfFolder = System.getProperty("conf");
            if(externalConfFolder != null)
                addResource(Strings.concatFilePath(externalConfFolder, CONFIG_FILE));
        } catch (Exception e) {
            logger.warn("Can not load properties from file {}", CONFIG_FILE);
        }
    }

    public static void addResource(String configFilePath){
        try {
            props.load(new InputStreamReader(
                    new FileInputStream(configFilePath),
                    StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("Can not load config from " + configFilePath);
        }
    }

    public boolean has(String key){
        return getProperty(key) != null;
    }

    public String getProperty(String key){
        if(Strings.isNotNullOrEmpty(prefix)){
            key = prefix + "." + key;
        }
        if(runtimeObj.containsKey(key))
            return runtimeObj.get(key).toString();
        else
            return props.getProperty(key);
    }

    public String getProperty(String key, String def){
        String val = getProperty(key);
        return val == null ? def : val;
    }

    /**
     * Lấy về giá trị có kiểu int
     *
     * @param key    key của giá trị cần lấy
     * @param defVal giá trị mặc định trả về
     * @return số int của giá trị cần lấy hoặc giá trị mặc định nếu
     * không tìm thấy giá trị nào ứng với key cần tìm
     */
    public int getIntProperty(String key, int defVal) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * Lấy về giá trị có kiểu long
     *
     * @param key    key của giá trị cần lấy
     * @param defVal giá trị mặc định trả về
     * @return số long của giá trị cần lấy hoặc giá trị mặc định nếu
     * không tìm thấy giá trị nào ứng với key cần tìm
     */
    public long getLongProperty(String key, long defVal) {
        try {
            return Long.parseLong(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * Lấy về giá trị có kiểu double
     *
     * @param key    key của giá trị cần lấy
     * @param defVal giá trị mặc định trả về
     * @return số double của giá trị cần lấy hoặc giá trị mặc định nếu
     * không tìm thấy giá trị nào ứng với key cần tìm
     */
    public double getDoubleProperty(String key, double defVal) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * Lấy về giá trị có kiểu bool
     *
     * @param key    key của giá trị cần lấy
     * @param defVal giá trị mặc định trả về
     * @return số double của giá trị cần lấy hoặc giá trị mặc định nếu
     * không tìm thấy giá trị nào ứng với key cần tìm
     */
    public boolean getBoolProperty(String key, boolean defVal) {
        try {
            return Boolean.parseBoolean(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * Lấy về giá trị có kiểu list String
     *
     * @param key key của giá trị cần lấy
     * @return list String các giá trị được phân cách bởi dấu phẩy
     */
    public List<String> getCollection(String key) {
        try {
            return Arrays.asList(getProperty(key).split(","));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    /**
     * Lấy về giá trị có kiểu list String
     *
     * @param key       key của giá trị cần lấy
     * @param delimiter chuỗi dùng để phân tách các giá trị
     * @return list String các giá trị được phân cách bởi delimiter
     */
    public List<String> getCollection(String key, String delimiter) {
        try {
            return Arrays.asList(getProperty(key).split(delimiter));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    /**
     * Lấy về giá trị có kiểu DateTime theo format yyyy-MM-dd HH:mm:ss
     *
     * @param key    key của giá trị cần lấy
     * @param defVal giá trị mặc định trả về
     * @return object Date của giá trị cần lấy hoặc giá trị mặc định nếu
     * không tìm thấy giá trị nào ứng với key cần tìm hoặc value
     * ở dạng raw không phù hợp để convert thành Date
     */
    public Date getDateTime(String key, Date defVal) {
        return getDateTime(key, "yyyy-MM-dd HH:mm:ss", defVal);
    }

    /**
     * Lấy về giá trị có kiểu DateTime
     *
     * @param key    key của giá trị cần lấy
     * @param format format để convert từ giá trị raw String về object Date
     * @param defVal giá trị mặc định trả về
     * @return object Date của giá trị cần lấy hoặc giá trị mặc định nếu
     * không tìm thấy giá trị nào ứng với key cần tìm hoặc value
     * ở dạng raw không phù hợp để convert thành Date
     */
    public Date getDateTime(String key, String format, Date defVal) {
        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(getProperty(key));
        } catch (Exception e) {
            return defVal;
        }
    }

    /**
     * Lấy về một hoặc nhiều class
     *
     * @param key key của giá trị cần lấy
     * @return danh sách các object Classs
     * @throws ClassNotFoundException nếu có lỗi xảy ra khi convert
     *                                từ tên class thành object Class
     */
    public Collection<Class<?>> getClasses(String key) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<>();
        for (String className : getCollection(key)) {
            classes.add(Class.forName(className));
        }
        return classes;
    }

    /**
     * Lấy ra một object trong quá trình chạy của hệ thống
     * @param key property's key
     * @param clazz object class
     * @return
     */
    public <T> T getProp(String key, Class<T> clazz){
        Object obj = runtimeObj.get(key);
        if(!obj.getClass().isAssignableFrom(clazz))
            throw new RuntimeException("Fail to get runtime object. Parsing error");

        return clazz.cast(obj);
    }

    /**
     * Set một object vào config trong quá trình chạy của hệ thống
     * @param key
     * @param obj
     */
    public Config setProp(String key, Object obj){
        runtimeObj.put(key, obj);
        return this;
    }

    public Set<String> keySet(boolean includeDefault){
        Set<String> keySet = new HashSet<>(runtimeObj.keySet());
        if(includeDefault)
            props.keySet().forEach(k -> keySet.add(String.valueOf(k)));

        return keySet;
    }

    public Properties getDefaults(){
        return props;
    }

    public boolean equals(Object config) {
        if(!(config instanceof Config))
            return false;
        Config conf = (Config) config;
        Set<String> keySet = conf.keySet(false);
        if(keySet.size() != runtimeObj.size())
            return false;
        for(String key : keySet){
            if(!conf.getProperty(key).equals(runtimeObj.get(key).toString()))
                return false;
        }

        return true;
    }
}
