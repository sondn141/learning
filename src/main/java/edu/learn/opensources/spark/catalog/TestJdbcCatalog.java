package edu.learn.opensources.spark.catalog;

import edu.learn.opensources.spark.common.SharedSparkSession;
import edu.learn.opensources.spark.common.SparkUtils;
import org.apache.spark.sql.SparkSession;
import org.junit.Test;

public class TestJdbcCatalog implements SharedSparkSession {

    @Test
    public void testJdbcTableCatalog(){
        spark.catalog().listDatabases().show();
    }
}
