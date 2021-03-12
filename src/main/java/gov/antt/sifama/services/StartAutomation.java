package gov.antt.sifama.services;
import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.Tro;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static gov.antt.sifama.services.appConstants.AppConstants.*;


@Service
public class StartAutomation {


    @Autowired
    TroService troService;


    String art = "";
    String tipoOcorrencia = ""; // cod 774 - buracos
    String ocorrenciaDesc = "";
    String inciso = ""; // inciso buracos correspondente ao codigo 774
    String tipoTempo = "1";  // corresponde a horas
    String prazo = "";
    boolean prazoNeedToChange = false;

    final String idArtigo = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlArtigo";
    final String idTipoOcorrencia = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlTipoInfracao";
    final String idElemento = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlElementoOcorrencia";
    final String idPrazo = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_txtPrazoExecucaoOcorrencia";
    final String idTipoPrazo = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlExecucaoOcorrencia";
    final String idData = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_txtDataOcorrencia";
    final String idHora = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_txtHoraOcorrencia";
    final String idUf = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlUf";
    final String idRodovia = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlRodovia";
    final String idPista = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlPista";
    final String idSentido = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlSentido";
    final String idkmInicial = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_txtKmInicial";
    final String idKmFinal = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_txtKmFinal";
    final String idDescricaoOcorrencia = "ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_txtDescricaoOcorrencia";


    String observacao = "";
    String data = "";
    String hora = "";
    String uf = "MT";
    String rodovia = "";
    String pista = "";
    String sentido = "";
    String kmInicial = "";
    String kmFinal = "";

    int totalTro = 0;
    int actualTro = 0;


    @Transactional
    public void inicioDigitacao() throws InterruptedException {


        System.setProperty("webdriver.chrome.driver", DRIVERPATH);

        ChromeOptions option = new ChromeOptions();
        option.setHeadless(true);

        WebDriver driver = new ChromeDriver(option);

        WebDriverWait wait = new WebDriverWait(driver, 50);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        Consulta consulta = new Consulta(driver, wait, js);

        System.out.println("abrindo chrome");

        driver.get("https://appweb1.antt.gov.br/fisn/Site/TRO/Cadastrar.aspx");
        System.out.println("abrindo pagina do Sifama");

        WebElement usuario = driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_TextBoxUsuario"));
        WebElement senha = driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_TextBoxSenha"));
        WebElement entrar = driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ButtonOk"));

        usuario.sendKeys(USER);
        senha.sendKeys(PASSWORD);

        System.out.println("entrando com senha");

        entrar.click();

        consulta.waitForProcessBar();


//        consulta.waitForJStoLoad();

        inicioTro(driver, wait, consulta, js);

    }

    public void inicioTro(WebDriver driver, WebDriverWait wait, Consulta consulta, JavascriptExecutor js) throws InterruptedException {
        List<Tro> troList = troService.getAll();
        totalTro = troList.size();
        boolean primeiro = true;
        for (Tro tro : troList) {
            Thread.sleep(700);

            if (!primeiro) {
                consulta.waitForJStoLoad();
                consulta.waitForProcessBar();
                js.executeScript("document.getElementById('MessageBox_ButtonOk').click()");
            }
            primeiro = false;
            actualTro = troList.indexOf(tro) + 1;

            registroTro(tro, consulta, driver, wait, js);

        }
    }

