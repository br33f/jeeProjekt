package webservices;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;

import javax.imageio.ImageIO;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.xml.ws.Endpoint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

@WebService(endpointInterface = "webservices.GalleryService")
public class GalleryServiceImpl implements GalleryService {
    public static String PATH;

    public GalleryServiceImpl() {
        PATH = new File(".").getAbsolutePath() + "/gallery/";
        File theDir = new File(PATH);

        if (!theDir.exists()) {
            try {
                theDir.mkdir();
            } catch (SecurityException se) {
                //handle it
            }
        }
    }

    @WebMethod
    public String upload(String fileEncoded, String fileType) {
        byte[] data = Base64.decode(fileEncoded);
        String[] typeArray = fileType.split("/");
        String ext = typeArray[1];
        String fileName = RandomStringUtils.randomAlphanumeric(8) + "." + ext;
        try (OutputStream stream = new FileOutputStream(PATH + fileName)) {
            stream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File imgPath = new File(PATH + fileName);
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(imgPath);
        } catch (IOException e) {
            e.printStackTrace();
        }


        int[][] arr = new int[bufferedImage.getHeight()][bufferedImage.getWidth()];

        for (int i = 0; i < bufferedImage.getHeight(); i++)
            for (int j = 0; j < bufferedImage.getWidth(); j++)
                arr[i][j] = bufferedImage.getRGB(j, i);


        System.out.println("doba");
        arr = this.filter(arr);

        BufferedImage bufferedImageOut = new BufferedImage(arr[0].length, arr.length, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < arr.length; row++) {
            for (int column = 0; column < arr[0].length; column++) {
                int Pixel = arr[row][column] << 16 | arr[row][column] << 8 | arr[row][column];
                bufferedImageOut.setRGB(column, row, Pixel);
            }


        }

        File outputfile = new File(PATH + fileName);
        try {
            ImageIO.write(bufferedImageOut, ext, outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }

    private int[][] filter(int[][] input) {
        /*
        * Tutaj Implementacja
        * */

        //po prostu przepisuje wejscie na wyjscie (usunąc po dodaniu implementacji algorytmu)
        int[][] output = input;

        return output;
    }

    @WebMethod
    public String[][] getImages(int limit, int offset) {
        ArrayList<String[]> images = new ArrayList<String[]>();

        File folder = new File(PATH);
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                try {
                    String fileEncoded = Base64.encode(FileUtils.readFileToByteArray(file));
                    images.add(new String[]{fileEncoded, file.getName()});
                    //String path = file.getCanonicalPath().replace("\\", "/");
                    //String pathEncoded = Base64.encode(IOUtils.toByteArray(path));
                    //images.add(pathEncoded);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return images.toArray(new String[0][0]);
    }

    @WebMethod
    public String getImageContent(String name) {
        String fileName = com.sun.xml.messaging.saaj.util.Base64.base64Decode(name);
        File file = new File(PATH + "/" + fileName);

        try {
            return Base64.encode(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @WebMethod
    public void deleteImage(String name) {
        String fileName = com.sun.xml.messaging.saaj.util.Base64.base64Decode(name);

        File file = new File(PATH + "/" + fileName);
        file.delete();
    }

    public static void main(String[] argv) {
        Object implementor = new GalleryServiceImpl();
        String address = "http://localhost:9000/GalleryServiceImpl";
        Endpoint.publish(address, implementor);
    }

}