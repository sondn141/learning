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
     * L???y v??? gi?? tr??? c?? ki???u int
     *
     * @param key    key c???a gi?? tr??? c???n l???y
     * @param defVal gi?? tr??? m???c ?????nh tr??? v???
     * @return s??? int c???a gi?? tr??? c???n l???y ho???c gi?? tr??? m???c ?????nh n???u
     * kh??ng t??m th???y gi?? tr??? n??o ???ng v???i key c???n t??m
     */
    public int getIntProperty(String key, int defVal) {
        try {
            return Integer.parseInt(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * L???y v??? gi?? tr??? c?? ki???u long
     *
     * @param key    key c???a gi?? tr??? c???n l???y
     * @param defVal gi?? tr??? m???c ?????nh tr??? v???
     * @return s??? long c???a gi?? tr??? c???n l???y ho???c gi?? tr??? m???c ?????nh n???u
     * kh??ng t??m th???y gi?? tr??? n??o ???ng v???i key c???n t??m
     */
    public long getLongProperty(String key, long defVal) {
        try {
            return Long.parseLong(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * L???y v??? gi?? tr??? c?? ki???u double
     *
     * @param key    key c???a gi?? tr??? c???n l???y
     * @param defVal gi?? tr??? m???c ?????nh tr??? v???
     * @return s??? double c???a gi?? tr??? c???n l???y ho???c gi?? tr??? m???c ?????nh n???u
     * kh??ng t??m th???y gi?? tr??? n??o ???ng v???i key c???n t??m
     */
    public double getDoubleProperty(String key, double defVal) {
        try {
            return Double.parseDouble(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * L???y v??? gi?? tr??? c?? ki???u bool
     *
     * @param key    key c???a gi?? tr??? c???n l???y
     * @param defVal gi?? tr??? m???c ?????nh tr??? v???
     * @return s??? double c???a gi?? tr??? c???n l???y ho???c gi?? tr??? m???c ?????nh n???u
     * kh??ng t??m th???y gi?? tr??? n??o ???ng v???i key c???n t??m
     */
    public boolean getBoolProperty(String key, boolean defVal) {
        try {
            return Boolean.parseBoolean(getProperty(key));
        } catch (Exception ignored) {
            return defVal;
        }
    }

    /**
     * L???y v??? gi?? tr??? c?? ki???u list String
     *
     * @param key key c???a gi?? tr??? c???n l???y
     * @return list String c??c gi?? tr??? ???????c ph??n c??ch b???i d???u ph???y
     */
    public List<String> getCollection(String key) {
        try {
            return Arrays.asList(getProperty(key).split(","));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    /**
     * L???y v??? gi?? tr??? c?? ki???u list String
     *
     * @param key       key c???a gi?? tr??? c???n l???y
     * @param delimiter chu???i d??ng ????? ph??n t??ch c??c gi?? tr???
     * @return list String c??c gi?? tr??? ???????c ph??n c??ch b???i delimiter
     */
    public List<String> getCollection(String key, String delimiter) {
        try {
            return Arrays.asList(getProperty(key).split(delimiter));
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    /**
     * L???y v??? gi?? tr??? c?? ki???u DateTime theo format yyyy-MM-dd HH:mm:ss
     *
     * @param key    key c???a gi?? tr??? c???n l???y
     * @param defVal gi?? tr??? m???c ?????nh tr??? v???
     * @return object Date c???a gi?? tr??? c???n l???y ho???c gi?? tr??? m???c ?????nh n???u
     * kh??ng t??m th???y gi?? tr??? n??o ???ng v???i key c???n t??m ho???c value
     * ??? d???ng raw kh??ng ph?? h???p ????? convert th??nh Date
     */
    public Date getDateTime(String key, Date defVal) {
        return getDateTime(key, "yyyy-MM-dd HH:mm:ss", defVal);
    }

    /**
     * L???y v??? gi?? tr??? c?? ki???u DateTime
     *
     * @param key    key c???a gi?? tr??? c???n l???y
     * @param format format ????? convert t??? gi?? tr??? raw String v??? object Date
     * @param defVal gi?? tr??? m???c ?????nh tr??? v???
     * @return object Date c???a gi?? tr??? c???n l???y ho???c gi?? tr??? m???c ?????nh n???u
     * kh??ng t??m th???y gi?? tr??? n??o ???ng v???i key c???n t??m ho???c value
     * ??? d???ng raw kh??ng ph?? h???p ????? convert th??nh Date
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
     * L???y v??? m???t ho???c nhi???u class
     *
     * @param key key c???a gi?? tr??? c???n l???y
     * @return danh s??ch c??c object Classs
     * @throws ClassNotFoundException n???u c?? l???i x???y ra khi convert
     *                                t??? t??n class th??nh object Class
     */
    public Collection<Class<?>> getClasses(String key) throws ClassNotFoundException {
        List<Class<?>> classes = new LinkedList<>();
        for (String className : getCollection(key)) {
            classes.add(Class.forName(className));
        }
        return classes;
    }

    /**
     * L???y ra m???t object trong qu?? tr??nh ch???y c???a h??? th???ng
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
     * Set m???t object v??o config trong qu?? tr??nh ch???y c???a h??? th???ng
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