    public void registroTro(Tro tro, Consulta consulta, WebDriver driver, WebDriverWait wait, JavascriptExecutor js) throws InterruptedException {

        consulta.waitForJStoLoad();

        consulta.waitForProcessBar();

        consulta.waitForJStoLoad();


        System.out.println("Selecionando CRO na lista");

        consulta.changeValueInSelect("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlConcessionaria", "19521322000104");

        System.out.println("Seleciona Resolução");

        consulta.jqueryScriptWithChange("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlLegislacao", "4071");


        List<Local> localList = tro.getLocais();
        String palavraChave = tro.getPalavraChave();

        String[] artigoList = getDisposicaoLegal(palavraChave);
        art = artigoList[0];
        tipoOcorrencia = artigoList[1];


        observacao = tro.getObservacao();
        observacao = observacao.substring(0, 1).toUpperCase() + observacao.substring(1);

        // verificar se data e hora não é para cada local

        data = tro.getLocais().get(0).getData();
        hora = tro.getLocais().get(0).getHora();


        System.out.println("Seleciona Artigo da Resolução");

        //Pega da planilha

        consulta.jqueryScriptWithChange(idArtigo, art);


        System.out.println("Seleciona TipoOcorrencia");


        consulta.jqueryScriptWithChange(idTipoOcorrencia, tipoOcorrencia);


        prazo = tro.getPrazo();
        consulta.enviaChaves(idPrazo, prazo);


        System.out.println("Seleciona Entre horas / dias");

        consulta.jqueryScriptWithChange(idTipoPrazo, tipoTempo);

        consulta.waitForProcessBar();

        System.out.println("informa data");

        consulta.waitToBeClickableAndClickById(idData);

        consulta.waitForProcessBar();
        consulta.enviaChaves(idData, data);
        consulta.waitForProcessBar();

        consulta.waitToBeClickableAndClickById(idHora);

        consulta.waitForProcessBar();


        System.out.println("informa Hora");


        System.out.println("insere descrição ocorrencia");

        while (true) {
            try {
                consulta.waitToBeClickableAndClickById(idDescricaoOcorrencia);
                consulta.enviaChaves(idDescricaoOcorrencia, observacao);
                break;
            } catch (org.openqa.selenium.StaleElementReferenceException | ElementClickInterceptedException e) {
                System.out.println("tentando novamente descrição");
                consulta.waitToBeClickableAndClickById(idDescricaoOcorrencia);
            }
            Thread.sleep(500);
        }

        consulta.enviaChaves(idHora, hora);

        try{
            consulta.waitToBeClickableAndClickById(idDescricaoOcorrencia);
        }catch (RuntimeException e){
            System.out.println("exception na linha 212 : nao conseguiu clicar em idDescriçãoOcorrencia");
            return;
        }

        consulta.scriptToClick(idDescricaoOcorrencia);

//        driver.findElement(By.id(idDescricaoOcorrencia)).click();
        consulta.waitForProcessBar();

        System.out.println("Insere UF");

        consulta.jqueryScript(idUf, uf);


        for (int i = 0; i < localList.size(); i++) {

            rodovia = tro.getLocais().get(i).getRodovia();
            pista = tro.getLocais().get(i).getPista();
            sentido = tro.getLocais().get(i).getSentido();
            kmInicial = tro.getLocais().get(i).getKmInicial();
            kmFinal = tro.getLocais().get(i).getKmFinal();

            System.out.println("insere rodovia");

            consulta.jqueryScript(idRodovia, rodovia);

            System.out.println("insere pista");

            consulta.jqueryScript(idPista, pista);

            System.out.println("insere sentido");

            consulta.jqueryScript(idSentido, sentido);
            consulta.waitForProcessBar();
            consulta.waitForJStoLoad();


            System.out.println("insere Km Inicial e Final");

            driver.findElement(By.id(idkmInicial)).clear();
            consulta.enviaChaves(idkmInicial, kmInicial);

            driver.findElement(By.id(idKmFinal)).clear();
            consulta.enviaChaves(idKmFinal, kmFinal);

            System.out.println("Incluindo Local .....");

            int h = 0;
            while (true) {
                h++;
                try {
                    consulta.waitForElementById("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirLocal");
                    consulta.scriptToClick("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirLocal");
//                    driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirLocal")).click();

                    break;
                } catch (org.openqa.selenium.StaleElementReferenceException | ElementClickInterceptedException e) {
                    System.out.println("tentando novamente clicar no botão incluir");
                    if(h>3){
                        return;
                    }
                }
                Thread.sleep(500);

            }
            consulta.waitForProcessBar();

        }
        int countImages = 0;
        for (int i = 0; i < localList.size(); i++) {


            kmInicial = tro.getLocais().get(i).getKmInicial();
            kmFinal = tro.getLocais().get(i).getKmFinal();

            for (int z = 0; z < tro.getLocais().get(i).getArquivosDeFotos().size(); z++) {

                consulta.changeValueInSelectByTexts("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ddlFotoLocal", kmInicial, kmFinal);

                consulta.enviaChaves("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_uplFotoLocal", IMGPATHGPS + File.separator + tro.getLocais().get(i).getArquivosDeFotos().get(z).getNome());

                countImages++;

                System.out.print("Enviando foto nº " + countImages + " .............");

                Thread.sleep(500);

                consulta.waitForElementById("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirFoto");


                consulta.scriptToClick("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirFoto");

                WebDriverWait longWait = new WebDriverWait(driver, 50000);
                Thread.sleep(500);

                consulta.waitForProcessBar();
//

                Thread.sleep(500);
                System.out.println( "OK !");
            }
        }

        System.out.print("Salva o TRO " + actualTro + " / " + totalTro + " ........... ");


        js.executeScript("document.getElementById('ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnSalvar').click()");
        Thread.sleep(1000);

        consulta.waitForProcessBar();

        consulta.scriptToClick("MessageBox_ButtonOk");

        Thread.sleep(1000);

        consulta.waitForProcessBar();

        consulta.waitForProcessBar();

        consulta.waitForJStoLoad();

        consulta.waitForProcessBar();

        System.out.print("OK ");
        System.out.println();

    }

