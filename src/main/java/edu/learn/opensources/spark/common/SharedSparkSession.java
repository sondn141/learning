package edu.learn.opensources.spark.common;

import edu.learn.common.config.Config;
import org.apache.spark.sql.SparkSession;

import java.util.Properties;

public interface SharedSparkSession {

    SparkSession spark = createBuilder().getOrCreate();

    static SparkSession.Builder createBuilder(){
        Config props = new Config("spark-default.properties");
        SparkSession.Builder sessionBuilder = SparkSession.builder();
        for(String key : props.keySet(true)){
            sessionBuilder.config(key, props.getProperty(key));
        }

        return sessionBuilder;
    }


}
