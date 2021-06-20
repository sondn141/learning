package edu.learn.opensources.spark.common;

import org.apache.spark.sql.SparkSession;

public class SparkUtils {

    public static SparkSession.Builder createSparkSessionBuilder(String... options){
        if(options.length % 2 != 0)
            throw new RuntimeException("Options is invalid");

        SparkSession.Builder sessionBuilder = SparkSession.builder();
        for(int i = 0 ; i < options.length - 1 ; i += 2){
            sessionBuilder = sessionBuilder.config(options[i], options[i + 1]);
        }

        return sessionBuilder;
    }
}
