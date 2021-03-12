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
    FotoService fotoService;


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

        int index = fullText.indexOf("Codigos");

        fullText = fullText.substring(0, index);


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
                j++;

                if (word.toLowerCase().contains("de ident.")) {
                    break;
                }

                if (word.toLowerCase().contains("tro")) {
                    palavraChave = words.get(j + 1);
                    tro = new Tro(null, palavraChave);
                    observacao = words.get(j + 2);
                    tro.setObservacao(observacao);
                    prazo = words.get(j + 3);
                    tro.setPrazo(prazo);
                    tro.setSeveridade(words.get(j + 4));
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
                        try{
                            tempDate = sdDayAndMinute.parse(word);
                        }catch (ParseException e){
                            System.out.println("data sem hora");
                        }
                        Date tempDate1 = sdfUSA.parse(word);
                        word = sdfBr.format(tempDate1);
                        date = word;
                    } else if (j == 3) {
                        if (word.isEmpty()){
                            hora = sdfHour.format(tempDate);
                        }else {
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
                        } else if(sentido.equals("Decrescente")) {
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
                        local.setObservacao(word);
                        local = localService.save(local);
                    }

                    if (word.toLowerCase().contains("tro") && !endLocais) {
                        endLocais = true;

                    }
                }
            }
            j = -1;
        }
    }
}
