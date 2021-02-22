package gov.antt.sifama.services;

import gov.antt.sifama.model.Foto;
import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.Tro;
import gov.antt.sifama.repositories.LocalRepo;
import gov.antt.sifama.repositories.TroRepo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.*;

@Service
public class ImportFromExcel {


    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");

    String nIdentidade = "";
    String date;
    String hora;
    String rodovia;
    String pista;
    String kmInicial;
    String kmFinal;
    String sentido;
    String palavraChave;
    String observacao;
    String prazo;



    @Autowired
    TroService troService;

    @Autowired
    LocalService localService;

    @Autowired
    FotoService fotoService;


    public ImportFromExcel() {

    }

    @Transactional
    public void getFilesInPath(String path) throws Exception {
        File file = null;
        String[] files = null;

        try {
            // create new file object
            file = new File(path);

            // array of files and directory
            files = file.list();

        } catch (Exception e) {
            e.printStackTrace();
        }

        List<Local> locais = localService.getAll();
        for (String fileStr : files) {

            String nameCompressed = fileStr;
            if (fileStr.contains("MT")) {
                int index = fileStr.indexOf("MT");
                nameCompressed = fileStr.substring(0, index) + fileStr.substring(fileStr.indexOf("km") - 1);
                File oldName = new File(path + "\\" + fileStr);
                File newName = new File(path + "\\" + nameCompressed);

                if (oldName.renameTo(newName)) {
                    System.out.println("Imagem " + oldName.getName() + "Renomeada para:  " + newName.getName());
                } else {
                    System.out.println("Error");
                }
            }
            fotoService.save(new Foto(null, nameCompressed));

            String statusCompactacao = FotoService.resize(path + "\\" + nameCompressed, 500);

            System.out.println(statusCompactacao);
        }


        List<Foto> fotos = fotoService.getAll();

        for (Foto f : fotos) {
            for (Local l : locais) {
                if (f.getNome().contains(l.getNumIdentificacao())) {
                    l.getArquivosDeFotos().add(f);
                    f.setLocal(l);
                    fotoService.save(f);
                    localService.save(l);
                }
            }
        }
    }

    @Transactional
    public void readSpreadsheet(String fileLocation) throws IOException {
        FileInputStream file = new FileInputStream(new File(fileLocation));
        Workbook workbook = new XSSFWorkbook(file);

        Sheet sheet = workbook.getSheetAt(0);

        boolean startLocais = false;
        boolean endLocais = false;


        Tro tro = null;

        for (int i = 0; i < sheet.getLastRowNum(); i++) {
            if (i < 1){
                continue;
            }
            Row row = sheet.getRow(i);

            double kmInicialDouble = 0;
            double kmFinalDouble = 0;

            for (int j = 0; j < row.getLastCellNum(); j++) {

                if (startLocais) {
                    startLocais = false;
                    break;
                }
                Cell cell = row.getCell(j);

                CellType cellType = cell.getCellType();
                CellType tipo = null;

                if (j == 0 && cellType == STRING) {
                    String cellInfo = cell.getStringCellValue();
                    if (cell.getStringCellValue().toLowerCase().contains("tro")) {
                        cell = row.getCell(j + 1);
                        try {
                            palavraChave = cell.getStringCellValue().toLowerCase();
                        } catch (NullPointerException e){
                            System.out.println("i: " + i + " j: " + j);
                        }
                        tro = new Tro(null, palavraChave);
                        cell = row.getCell(j + 2);
                        if (cell.getCellType() != BLANK){
                            tro.setObservacao(cell.getStringCellValue());
                        }
                        cell = row.getCell(j + 3);
                        try {
                            tipo = cell.getCellType();
                        }catch (NullPointerException e){
                            System.out.println("i: " + i + " j: " + j);
                        }
                        if (tipo != BLANK){
                            double p = cell.getNumericCellValue();
                            prazo = String.format("%.0f", p);
                            tro.setPrazo(prazo);
                        }
                        cell = row.getCell(j + 4);
                        if (cell.getCellType() == STRING){
                            tro.setSeveridade(cell.getStringCellValue());
                        }


                        tro = troService.save(tro);

                        startLocais = true;
                        break;

                    }else if(!isBlankOrHeader(cell)){
                        nIdentidade = cell.getStringCellValue();
                        nIdentidade = nIdentidade.replace(".","");
                    }
                } else if (cellType == NUMERIC && j == 0) {
                    long longNumber = new BigDecimal(cell.toString()).longValue();
                    nIdentidade = String.valueOf(longNumber);
                } else if (j == 1) {
                    Date dataCell = cell.getDateCellValue();
                    date = sdf.format(dataCell);
                } else if (j == 2) {
                    Date dataCell = cell.getDateCellValue();
                    hora = sdfHour.format(dataCell);
                } else if (j == 4) {
                    rodovia = cell.getStringCellValue().toLowerCase();
                    if (rodovia.contains("70")) {
                        rodovia = "70";
                    } else if (rodovia.contains("163")) {
                        rodovia = "163";
                    } else if (rodovia.contains("364")) {
                        rodovia = "364";
                    }
                } else if (j == 5) {
                     kmInicialDouble = cell.getNumericCellValue();
                } else if (j == 6) {
                     kmFinalDouble = cell.getNumericCellValue();

                } else if (j == 7) {
                    sentido = cell.getStringCellValue().toLowerCase();
                    sentido = sentido.startsWith("c") ? "Crescente" : "Decrescente";
                    if (sentido.equals("Crescente")){
                        if (kmInicialDouble > kmFinalDouble){
                            double temp = kmFinalDouble;
                            kmFinalDouble = kmInicialDouble;
                            kmInicialDouble = temp;
                        }
                    } else {
                        if (kmInicialDouble < kmFinalDouble){
                            double temp = kmFinalDouble;
                            kmFinalDouble = kmInicialDouble;
                            kmInicialDouble = temp;
                        }
                    }
                  kmFinal = String.format("%.3f", kmFinalDouble);
                  kmInicial = String.format("%.3f", kmInicialDouble);

                } else if (j == 8) {
                    pista = cell.getStringCellValue().toLowerCase();
                    pista = pista.contains("p") ? "1" : "2";
                } else if (j == 9){

                    String localObs = cell.getStringCellValue().toLowerCase();

                    Local local = new Local(null, tro, nIdentidade, date, hora, rodovia, kmInicial, kmFinal, sentido, pista);
                    local.setObservacao(localObs);
                    local = localService.save(local);


                    if (cell.getStringCellValue().toLowerCase().contains("tro")) {
                        cell = row.getCell(j + 1);
                        endLocais = true;
                    }
                }

            }
        }
        if (endLocais) {
            endLocais = false;
            startLocais = true;
        }
    }


    private boolean isBlankOrHeader(Cell cell) {
        if (cell.getCellType() == BLANK) {
            return true;
        } else if (cell.getCellType() == STRING) {
            if (cell.getStringCellValue().equalsIgnoreCase("ide")) {
                return true;
            }
        }
        return false;
    }


}

