package com.NetCracker.Configuration;

import com.NetCracker.Entities.Appointment;
import com.NetCracker.Repositories.AppointmentRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class LoadDatabase {
    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(AppointmentRepo repository) {
        return args -> {
            log.info("Preloading ");
        };
    }
}
