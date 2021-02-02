package gov.antt.sifama.services;

import gov.antt.sifama.model.Tro;
import gov.antt.sifama.repositories.TroRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TroService {

    @Autowired
    TroRepo troRepo;

    @Transactional
    public Tro save(Tro tro) {
        tro = troRepo.save(tro);
        return tro;
    }

    public List<Tro> getAll(){
        return troRepo.findAll();
    }
}
