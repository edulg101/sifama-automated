package gov.antt.sifama.services;

import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.dto.LocalDto;
import gov.antt.sifama.repositories.LocalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class LocalService {

    @Autowired
    LocalRepo localRepo;

    public Local save(Local local) {
        local = localRepo.save(local);
        return local;
    }



    public void deleteAll(){
        localRepo.deleteAll();
    }

    public List<Local> getAll(){
        return localRepo.findAll();
    }

    public LocalDto localToDto(Local local){
        Integer id = local.getId();
        String rodovia = local.getRodovia();
        String kmInicial = local.getKmInicial();
        String kmFinal = local.getKmFinal();
        String sentido = local.getSentido();
        Integer troId = local.getTro().getId();
        return new LocalDto(id, rodovia, kmInicial, kmFinal, sentido, troId);

    }

    public Local getLocalById(int id){
        return localRepo.findById(id).orElseThrow();

    }

}


