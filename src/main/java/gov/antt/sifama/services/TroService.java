package gov.antt.sifama.services;

import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.Tro;
import gov.antt.sifama.model.dto.LocalDto;
import gov.antt.sifama.model.dto.TroDto;
import gov.antt.sifama.repositories.TroRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TroService {

    @Autowired
    TroRepo troRepo;

    @Autowired
    LocalService localService;

    @Transactional
    public Tro save(Tro tro) {
        tro = troRepo.save(tro);
        return tro;
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
}
