package gov.antt.sifama.services;

import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.Tro;
import gov.antt.sifama.repositories.LocalRepo;
import gov.antt.sifama.repositories.TroRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;



@Service
public class LocalService {

    @Autowired
    LocalRepo localRepo;

    public Local save(Local tro) {
        tro = localRepo.save(tro);
        return tro;
    }

    public List<Local> getAll(){
        return localRepo.findAll();
    }
}
