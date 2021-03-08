package gov.antt.sifama.config;

import gov.antt.sifama.services.FotoService;
import gov.antt.sifama.services.ImportExcelTika;
import gov.antt.sifama.services.ImportFromExcel;
import gov.antt.sifama.services.StartAutomation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Scanner;

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

    @Autowired
    ImportExcelTika tika;




    @Bean
    public boolean inicio() throws Exception {


        tika.parseExcel(SPREADSHEETPATH);


//        ie.readSpreadsheet(SPREADSHEETPATH);


        System.out.println("Digite a pasta onde estão os arquivos zip das fotos");

        String origemFotosFolder = SCANNER.nextLine();

        System.out.println(origemFotosFolder);

//        origemFotosFolder = origemFotosFolder.replace("\\", "/");

        System.out.println(origemFotosFolder);


        fotoService.unzipAllDirectory(origemFotosFolder, IMGPATH);

        ie.saveFotosOnLocal();

        fotoService.insertCaption();

        return true;
    }
}
