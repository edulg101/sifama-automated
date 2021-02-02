package gov.antt.sifama.config;

import gov.antt.sifama.StartAutomation;
import gov.antt.sifama.services.ImportFromExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.text.ParseException;

@Configuration
@Profile("h2")
public class h2Config {


    @Autowired
    StartAutomation startAutomation;



    @Bean
    public boolean inicio() throws Exception {

        startAutomation.inicio();

        return true;
    }
}
