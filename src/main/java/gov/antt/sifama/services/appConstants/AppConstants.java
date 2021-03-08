package gov.antt.sifama.services.appConstants;


import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public abstract class AppConstants {

    private static final String operatinSystemSystem = System.getProperty("os.name");
    private static final String ops = "ops";

    public static final Scanner SCANNER = getScanner();
    public static final String PASSWORD = "";
    public static final String USER = "";

    public static final String DRIVERPATH = getDriverpath();
    public static final String ORIGINIMAGESFOLDER = "/home/eduardo/Documentos/projetos/sifamaSources/imageszipped";
    public static final String IMGPATHGPS = getImageGpsPath();
    public static final String IMGPATH = getImagePath();
    public static final String SPREADSHEETPATH = getSpreadsheetPath();


    private static String getDriverpath(){
        if (operatinSystemSystem.toLowerCase().contains("linux")){
            return "/home/eduardo/automation/chromedriver";
        } else if(operatinSystemSystem.toLowerCase().contains("windows")){
            return "D:\\chromedriver.exe";
        }
        return "Error";
    }

    private static String getImagePath(){
        if (operatinSystemSystem.toLowerCase().contains("linux")){
            return "/home/eduardo/Documentos/projetos/sifamaSources/images";
        } else if(operatinSystemSystem.toLowerCase().contains("windows")){
            return "D:\\sifamadocs\\imagens";
        }
        return "Error";
    }

    private static String getImageGpsPath(){
        if (operatinSystemSystem.toLowerCase().contains("linux")){
            return "/home/eduardo/Documentos/projetos/sifamaSources/imageGPS";
        } else if(operatinSystemSystem.toLowerCase().contains("windows")){
            return "D:\\sifamadocs\\imagensGPS";
        }
        return "Error";
    }

    private static String getSpreadsheetPath(){
        if (operatinSystemSystem.toLowerCase().contains("linux")){
            return "/home/eduardo/Documentos/projetos/sifamaSources/tros.xlsx";
        } else if(operatinSystemSystem.toLowerCase().contains("windows")){
            return "D:\\sifamadocs\\planilha\\tros.xlsx";
        }
        return "Error";
    }

    private static Scanner getScanner(){

        if (operatinSystemSystem.toLowerCase().contains("linux")){
            Charset charsetUTF = StandardCharsets.UTF_8;
            return new Scanner( new InputStreamReader(System.in, charsetUTF));
        } else if(operatinSystemSystem.toLowerCase().contains("windows")){
            Charset charsetIBM = Charset.forName("IBM850");
            return new Scanner( new InputStreamReader(System.in, charsetIBM));
        }
        return null;
    }



    // Art 5

    //      742	III - deixar de executar os serviços de conservação das instalações, áreas operacionais e bens vinculados à concessão por prazo superior a 72 horas após a ocorrência de evento que comprometa suas condições normais de uso e a integridade do bem
    //      744	V - deixar de remover, da faixa de domínio, material resultante de poda, capina ou obras no prazo de 48 (quarenta e oito) horas, salvo no caso de materiais reaproveitáveis ou de bota-foras autorizados pela ANTT
    //      748	IX - deixar de repor ou manter tachas, tachões e balizadores refletivos danificados ou ausentes no prazo de 72 (setenta e duas) horas
    //      751	XII - deixar de adotar medidas, ainda que provisórias, para reparação de cercamento nas áreas operacionais por prazo superior a 24 (vinte e quatro) horas
    //      752	XIII - deixar de adotar medidas, ainda que provisórias, para reparar painel de mensagem variável inoperante ou em condições que não permitam a transmissão de informações aos usuários, por prazo superior a 72 (setenta e duas) horas
    //      753	XIV - deixar de adotar medidas, ainda que provisórias para reparação das cercas limítrofes da faixa de proteção e de seus aceiros por prazo superior a 72 (setenta e duas) horas
    //      754	XV - deixar de adotar medidas, ainda que provisórias, para corrigir falha em sistema ou equipamento dos postos de pesagem no prazo de 24 (vinte e quatro) horas ou de acordo com o especificado no Contrato e/ou PER, se este fizer referência diversa
    //      767	XXVIII - deixar de adotar providências para corrigir desnível entre faixas contíguas, ainda que em caráter provisório, no prazo de 24 (vinte e quatro) horas, ou, deixar de implementar a solução definitiva para correção no prazo estabelecido pela ANTT


    //Art 6:

    //       773	III - deixar de corrigir depressões, abaulamentos (escorregamentos de massa asfáltica) ou áreas exsudadas na pista ou no acostamento, no prazo de 72 (setenta e duas) horas, ou conforme previsto no Contrato de Concessão e/ou PER
    //      774	IV - deixar de corrigir/tapar buracos, panelas na pista ou no acostamento, no prazo de 24 (vinte e quatro) horas, ou conforme previsto no Contrato de Concessão e/ou PER
    //      775	V - deixar de corrigir, no pavimento rígido, defeitos com grau de severidade alto, no prazo de 7 (sete) dias, ou conforme previsto no Contrato de Concessão e/ou PER
    //      777	VII - deixar de corrigir, no pavimento rígido, defeitos de alçamento de placa, fissura de canto, placa dividida (rompida), escalonamento ou degrau, placa bailarina, quebras localizadas e buracos no prazo de 48 (quarenta e oito) horas, ou conforme previsto no Contrato de Concessão e/ou PER
    //      778	VIII - deixar de manter ou manter de forma não visível pelos usuários sinalização (vertical ou aérea) de indicação, de serviços auxiliares ou educativas, por prazo superior a 7 (sete) dias
    //      780	X - deixar de manter ou manter de forma não funcional dispositivo anti-ofuscante por prazo superior a 7 (sete) dias, ou conforme previsto no Contrato de Concessão ou no PER
    //      781	XI - deixar com problemas de conservação elemento de OAE, exceto guarda-corpo, por prazo superior a 30 (trinta) dias ou conforme Contrato de Concessão e/ou PER
    //      782	XII - deixar de reparar, limpar ou desobstruir sistema de drenagem e Obra-de-Arte Corrente-OAC por prazo superior a 72 (setenta e duas) horas, ou conforme previsto no Contrato de Concessão ou no PER
    //      783	XIII - deixar de adotar providências para solucionar, ainda que de modo provisório, processo erosivo ou condição de instabilidade em talude, por prazo superior a 72 (setenta e duas) horas, ou deixar de implementar solução definitiva no prazo estabelecido pela ANTT
    //      784	XIV - deixar de manter ou manter de forma não funcional o sistema de iluminação da rodovia, por prazo superior a 48 (quarenta e oito) horas
    //      786	XVI - deixar de corrigir falha em equipamento de praça de pedágio no prazo de 6 (seis) horas, sem prejuízo ao atendimento dos parâmetros de desempenho estabelecidos no PER
    //      787	XVII - deixar "Call Box" inoperante por prazo superior a 24 (vinte e quatro) horas, ou de acordo com o especificado no PER, se este fizer referência diversa
    //      798	XXVIII - deixar de intervir, mesmo que provisoriamente, em recalque em pavimento na cabeceira de OAE e/ou OAC por prazo superior a 72 (setenta e duas) horas, desde que essa obrigação tenha sido prevista no Contrato de Concessão ou PER



    //Art 7

    //        806	VIII - deixar de remover material da(s) faixa(s) de rolamento( s) ou acostamento(s) que obstrua ou comprometa a correta fluidez do tráfego no prazo de 6 (seis) horas a partir do evento que lhe deu origem
    //          807	IX - deixar de manter ou manter a sinalização horizontal, vertical ou aérea, em desconformidade com as normas técnicas vigentes, por prazo superior ao estabelecido pela ANTT, excluídas as ocorrências previstas nos artigos 5°, 6° e 9°
    //        808	X - deixar de recompor barreira rígida ou defensa metálica danificada no prazo de 48 horas
    //          810	XII - deixar de intervir para restaurar a funcionalidade de elemento da rodovia quando da ocorrência de fatos oriundos da ação de terceiros ou de eventos da natureza que possam colocar em risco a segurança do usuário, no prazo de 48 (quarenta e oito) horas ou conforme estabelecido pela ANTT
    //          811	XIII - deixar de recuperar, ainda que provisoriamente, guarda- corpo de OAE, inclusive passarela, por prazo superior a 24 (vinte e quatro) horas, ou, deixar de efetuar sua reposição definitiva, por prazo superior a 72 (setenta e duas) horas, ou conforme Contrato e/ou PER


    // Art 8

//        864	VII - deixar de adotar as providências cabíveis, inclusive por vias judiciais, para garantia do patrimônio da rodovia, da faixa de domínio, das edificações e dos bens da concessão, inclusive quanto à implantação de acessos irregulares e ocupações ilegais; Nos casos de constatação destas irregularidades para as concessões da 2ª etapa, há previsão contratual de prazo de 24 (vinte e quatro) horas para a correção. Deste modo, deverá ser expedido TRO enquadrado neste mesmo Art. 8º, inciso VII, da Re</option>


    // Art 9

//        863	VII - deixar de manter ou manter sinalização vertical de regulamentação em desconformidade com as normas técnicas vigentes, por prazo superior ao previsto no Contrato de Concessão ou no PER




















}
