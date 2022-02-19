package com.NetCracker.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.el.PropertyNotFoundException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//This utility cleans directory specified in properties file periodically.
@Slf4j
@Component
public class TmpCleanerUtility
{
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Duration cleanDelay;

    TmpCleanerUtility()
    {
        try
        {
            cleanDelay = Duration.ofSeconds(PropertiesUtils.
                    getPropertyAsLong(PropertiesUtils.applicationProperties, "app.hospital.tmp.delay"));
        }
        catch (Exception e)
        {
            throw new PropertyNotFoundException("app.hospital.clean.tmp property not found in file " +
                                                                                PropertiesUtils.applicationProperties);
        }
    }

    public void deleteTmpFilesPeriodically() throws IOException
    {
        final Runnable deleteTask = () ->
        {
            File tempDirectory;

            try
            {
                tempDirectory = new File(PropertiesUtils.getProperty(PropertiesUtils.applicationProperties,
                        "app.hospital.tmp.directory"));
            }
            catch (IOException e)
            {
                log.error("Could not determine temporary directory");
                scheduler.shutdown();
                return;
            }

            File[] directoryListing = tempDirectory.listFiles();

            if (directoryListing != null)
            {
                var currentTime = Instant.now();
                for (File tempFile : directoryListing)
                {
                    var modificationTime = Instant.ofEpochMilli(tempFile.lastModified());

                    if (Duration.between(currentTime, modificationTime).compareTo(cleanDelay) > 0)
                    {
                        if (!tempFile.delete())
                        {
                            log.info("Could not delete temporary file "
                                    + tempFile.toURI().relativize(tempDirectory.toURI()).getPath());
                        }
                    }
                }
            }
            else
            {
                log.warn("Could not delete any of temporary files");
            }
        };
    }

    //Run this after spring startup
    //final ScheduledFuture<?> taskHandler = scheduler.scheduleAtFixedRate(deleteTask, 10, 10, TimeUnit.MINUTES);
}