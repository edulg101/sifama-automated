package gov.antt.sifama.services;

import ch.qos.logback.core.util.CloseUtil;
import com.google.common.primitives.Chars;
import gov.antt.sifama.model.Foto;
import gov.antt.sifama.model.Local;
import gov.antt.sifama.repositories.FotoRepo;
import gov.antt.sifama.services.util.AppUtil;
import org.apache.commons.imaging.*;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.*;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.metadata.IIOMetadata;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static gov.antt.sifama.services.appConstants.AppConstants.IMGPATH;
import static gov.antt.sifama.services.appConstants.AppConstants.IMGPATHGPS;


@Service
public class FotoService {

    public static final int BUFFER_SIZE = 4096;

    @Autowired
    FotoRepo fotoRepo;

    @Autowired
    LocalService localService;

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
            unzipFilesToSameDirectory1(path + File.separator + files[i], destPath);
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

        if (name.contains("MT") || name.length() > 80) {

            try {
                int index = name.indexOf("MT");
                name = name.substring(0, index) + name.substring(name.indexOf("km") - 1);
            } catch (RuntimeException e) {
                System.out.println("não consegui diminuir o tamanho com MT");
            }
            if (name.length() > 98) {

                name = name.substring(0, 45) + name.substring(name.length() - 45);
            }

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

    private void unzipFilesToSameDirectory1(String zipFilePath, String destDirectory) throws IOException, InterruptedException {
        File destDir = new File(destDirectory);
        if (!destDir.exists()) {
            destDir.mkdir();
        }
        Charset charset = null;
        String system = System.getProperty("os.name");
        if (system.toLowerCase().contains("windows")) {
            charset = Charset.forName("IBM850");
        } else if (system.toLowerCase().contains("linux")) {
            charset = StandardCharsets.UTF_8;
        }

        FileInputStream nfis = new FileInputStream(zipFilePath);
        ZipInputStream zipIn = new ZipInputStream(nfis, Charset.forName("IBM850"));
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


            fotoRepo.save(new Foto(null, fileName));

            filePath = destDirectory + File.separator + fileName;

            List<Local> locais = localService.getAll();

            for (Local local : locais) {
                boolean mat = AppUtil.getMatch(fileName, local.getNumIdentificacao() );
                System.out.println("checando: " + local.getNumIdentificacao() + " file: " +
                        fileName + " check: " + mat);
                if (mat) {
                    extractFile(zipIn, filePath);
                }
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }

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

            fotoRepo.save(new Foto(null, fileName));

            filePath = destDirectory + File.separator + fileName;

            extractFile(zipIn, filePath);

            zipIn.closeEntry();
            entry = zipIn.getNextEntry();
        }
        zipIn.close();
    }


