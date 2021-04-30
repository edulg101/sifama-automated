package gov.antt.sifama.config;

import gov.antt.sifama.model.ImgPath;
import gov.antt.sifama.repositories.PathRepo;
import gov.antt.sifama.services.FotoService;
import gov.antt.sifama.services.ImportExcelTika;
import gov.antt.sifama.services.ImportFromExcel;
import gov.antt.sifama.services.StartAutomation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.awt.*;
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

    @Autowired
    PathRepo pathRepo;




    @Bean
    public boolean inicio() throws Exception {


        tika.parseExcel(SPREADSHEETPATH);


        System.out.println("Digite a pasta onde estão os arquivos zip das fotos");

        String origemFotosFolder = SCANNER.nextLine();

        origemFotosFolder = origemFotosFolder.replace("\\", File.separator);

//        System.out.println("DIGITE:");
//        System.out.printf("1 -> Movimentar Mouse \n 2 -> NÃO Movimentar Mouse\n");
//        String o = SCANNER.nextLine();
//        if (o.equals("1")){
//            Robot robot = new Robot();
//
//            Runnable r = () -> {
//                robot.mouseMove(100,200);
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                robot.mouseMove(800,500);
//            };
//
//            mouseMove(r, 5000);
//        }



//        String origemFotosFolder = "/home/eduardo/Documentos/projetos/sifamaSources/imageszipped";

//        String origemFotosFolder = "D:\\Documentos\\Users\\Eduardo\\Documentos\\ANTT\\OneDrive - ANTT- Agencia Nacional de Transportes Terrestres\\CRO\\Relatórios RTA\\Diário 10.03.2021\\Anexos";


        ImgPath imgPath = new ImgPath(1, origemFotosFolder);

        pathRepo.save(imgPath);



        fotoService.unzipAllDirectory(origemFotosFolder, IMGPATH);

        ie.saveFotosOnLocal();

        fotoService.insertCaption();

        return true;
    }
    private static void mouseMove(Runnable runnable, int delay){
        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(delay);
                    runnable.run();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }).start();
    }
}
