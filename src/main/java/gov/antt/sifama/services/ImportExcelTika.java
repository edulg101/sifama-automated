package gov.antt.sifama.services;

import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.Tro;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.microsoft.ooxml.OOXMLParser;
import org.apache.tika.sax.BodyContentHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class ImportExcelTika {

    SimpleDateFormat sdfUSA = new SimpleDateFormat("MM/dd/yy");
    SimpleDateFormat sdfBr = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdfHour = new SimpleDateFormat("HH:mm");
    SimpleDateFormat sdDayAndMinute = new SimpleDateFormat("MM/dd/yy HH:mm");

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
    StartAutomation startAutomation;


    public ImportExcelTika() {
    }


    @Transactional
    public String readexcel(String fileName) throws TikaException, SAXException, IOException {

        //detecting the file type
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        FileInputStream inputstream = new FileInputStream(new File(fileName));
        ParseContext pcontext = new ParseContext();

        //OOXml parser
        OOXMLParser msofficeparser = new OOXMLParser();
        msofficeparser.parse(inputstream, handler, metadata, pcontext);
        inputstream.close();
        return handler.toString();

    }

    public void parseExcel(String filePath) throws IOException, TikaException, SAXException, ParseException {
        String fullText = readexcel(filePath);

        System.out.println(fullText);

        int index = fullText.indexOf("Codigos");

        fullText = fullText.substring(0, index);

        System.out.println(fullText);


        String[] lineArray = fullText.split("\n");
        List<String> lines = new ArrayList<>(Arrays.asList(lineArray));
        lines.remove(0);

        Tro tro = null;
        double kmInicialDouble = 0;
        double kmFinalDouble = 0;
        boolean startLocais = false;
        boolean endLocais = false;
        int i = -1;
        int j = -1;
        Date tempDate = null;

        for (String line : lines) {
            i++;

            String[] wordsArray = line.split("\t");
            List<String> words = new ArrayList<>(Arrays.asList(wordsArray));

            for (String word : words) {
                word = word.trim();
                j++;

                System.out.printf("Linha - Coluna : %d - %d : %s\n ", i + 1, j, word);

                if (word.toLowerCase().contains("de ident.")) {
                    break;
                }


                if (word.toLowerCase().equalsIgnoreCase("tro")) {
                    palavraChave = words.get(j + 1);
                    tro = new Tro(null, palavraChave);
                    observacao = words.get(j + 2);
                    tro.setObservacao(observacao);
                    prazo = words.get(j + 3);
                    tro.setPrazo(prazo);

                    try {
                        tro.setSeveridade(words.get(j + 4));
                    } catch (IndexOutOfBoundsException e) {
                        tro.setSeveridade(null);
                    }
                    tro = troService.save(tro);
                    startLocais = true;
                    endLocais = false;
                    break;
                }
                if (startLocais) {
                    if (j == 1) {
                        nIdentidade = word;
                        nIdentidade = nIdentidade.replace(".", "");
                    } else if (j == 2) {
                        if (words.get(j + 1) == null || words.get(j + 1).isEmpty()) {
                            try {
                                tempDate = sdDayAndMinute.parse(word);
                                date = sdfBr.format(tempDate);
                            } catch (ParseException e) {
                                System.out.println("teste");
                            }
                        } else {
                            Date tempDate1 = sdfUSA.parse(word);
                            word = sdfBr.format(tempDate1);
                            date = word;
                        }

                    } else if (j == 3) {
                        if (word.isEmpty()) {
                            hora = sdfHour.format(tempDate);
                        } else {
                            tempDate = sdfHour.parse(word);
                            hora = sdfHour.format(tempDate);
                        }
                    } else if (j == 5) {
                        if (word.contains("70")) {
                            rodovia = "70";
                        } else if (word.contains("163")) {
                            rodovia = "163";
                        } else if (word.contains("364")) {
                            rodovia = "364";
                        }
                    } else if (j == 6) {
                        kmInicial = word;
                    } else if (j == 7) {
                        kmFinal = word;
                    } else if (j == 8) {

                        sentido = word.toLowerCase().startsWith("c") ? "Crescente" : "Decrescente";

                        kmInicialDouble = Double.parseDouble(kmInicial.replace(",", "."));
                        kmFinalDouble = Double.parseDouble(kmFinal.replace(",", "."));

                        if (sentido.equals("Crescente")) {
                            if (kmInicialDouble > kmFinalDouble) {
                                double temp = kmFinalDouble;
                                kmFinalDouble = kmInicialDouble;
                                kmInicialDouble = temp;
                            }
                        } else if (sentido.equals("Decrescente")) {
                            if (kmInicialDouble < kmFinalDouble) {
                                double temp = kmFinalDouble;
                                kmFinalDouble = kmInicialDouble;
                                kmInicialDouble = temp;
                            }
                        }

                        kmFinal = String.format("%.3f", kmFinalDouble);
                        kmInicial = String.format("%.3f", kmInicialDouble);

                    } else if (j == 9) {
                        pista = word.toLowerCase().contains("p") ? "1" : "2";

                    } else if (j == 10) {

                        Local local = new Local(null, tro, nIdentidade, date, hora, rodovia, kmInicial, kmFinal, sentido, pista);
                        local.setKmFinalDouble(kmFinalDouble);
                        local.setKmInicialDouble(kmInicialDouble);
                        local.setObservacao(word);

                        if (!isLocalValid(local)) {
                            System.out.printf("Local fora do trecho : Rodovia %s Km %f\n", rodovia, kmInicialDouble);
                            System.exit(-1);
                        } else if (!isLocalValid(local)) {
                            System.out.printf("Local fora do trecho : Rodovia %s Km %f\n", rodovia, kmFinalDouble);
                            System.exit(-1);
                        } else {

                            local = localService.save(local);
                            tro.getLocais().add(local);
                            troService.save(tro);
                        }


                    }

                    if (word.toLowerCase().contains("tro") && !endLocais) {
                        endLocais = true;

                    }
                }
            }
            j = -1;
        }
        checkForDuplicateTime();
    }

    private void checkForDuplicateTime() {
        List<Tro> troList = troService.getAll();

        System.out.println("lista de tros");
        for (Tro tro : troList) {

            System.out.println(tro.getId());
            System.out.println(tro.getLocais().toString());
        }
        List<Local> localList = localService.getAll();
        for (Local local : localList) {

            System.out.println(local.getId());
            System.out.println(local.getTro().getId());
        }

        List<Object[]> list = new ArrayList<>();
        String data = "";
        String hora = "";
        int localId = 0;

        for (Tro tro : troList) {
            try {
                data = tro.getLocais().get(0).getData();
                hora = tro.getLocais().get(0).getHora();
                localId = tro.getLocais().get(0).getId();
            } catch (IndexOutOfBoundsException e) {
                System.out.printf("troId: %d. Tro texto: %s \n",
                        tro.getId(), tro.getObservacao());
                e.printStackTrace();
                System.exit(-1);
            }
            Object[] objects = new Object[3];
            objects[0] = localId;
            objects[1] = data;
            objects[2] = hora;

            list.add(objects);
        }

        int match = getLocalIdWithDuplicated(list);
        while (match != -1) {
            Local local = localService.getLocalById((int) list.get(match)[0]);
            double random = Math.random();
            int randomInt = (int) (random * 100 / 2);
            String newMinutes = String.format("%02d", randomInt);
            String oldHora = local.getHora();
            String horaCheia = oldHora.substring(0, 3);
            local.setHora(horaCheia + newMinutes);
            local = localService.save(local);
            list.get(match)[2] = horaCheia + newMinutes;
            match = getLocalIdWithDuplicated(list);
        }
    }

    private int getLocalIdWithDuplicated(List<Object[]> list) {

        int i = 0;
        int j = 0;
        for (i = 0; i < list.size(); i++) {
            for (j = i + 1; j < list.size(); j++) {
                String dataI = (String) list.get(i)[1];
                String dataJ = (String) list.get(j)[1];
                String horaI = (String) list.get(i)[2];
                String horaJ = (String) list.get(j)[2];

                if (dataI.equals(dataJ) && horaI.equals(horaJ)) {
                    return j;
                }
            }
        }
        return -1;
    }

    private boolean isLocalValid(Local local) {

        double kmInicial = local.getKmInicialDouble();
        double kmFinal = local.getKmFinalDouble();
        String palavraChave = local.getTro().getPalavraChave();
        boolean initialKmChanged = false;
        if (local.getRodovia().contains("364") && local.getSentido().equalsIgnoreCase("Decrescente")) {
            if (kmInicial > 0 && kmInicial < 19) {
                double newKmInicial = interpolation(kmInicial);
                String kmInicialStr = String.format("%.3f", newKmInicial);
                initialKmChanged = true;
                local.setKmInicialDouble(newKmInicial);
                local.setKmInicial(kmInicialStr);
                local.setObservacao(local.getObservacao() + " - Marco Quilométrico: " + String.format("%.3f",kmInicial));
            }
            if (kmFinal > 0 && kmFinal < 19){
                double newKmFinal = interpolation(kmFinal);
                String kmFinalStr = String.format("%.3f", newKmFinal);
                local.setKmFinal(kmFinalStr);
                local.setKmFinalDouble(newKmFinal);
                if (initialKmChanged){
                    local.setObservacao(local.getObservacao() + " - " + String.format("%.3f",kmFinal));
                } else {
                    local.setObservacao(local.getObservacao() + " - Marco Quilométrico: " + String.format("%.3f",kmFinal));
                }
            }

        }

        kmInicial = local.getKmInicialDouble();
        kmFinal = local.getKmFinalDouble();

        boolean checkkmInicial = checkKm(kmInicial, palavraChave);
        boolean checkkmFinal = checkKm(kmFinal, palavraChave);

        return checkkmInicial && checkkmFinal;
    }

    private boolean checkKm(double km, String palavraChave){

        String[] disposicao = startAutomation.getDisposicaoLegal(palavraChave);
        boolean edificacoes = false;

        if (disposicao[0].equals("5") && disposicao[1].equals("742")) {
            edificacoes = true;
        }

        switch (rodovia) {
            case "70":
                if (km < 495.9 || km > 524) {
                    return false;
                }
                return true;

            case "163":
                return (!(km > 119.9) || !(km < 507.1)) && (!(km > 855));

            case "364":
                 if (km < 201 || km > 588.2) {
                    return false;
                } else if (km >= 201 && km <= 230) {
                    return true;
                } else if (km >= 277 && km <= 360) {
                    return true;
                } else if (km >= 277 && km <= 304) {
                    return true;
                } else if (km >= 434.6 && km <= 588.2) {
                    return true;
                }

                if (edificacoes) {
                    if ((km >= 211.3 && km <= 402.4) ||
                            (km >= 434.6 && km <= 588.2)) {
                        return true;
                    }
                }
                return false;

        }
        return false;
    }

    public static double interpolation(double km){

           double newKmInicial = (km * 17) / 19 + 343.100;
            return newKmInicial;

    }
}



