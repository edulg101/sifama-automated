package gov.antt.sifama.config;

import gov.antt.sifama.services.ImportFromExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;
import java.text.ParseException;

@Configuration
@Profile("mysql")
public class mysqlConfig {

    @Autowired
    ImportFromExcel ie;

    public static final String DRIVERPATH = "D:\\chromedriver.exe";
    public static final String IMGPATH = "D:\\sifamadocs\\imagens";
    public static final String SPREADSHEETPATH = "D:\\sifamadocs\\planilha\\tros.xlsx";

    @Bean
    public boolean inicio() throws Exception {


        ie.getFilesInPath(IMGPATH);

        ie.readSpreadsheet(SPREADSHEETPATH);

        return true;
    }
}
