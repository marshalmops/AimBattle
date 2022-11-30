package com.mcdead.aimbattle.utils;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFileLoaderStorer {
    public static Properties load(final String filename) throws IOException
    {
        if (filename.length() <= 0) return null;

        Properties properties = new Properties();
        FileReader propertiesFile = new FileReader(filename);

        properties.load(propertiesFile);

        return properties;
    }

    public static boolean store(final Properties properties,
                                final String filename) throws IOException
    {
        if (filename.length() <= 0) return false;
        if (properties.isEmpty()) return false;

        FileWriter propertiesFile = new FileWriter(filename);

        properties.store(propertiesFile, "");

        return true;
    }
}
