package gov.antt.sifama.services;

import gov.antt.sifama.model.Foto;
import gov.antt.sifama.repositories.FotoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static gov.antt.sifama.services.appConstants.AppConstants.IMGPATH;


@Service
public class FotoService {

    public static final int BUFFER_SIZE = 4096;

    @Autowired
    FotoRepo fotoRepo;

    public void deleteAll() {
        fotoRepo.deleteAll();
    }

    public Foto save(Foto tro) {
        tro = fotoRepo.save(tro);
        return tro;
    }

    public Foto getById(Integer id) {
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

            int imgWidth = bim.getWidth();
            int imgHeight = bim.getHeight();

            boolean isLandscape = imgWidth > imgHeight;


            if (isLandscape) {
                if (imgWidth <= width * 1.25) {
                    return "imagem " + sourceFile + " já compactada";
                }
            } else {
                if (bim.getWidth() <= width) {
                    return "imagem " + sourceFile + " já compactada";
                }
            }
            BufferedImage rBimg = null;
            Image resizedImg = null;

            if (isLandscape) {
                resizedImg = bim.getScaledInstance((int) (width * 1.25), -1, 4);
                int scaled_height = resizedImg.getHeight(null);

                rBimg = new BufferedImage((int) (width * 1.25), scaled_height, bim.getType());
            } else {
                resizedImg = bim.getScaledInstance(width, -1, 4);
                int scaled_height = resizedImg.getHeight(null);

                rBimg = new BufferedImage(width, scaled_height, bim.getType());
            }
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

    // unzip files to directory (same directory or subfolders. check)

    public void unzipAllDirectory(String path, String destPath) throws IOException, InterruptedException {
        String[] files = null;
        try {
            files = getFilesInPath(path);


        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < files.length; i++) {
            if (!files[i].contains(".zip")) {
                continue;
            }
            //change to unzip for unzip in subfolders
            unzipFilesToSameDirectory(path + File.separator + files[i], destPath);
        }
    }

    public String[] getFilesInPath(String path) throws Exception {
        File file = null;
        String[] files = null;

        try {
            file = new File(path);
            files = file.list();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return files;
    }


    private void unzip(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        Charset charset = Charset.forName("IBM850");

        FileInputStream nfis = new FileInputStream(zipFilePath);
        ZipInputStream zipIn = new ZipInputStream(nfis, charset);

        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {


            String fileName = entry.getName();

            while (fileName.contains(File.separator)) {
                int index = fileName.indexOf(File.separator);
                fileName = fileName.substring(index + 1);
                if (fileName.equals("")) {
                    continue;
                }

            }

            if (!fileName.contains(".")) {
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
                continue;
            }
            int index = fileName.indexOf(".");
            fileName = fileName.substring(0, index) + "_" + fileName.substring(index);

            String filePath = destDirectory + File.separator + fileName;


            extractFile(zipIn, filePath);

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    public String nameShorterner(String name) {

        if (name.contains("MT")) {

            int index = name.indexOf("MT");
            name = name.substring(0, index) + name.substring(name.indexOf("km") - 1);
        }

        return name;
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        File parentFile = new File(filePath).getParentFile();
        if (parentFile != null && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int len = 0;
        while ((len = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, len);
        }
        bos.close();
    }

//    private void unzipFilesToSameDirectory(String zipFilePath, String destDirectory) throws IOException {
//        File destDir = new File(destDirectory);
//        if (!destDir.exists()) {
//            destDir.mkdir();
//        }
//        Charset charset = Charset.forName("IBM850");
//
//        FileInputStream nfis = new FileInputStream(zipFilePath);
//        ZipInputStream zipIn = new ZipInputStream(nfis, charset);
//        ZipEntry entry = zipIn.getNextEntry();
//
//        while (entry != null) {
//
//            String fileName = entry.getName();
//
//            String filePath = destDirectory + File.separator + fileName;
//
//            System.out.println(fileName);
//            while (fileName.contains("/")) {
//                int index = fileName.indexOf("/");
//                fileName = fileName.substring(index + 1);
//
//                String shortenedName = nameShorterner(fileName);
//
//                if(shortenedName.length() > 1){
//                    fileName = shortenedName;
//                }
//
//                int index1 = fileName.indexOf(".");
//                fileName = fileName.substring(0, index1) + "_" + fileName.substring(index1);
//
//
//                fotoRepo.save(new Foto(null, fileName));
//
//
//                System.out.println(fileName);
//                if (fileName.equals("")) {
//                    continue;
//                }
//                filePath = destDirectory + File.separator + fileName;
//            }
//
//            if (!fileName.contains(".")) {
//                zipIn.closeEntry();
//                entry = zipIn.getNextEntry();
//                continue;
//            }
//
////            fotoRepo.save(new Foto(null, fileName));
//
//            extractFile(zipIn, filePath);
//
//            zipIn.closeEntry();
//            entry = zipIn.getNextEntry();
//        }
//        zipIn.close();
//    }

    private void unzipFilesToSameDirectory(String zipFilePath, String destDirectory) throws IOException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        Charset charset = Charset.forName("IBM850");

        FileInputStream nfis = new FileInputStream(zipFilePath);
        ZipInputStream zipIn = new ZipInputStream(nfis, charset);
        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {

            String fileName = entry.getName();

            String filePath = destDirectory + File.separator + fileName;

            System.out.println(fileName);
            while (fileName.contains("/")) {
                int index = fileName.indexOf("/");
                fileName = fileName.substring(index + 1);

                System.out.println(fileName);
                if (fileName.equals("")) {
                    continue;
                }

            }

            if (!fileName.contains(".")) {
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
                continue;
            }

            String shortenedName = nameShorterner(fileName);

            if (shortenedName.length() > 1) {
                fileName = shortenedName;
            }

//            int index1 = fileName.indexOf(".");
//            fileName = fileName.substring(0, index1) + "_" + fileName.substring(index1);


            fotoRepo.save(new Foto(null, fileName));

            filePath = destDirectory + File.separator + fileName;

            extractFile(zipIn, filePath);

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }


    private void unzip(String zipFilePath) throws IOException {
        File zipFile = new File(zipFilePath);
        System.out.println("zipfilepath: " + zipFilePath);
        System.out.println("getparent: " + zipFile.getParent());


        Charset charset = Charset.forName("IBM850");

        FileInputStream nfis = new FileInputStream(zipFilePath);
        ZipInputStream zipIn = new ZipInputStream(nfis, charset);

        ZipEntry entry = zipIn.getNextEntry();

        while (entry != null) {

            extractFile(zipIn, zipFilePath);

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

    public void changeCharset(String oldFileName) {
        byte[] bytes = new byte[0];
        bytes = oldFileName.getBytes(StandardCharsets.UTF_8);

        String newFileName = new String(bytes, StandardCharsets.UTF_8);

        String pathName = "C:\\test";

        File oldfile = new File(pathName + File.separator + oldFileName);
        File newFile = new File(pathName + File.separator + "_" + newFileName);

        System.out.println(oldfile + "  " + oldfile.renameTo(newFile));
    }

    public void insertCaption() throws IOException {

        List<Foto> allFotos = fotoRepo.findAll();
        int totalInList = 0;
        for (Foto foto : allFotos) {
            if (foto.getLocal() != null) {
                totalInList ++;
            }
        }
        int actualCount = 1;
        for (Foto foto : allFotos) {
            if (foto.getLocal() != null) {
                String filePath = IMGPATH + File.separator + foto.getNome();
                BufferedImage img = getImageOrientationAndScaled(filePath);

                String fotoObs = foto.getLocal().getObservacao();
                System.out.println("gerando titulo da foto " + actualCount + " de " + totalInList);

                actualCount++;

                createImage(img, createRect(fotoObs, img.getWidth()), filePath);
            }
        }

    }

    public void createImage(BufferedImage img1, BufferedImage img2, String outputPathFile) throws IOException {

        BufferedImage joinedImg = joinBufferedImage(img1, img2);
        ImageIO.write(joinedImg, "jpg", new File(outputPathFile));
    }

    public BufferedImage joinBufferedImage(BufferedImage img1, BufferedImage img2) {
        int offset = 0;
        int width = Math.max(img1.getWidth(), img2.getWidth());
        int height = img1.getHeight() + img2.getHeight();
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        g2.setColor(oldColor);
        g2.drawImage(img1, null, 0, 0);
        g2.drawImage(img2, null, 0, img1.getHeight() + offset);
        g2.dispose();
        return newImage;
    }

    public BufferedImage getImageOrientationAndScaled(String img1) throws IOException {
        javaxt.io.Image image = new javaxt.io.Image(img1);

        // Auto-rotate based on Exif Orientation tag, and remove all Exif tags

        image.rotate();
        image.setWidth(500);

        return image.getBufferedImage();

    }

    public BufferedImage createRect(String captionText, int width) {
        BufferedImage newImage = new BufferedImage(width, 40, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = newImage.createGraphics();
        Color oldColor = g2.getColor();
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, 500, 40);
        g2.setColor(oldColor);

        javaxt.io.Image img = new javaxt.io.Image(newImage);


        int len = captionText.length();

        if (len > 50) {
            System.out.println("caption too big - lenght : " + len);
            System.out.println("caption text = " + captionText);
        }

//        int coordenatex = 250 - 5 * len;
        int coordenatex = 250 - (int)((4.5 * len));
        int coordenatey = 25;
        if (captionText.length() > 1) {
            captionText = captionText.substring(0, 1).toUpperCase() + captionText.substring(1);

            img.addText(captionText, coordenatex, coordenatey, "arial", 18, 0, 0, 0);
        }
        return img.getBufferedImage();

    }


}
