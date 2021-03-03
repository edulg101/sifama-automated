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


    @Transactional
    public void inicioDigitacao() throws InterruptedException {


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");



        String password = "";

        String user = "";


        System.setProperty("webdriver.chrome.driver", DRIVERPATH);

        ChromeOptions option = new ChromeOptions();
        option.setHeadless(false);

        WebDriver driver = new ChromeDriver(option);

        WebDriverWait wait = new WebDriverWait(driver, 50);
        WebDriverWait longWait = new WebDriverWait(driver, 120000);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        Consulta consulta = new Consulta(driver, wait, js);

        System.out.println("abrindo chrome");

        driver.get("https://appweb1.antt.gov.br/fisn/Site/TRO/Cadastrar.aspx");
        System.out.println("abrindo pagina do Sifama");

        WebElement usuario = driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_TextBoxUsuario"));
        WebElement senha = driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_TextBoxSenha"));
        WebElement entrar = driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ButtonOk"));

        usuario.sendKeys(user);
        senha.sendKeys(password);

        System.out.println("entrando com senha");

        entrar.click();

        consulta.waitForProcessBar();


//        consulta.waitForJStoLoad();

        inicioTro(driver, wait, consulta, js);

    }

    public void inicioTro(WebDriver driver, WebDriverWait wait, Consulta consulta, JavascriptExecutor js) throws InterruptedException {
        List<Tro> troList = troService.getAll();
        boolean primeiro = true;
        for (Tro tro : troList) {
            Thread.sleep(700);

            if (!primeiro) {
                consulta.waitForJStoLoad();
                consulta.waitForProcessBar();
                js.executeScript("document.getElementById('MessageBox_ButtonOk').click()");
            }
            primeiro = false;
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

        List<String> artigoList = getArtigoFromPalavraChave(palavraChave);
        art = artigoList.get(0);
        tipoOcorrencia = artigoList.get(1);


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


            while (true) {
                try {
                    consulta.waitForElementById("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirLocal");
                    consulta.scriptToClick("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirLocal");
//                    driver.findElement(By.id("ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnIncluirLocal")).click();

                    break;
                } catch (org.openqa.selenium.StaleElementReferenceException | ElementClickInterceptedException e) {
                    System.out.println("tentando novamente clicar no botão incluir");
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
//                while (!waitForJStoLoad(js, longWait)) {
//                    Thread.sleep(500);
//                }

                Thread.sleep(500);
                System.out.println( "OK !");
            }
        }

        System.out.println("Salva o TRO Atual .... ");


        js.executeScript("document.getElementById('ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_ContentPlaceHolderCorpo_btnSalvar').click()");
        Thread.sleep(1000);

        consulta.waitForProcessBar();

        consulta.scriptToClick("MessageBox_ButtonOk");

        Thread.sleep(1000);

        consulta.waitForProcessBar();

        consulta.waitForProcessBar();

        consulta.waitForJStoLoad();

        consulta.waitForProcessBar();

    }

    private List<String> getArtigoFromPalavraChave(String palavraChave) {

        if (palavraChave.contains("buraco")) {
            art = "6";
            tipoOcorrencia = "774";

        } else if (palavraChave.contains("afundamen") || palavraChave.contains("escorregamento") || palavraChave.contains("remendo")) {
            art = "6";
            tipoOcorrencia = "773";

        } else if (palavraChave.contains("drenagem")) {
            art = "6";
            tipoOcorrencia = "782";

        } else if (palavraChave.contains("meio fio")) {
            art = "4";
            tipoOcorrencia = "720";

        } else if (palavraChave.contains("desplacam") || palavraChave.contains("deforma")) {
            art = "6";
            tipoOcorrencia = "774";
            prazo = "72";

        } else if (palavraChave.contains("vertical") || (palavraChave.contains("horizontal"))) {
            art = "7";
            tipoOcorrencia = "807";
            prazo = "72";
        } else if (palavraChave.contains("terrapleno") || palavraChave.contains("talude")) {
            art = "6";
            tipoOcorrencia = "783";
        } else if (palavraChave.contains("defensa")) {
            art = "7";
            tipoOcorrencia = "808";
        } else if (palavraChave.contains("instalac") || palavraChave.contains("instalaç") || palavraChave.contains("edifica")) {
            art = "5";
            tipoOcorrencia = "742";
        } else if (palavraChave.contains("pmv")){
            art = "5";
            tipoOcorrencia = "752";
        }
        else {
            System.out.println("deu merda");
        }


        List<String> list = new ArrayList<>();
        list.add(art);
        list.add(tipoOcorrencia);

        return list;
    }


    public boolean waitForJStoLoad(JavascriptExecutor js, WebDriverWait wait) {

        js.executeScript("console.log(document.readyState)");
        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long) js.executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    return true;
                }
            }
        };

        // wait for Javascript to load
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return js.executeScript("return document.readyState")
                        .toString().equals("complete");
            }
        };

        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }
}




