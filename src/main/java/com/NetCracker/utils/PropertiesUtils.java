package com.NetCracker.utils;

import com.NetCracker.Main;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class PropertiesUtils
{
    private static Map<String, Properties> filePropertiesMap = new HashMap<>();

    public static String getProperty(String filePath, String propertyName) throws IOException
    {
        var absoluteFilePath = Paths.get(filePath).toAbsolutePath();
        Properties requestedFileProperties = filePropertiesMap.get(absoluteFilePath.toString());

        if (requestedFileProperties == null)
        {
            var newFileProperties = new Properties();
            newFileProperties.load(Main.class.getClassLoader().getResourceAsStream(absoluteFilePath.getFileName().toString()));
            requestedFileProperties = newFileProperties;

            filePropertiesMap.put(absoluteFilePath.toString(), newFileProperties);
        }

        return requestedFileProperties.getProperty(propertyName);
    }

    public static Long getPropertyAsLong(String filePath, String propertyName) throws IOException, NumberFormatException
    {
        var propertyValue = getProperty(filePath, propertyName);
        return Long.valueOf(propertyValue);
    }

    // It's convenient to have intellij idea to pop up hints about these files
    public static final String applicationProperties = "src/main/resources/application-local.properties";
    public static final String credentialProperties = "src/main/resources/credentials.properties";
}
