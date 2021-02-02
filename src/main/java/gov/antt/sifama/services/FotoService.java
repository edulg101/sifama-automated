package gov.antt.sifama.services;

import gov.antt.sifama.model.Foto;
import gov.antt.sifama.repositories.FotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;


@Service
public class FotoService {

    @Autowired
    FotoRepo fotoRepo;

    public Foto save(Foto tro) {
        tro = fotoRepo.save(tro);
        return tro;
    }

    public Foto getById(Long id) {
        return fotoRepo.getOne(id);
    }

    public List<Foto> getAll() {
        return fotoRepo.findAll();
    }


    // This method will resize the image to the specified width and will maintain aspect ratio for the height of the picture to maintain quality
    public static String resize(String sourceFile, int width) throws Exception {
        try {
            File f = new File(sourceFile);
            if (!f.exists()) {
                return "File " + f.getAbsolutePath() + "Não existe";
            }

// Logic to implement image resizing

            BufferedImage bim = ImageIO.read(new FileInputStream(sourceFile));
            if (bim.getWidth() <= width){
                return "imagem " + sourceFile + " já compactada";
            }
            Image resizedImg = bim.getScaledInstance(width, -1, 4);
            int scaled_height = resizedImg.getHeight(null);

            BufferedImage rBimg = new BufferedImage(width, scaled_height, bim.getType());
// Create Graphics object
            Graphics2D g = rBimg.createGraphics();// Draw the resizedImg from 0,0 with no ImageObserver
            g.drawImage(resizedImg, 0, 0, null);

// Dispose the Graphics object, we no longer need it
            g.dispose();

            ImageIO.write(rBimg, sourceFile.substring(sourceFile.indexOf(".") + 1), new FileOutputStream(sourceFile))
            ;
        } catch (Exception e) {
            return e.getMessage();
        }
        return "Imagem " + sourceFile + " compactada com sucesso";
    }


    //        image.setWidth(400);
//
//        image.saveAs(fileStr);


}
