package edu.learn.opensources.spark.dataframes;

import edu.learn.common.misc.Const;
import edu.learn.opensources.spark.common.SharedSparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class TestDataframeApis implements SharedSparkSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestDataframeApis.class);

    @Test
    public void countDataframeFromCSV(){
        String filePath = String.format("%s/src/main/resources/ramen-ratings.csv", Const.BASE_DIR);
        LOGGER.info("Created data frame from csv file {}", filePath);
        Dataset<Row> df = spark.read().option("header", "true").csv(filePath);

        df.show();
        df.printSchema();
        assertEquals(2584, df.count());
    }

}
