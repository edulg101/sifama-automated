package gov.antt.sifama.config;

import gov.antt.sifama.services.FotoService;
import gov.antt.sifama.services.ImportFromExcel;
import gov.antt.sifama.services.StartAutomation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import static gov.antt.sifama.services.appConstants.AppConstants.*;

@Configuration
@Profile("h2")
public class h2Config {


    @Autowired
    StartAutomation startAutomation;

    @Autowired
    ImportFromExcel ie;

    @Autowired
    FotoService fotoService;



    @Bean
    public boolean inicio() throws Exception {

        ie.readSpreadsheet(SPREADSHEETPATH);

        fotoService.unzipAllDirectory(ORIGINIMAGESFOLDER, IMGPATH);

        ie.saveFotosOnLocal();

        fotoService.insertCaption();

        return true;
    }
}
