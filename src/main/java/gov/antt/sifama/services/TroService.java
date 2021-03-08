package gov.antt.sifama.services;

import gov.antt.sifama.config.h2Config;
import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.Tro;
import gov.antt.sifama.model.dto.LocalDto;
import gov.antt.sifama.model.dto.TroDto;
import gov.antt.sifama.repositories.TroRepo;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static gov.antt.sifama.services.appConstants.AppConstants.*;

@Service
public class TroService {

    @Autowired
    TroRepo troRepo;

    @Autowired
    LocalService localService;

    @Autowired
    StartAutomation startAutomation;

    @Autowired
    FotoService fotoService;

    @Autowired
    ImportFromExcel ie;

    @Autowired
    ImportExcelTika tika;

    @Autowired
    h2Config h2Config;

    @Transactional
    public Tro save(Tro tro) {
        tro = troRepo.save(tro);
        return tro;
    }

    public void deleteAll(){
        troRepo.deleteAll();

    }

    public void startOver() throws Exception {
        fotoService.deleteAll();
        localService.deleteAll();
        deleteAll();

        h2Config.inicio();



    }

    public List<Tro> getAll(){
        return troRepo.findAll();
    }

    public List<TroDto> getTroDto(){
        List<TroDto> dtoList = new ArrayList<>();
        List<Tro> troList = getAll();
        List<LocalDto> localDtoList = new ArrayList<>();
        for(Tro tro: troList){
            Integer id = tro.getId();
            String obs = tro.getObservacao();
            String prazo = tro.getPrazo();
            TroDto troDto = new TroDto(id, obs, prazo);
            List <Local> localList = localService.getAll();
            for(Local local: localList) {
                if(local.getTro().getId().equals(tro.getId())){
                    troDto.getLocalList().add(localService.localToDto(local));
                }
            }

            dtoList.add(troDto);
        }
        return dtoList;
    }

    public void startDigitacao(){
        try {
            startAutomation.inicioDigitacao();
        } catch (InterruptedException e) {
            System.out.println("não conseguiu entrar na digitação. deu merda");;
            System.exit(-1);
        }
    }

    public void deleteAllBD(){

    }
}
