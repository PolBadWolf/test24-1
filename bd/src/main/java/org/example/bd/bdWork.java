package org.example.bd;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class bdWork {
    private Properties propertiesSql = null;
    private final String fileNameProperties = "sql.txt";

    public bdWork() {
        propertiesSql = new Properties();
        try {
            propertiesSql.load(new BufferedReader(new FileReader(fileNameProperties)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void defaultProperties(Properties properties) {
    }
}
