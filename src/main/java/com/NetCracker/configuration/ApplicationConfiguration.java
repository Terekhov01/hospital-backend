package com.NetCracker.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

//This configuration class loads required properties files
@PropertySources({
        @PropertySource("classpath:application-local.properties"),
        @PropertySource("classpath:application-prod.properties"),
        @PropertySource("classpath:credentials.properties")
})
@Configuration
public class ApplicationConfiguration
{
}
