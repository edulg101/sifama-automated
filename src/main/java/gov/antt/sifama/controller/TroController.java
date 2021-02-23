package gov.antt.sifama.controller;



import gov.antt.sifama.model.Foto;
import gov.antt.sifama.model.Local;
import gov.antt.sifama.model.Tro;
import gov.antt.sifama.model.dto.TroDto;
import gov.antt.sifama.repositories.FotoRepo;
import gov.antt.sifama.repositories.LocalRepo;
import gov.antt.sifama.services.StartAutomation;
import gov.antt.sifama.services.TroService;
import javaxt.io.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static gov.antt.sifama.services.appConstants.AppConstants.IMGPATH;


@RestController
@CrossOrigin(origins="*", allowedHeaders ="*")
@RequestMapping(value = "/tros")
public class TroController {

    @Autowired
    TroService service;

    @Autowired
    FotoRepo fotoRepo;

    @Autowired
    LocalRepo localRepo;


    @GetMapping
    public ResponseEntity<List<TroDto>> getAll() {
        List<TroDto> obj = service.getTroDto();
        return ResponseEntity.ok().body(obj);
    }

    @RequestMapping(value = "/foto/{id}", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer id)  {

        Foto foto = fotoRepo.findById(id).orElse(null);
        assert foto != null;
        String nome = foto.getNome();

        System.out.println(IMGPATH + File.separator + nome);

        Image img = new Image(IMGPATH + File.separator + nome);


        byte[] bit = img.getByteArray();
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(bit);
    }

    @GetMapping (value = "/fotosbylocal/{id}")
    public ResponseEntity<List<Integer>> getFotosInLocal(@PathVariable("id") Integer id) {
        Local local = localRepo.findById(id).orElse(null);
        assert local != null;
        List<Foto> listaFotos = local.getArquivosDeFotos();
        List<Integer> listaFotosId = new ArrayList<>();
        for (Foto foto: listaFotos){
            listaFotosId.add(foto.getId());
        }
        return ResponseEntity.ok().body(listaFotosId);
    }

    @GetMapping (value = "/iniciadigitacao")
    public ResponseEntity<List<TroDto>> iniciaDigitacao() {
        service.startDigitacao();
        List<TroDto> obj = service.getTroDto();
        return ResponseEntity.ok().body(obj);
    }

    @GetMapping (value = "/startOver")
    public ResponseEntity<List<TroDto>> startOver() {
        service.startOver();
        List<TroDto> obj = service.getTroDto();
        return ResponseEntity.ok().body(obj);
    }
}