        private void unzip (String zipFilePath) throws IOException {
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

        public void changeCharset (String oldFileName){
            byte[] bytes = new byte[0];
            bytes = oldFileName.getBytes(StandardCharsets.UTF_8);

            String newFileName = new String(bytes, StandardCharsets.UTF_8);

            String pathName = "C:\\test";

            File oldfile = new File(pathName + File.separator + oldFileName);
            File newFile = new File(pathName + File.separator + "_" + newFileName);

        }

        public void insertCaption () throws IOException {

            List<Foto> allFotos = fotoRepo.findAll();
            int totalInList = 0;
            for (Foto foto : allFotos) {
                if (foto.getLocal() != null) {
                    totalInList++;
                }
            }
            int actualCount = 1;
            for (Foto foto : allFotos) {
                if (foto.getLocal() != null) {
                    String filePath = IMGPATH + File.separator + foto.getNome();

                    javaxt.io.Image image = new javaxt.io.Image(filePath);

                    double[] gps = image.getGPSCoordinate();

                    image.rotate();


                    IIOMetadata metadata = image.getIIOMetadata();

                    java.util.HashMap<Integer, Object> exif = image.getExifTags();

                    int orientation = 6;

                    try {
                        orientation = (int) exif.get(0x0112);

                    } catch (NullPointerException e) {
                        System.out.println("imagem: " + foto.getNome() + " Sem Exif para orientação");

                    }


//                    System.out.println(foto.getNome());

                    switch (orientation) {
                        case 6:
                            image.setWidth(500);
                            javaxt.io.Image rect = createRect(foto.getLocal().getObservacao(), 500);
                            image.crop(0, 40, image.getWidth(), image.getHeight() - 40);
                            image.addImage(rect, 0, image.getHeight(), true);
                            break;
                        case 1:
                            image.setWidth(625);
                            rect = createRect(foto.getLocal().getObservacao(), 625);
                            image.addImage(rect, 0, image.getHeight(), true);
                            break;
                    }

                    System.out.println("gerando titulo da foto " + actualCount + " de " + totalInList);

                    actualCount++;

                    File file = new File(filePath);
                    FileOutputStream fos = new FileOutputStream(file);

                    image.saveAs(filePath);

                    File currentImagePath = new File(filePath);
                    File imagePathWithGps = new File(IMGPATHGPS + File.separator + foto.getNome());

                    FileUtils.copyFile(currentImagePath, imagePathWithGps);

                    try {
                        changeExifMetadata(new File(filePath), imagePathWithGps, gps);
                    } catch (ImageReadException e) {
                        e.printStackTrace();
                    } catch (ImageWriteException e) {
                        e.printStackTrace();
                    }

                }
            }

        }


        public BufferedImage joinBufferedImage (BufferedImage img1, BufferedImage img2){
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


        public javaxt.io.Image createRect (String captionText,int width){
            int coordenateX = 0;
            BufferedImage newImage = new BufferedImage(width, 34, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = newImage.createGraphics();
            Color oldColor = g2.getColor();
            g2.setPaint(Color.WHITE);
            g2.fillRect(0, 0, width, 34);
            g2.setColor(oldColor);
            javaxt.io.Image img = new javaxt.io.Image(newImage);
            int len = captionText.length();

            if (len > 50) {
                System.out.println("caption too big - lenght : " + len);
                System.out.println("caption text = " + captionText);
            }
            if (width == 500) {
                coordenateX = 250 - (int) ((4.3 * len));
            } else {
                coordenateX = 313 - (int) ((4.3 * len));
            }
            int coordenateY = 25;
            if (captionText.length() > 1) {
                captionText = captionText.substring(0, 1).toUpperCase() + captionText.substring(1);

                img.addText(captionText, coordenateX, coordenateY, "arial", 18, 0, 0, 0);
            }
            return img;

        }


        public void getGps () throws ImageWriteException, ImageReadException, IOException {
            List<Foto> allFotos = fotoRepo.findAll();
            int totalInList = 0;
            for (Foto foto : allFotos) {
                if (foto.getLocal() != null) {
                    totalInList++;
                }
            }
            int actualCount = 1;
            for (Foto foto : allFotos) {
                if (foto.getLocal() != null) {

                    File imagePath = new File(IMGPATH + File.separator + foto.getNome());
                    File imagePathWithGps = new File(IMGPATHGPS + File.separator + foto.getNome());
                    javaxt.io.Image image = new javaxt.io.Image(imagePath);
                    double[] gps = image.getGPSCoordinate();
                    System.out.println(foto.getNome());
                    System.out.println(gps[0]);
                    changeExifMetadata(imagePath, imagePathWithGps, gps);
                }
            }
        }

        public void changeExifMetadata (File jpegImageFile, File dst,double[] gps)
            throws IOException, ImageReadException, ImageWriteException {

            OutputStream os = null;
            try {
                TiffOutputSet outputSet = null;

                // note that metadata might be null if no metadata is found.
                final ImageMetadata metadata = Imaging.getMetadata(jpegImageFile);
                final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                if (null != jpegMetadata) {
                    // note that exif might be null if no Exif metadata is found.
                    final TiffImageMetadata exif = jpegMetadata.getExif();

                    if (null != exif) {
                        // TiffImageMetadata class is immutable (read-only).
                        // TiffOutputSet class represents the Exif data to write.
                        //
                        // Usually, we want to update existing Exif metadata by
                        // changing
                        // the values of a few fields, or adding a field.
                        // In these cases, it is easiest to use getOutputSet() to
                        // start with a "copy" of the fields read from the image.
                        outputSet = exif.getOutputSet();
                    }
                }

                // if file does not contain any exif metadata, we create an empty
                // set of exif metadata. Otherwise, we keep all of the other
                // existing tags.
                if (null == outputSet) {
                    outputSet = new TiffOutputSet();
                }

                {
                    // Example of how to add a field/tag to the output set.
                    //
                    // Note that you should first remove the field/tag if it already
                    // exists in this directory, or you may end up with duplicate
                    // tags. See above.
                    //
                    // Certain fields/tags are expected in certain Exif directories;
                    // Others can occur in more than one directory (and often have a
                    // different meaning in different directories).
                    //
                    // TagInfo constants often contain a description of what
                    // directories are associated with a given tag.
                    //
                    final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
                    // make sure to remove old value if present (this method will
                    // not fail if the tag does not exist).

                    exifDirectory.removeField(ExifTagConstants.EXIF_TAG_GPSINFO);
                }


                // Example of how to add/update GPS info to output set.

                // New York City
                double longitude = 0;
                double latitude = 0;
                try {
                    longitude = gps[0]; // 74 degrees W (in Degrees East)
                } catch (NullPointerException e) {
                    longitude = 0;
                }
                try {
                    latitude = gps[1]; // 40 degrees N (in Degrees
                    // North)
                } catch (NullPointerException e) {
                    latitude = 0;
                }

                outputSet.setGPSInDegrees(longitude, latitude);


                final TiffOutputDirectory exifDirectory = outputSet.getOrCreateRootDirectory();
                exifDirectory.removeField(ExifTagConstants.EXIF_TAG_SOFTWARE);
                exifDirectory.add(ExifTagConstants.EXIF_TAG_SOFTWARE,
                        "SomeKind");

                os = new FileOutputStream(dst);
                os = new BufferedOutputStream(os);


                ExifRewriter rewriter = new ExifRewriter();

                FileInputStream fis = new FileInputStream(jpegImageFile);

                BufferedInputStream bis = new BufferedInputStream(fis);


                new ExifRewriter().updateExifMetadataLossless(bis, os,
                        outputSet);


//            new ExifRewriter().updateExifMetadataLossless(jpegImageFile, os,
//                    outputSet);

            } finally {
                if (os != null) {
                    os.close();
                }
            }
        }


    }