    private String[]  getDisposicaoLegal(String palavraChave) {

        if (palavraChave.contains("buraco")) {
            return new String[]{"6", "774"};
        } else if (palavraChave.contains("afundamen") || palavraChave.contains("escorregamento") || palavraChave.contains("remendo")) {
            return new String[] {"6", "773"};
        } else if (palavraChave.contains("drenagem")) {
            return new String[] {"6", "782"};
        } else if (palavraChave.contains("meio fio") || palavraChave.contains("meio-fio")) {
            return new String[] {"4", "720"};
        } else if (palavraChave.contains("desplacam") || palavraChave.contains("deformaç")) {
            return new String[] {"6", "774"};
        } else if (palavraChave.contains("vertical") || (palavraChave.contains("horizontal"))) {
            return new String[] {"7", "807"};
        } else if (palavraChave.contains("terrapleno") || palavraChave.contains("talude")) {
            return new String[] {"6", "783"};
        } else if (palavraChave.contains("defensa")) {
            return new String[] {"7", "808"};
        } else if (palavraChave.contains("instalac") || palavraChave.contains("instalaç") || palavraChave.contains("edifica")) {
            return new String[] {"5", "742"};
        } else if (palavraChave.contains("pmv")){
            return new String[] {"5", "752"};
        } else if (palavraChave.contains("guarda corpo") || palavraChave.contains("guarda-corpo")){
            return new String[] {"7", "811"};
        } else if (palavraChave.contains("sujeira")){
            return new String[] {"7", "806"};
        }

        Map<String, String[]> disposicaoLegal = new HashMap<>();


        disposicaoLegal.put("4-v", new String[] {"4", "718"});
        disposicaoLegal.put("4-vi", new String[] {"4", "719"});
        disposicaoLegal.put("4-vii", new String[] {"4", "720"});
        disposicaoLegal.put("4-xii", new String[] {"4", "725"});
        disposicaoLegal.put("4-xiii", new String[] {"4", "726"});

        disposicaoLegal.put("5-iii", new String[] {"5", "742"});
        disposicaoLegal.put("5-v", new String[] {"5", "744"});
        disposicaoLegal.put("5-ix", new String[] {"5", "748"});
        disposicaoLegal.put("5-xii", new String[] {"5", "751"});
        disposicaoLegal.put("5-xiii", new String[] {"5", "752"});
        disposicaoLegal.put("5-xiv", new String[] {"5", "753"});
        disposicaoLegal.put("5-xv", new String[] {"5", "754"});
        disposicaoLegal.put("5-xxviii", new String[] {"5", "767"});


        disposicaoLegal.put("6-iii", new String[] {"6", "773"});
        disposicaoLegal.put("6-iv", new String[] {"6", "774"});
        disposicaoLegal.put("6-v", new String[] {"6", "775"});
        disposicaoLegal.put("6-vii", new String[] {"6", "777"});
        disposicaoLegal.put("6-viii", new String[] {"6", "778"});
        disposicaoLegal.put("6-x", new String[] {"6", "780"});
        disposicaoLegal.put("6-xi", new String[] {"6", "781"});
        disposicaoLegal.put("6-xii", new String[] {"6", "782"});
        disposicaoLegal.put("6-xiii", new String[] {"6", "783"});
        disposicaoLegal.put("6-xiv", new String[] {"6", "784"});
        disposicaoLegal.put("6-xvi", new String[] {"6", "786"});
        disposicaoLegal.put("6-xvii", new String[] {"6", "787"});
        disposicaoLegal.put("6-xxviii", new String[] {"6", "798"});

        disposicaoLegal.put("7-viii", new String[] {"7", "806"});
        disposicaoLegal.put("7-ix", new String[] {"7", "807"});
        disposicaoLegal.put("7-x", new String[] {"7", "808"});
        disposicaoLegal.put("7-xii", new String[] {"7", "810"});
        disposicaoLegal.put("7-xiii", new String[] {"7", "811"});

        disposicaoLegal.put("8-vii", new String[] {"8", "864"});

        disposicaoLegal.put("9-vii", new String[] {"9", "863"});

        return disposicaoLegal.get(palavraChave);
    }

}

//art 4
//        718	V - deixar selagem em juntas de pavimento rígido ou trincas em desconformidade com o PER, por prazo superior a 72 (setenta e duas) horas, ou conforme prazo diverso previsto no Contrato de Concessão ou no PER
//        719	VI - deixar de manter marcos quilométricos ou mantê-los em más condições de visibilidade, por prazo superior a 7 (sete) dias, ou conforme prazo diverso previsto no Contrato de Concessão ou no PER
//        720	VII - deixar meios-fios danificados, deteriorados ou ausentes por prazo superior a 72 (setenta e duas) horas, ou conforme prazo diverso previsto no Contrato de Concessão ou no PER
//        725	XII - deixar barreira de concreto de Obra-de-Arte Especial - OAE sem pintura por prazo superior a 72 (setenta e duas) horas, ou conforme prazo diverso previsto no Contrato de Concessão ou no PER
//        726	XIII - deixar armaduras de OAE sem recobrimento por prazo superior a 48 (quarenta e oito horas)

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






