package ejb;

import org.apache.commons.io.IOUtils;
import webservices.GalleryService;

import javax.ejb.Stateful;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

@Stateful
public class GalleryImpl implements Gallery {
    private GalleryService usluga;

    public GalleryImpl() {
        URL url = null;

        try {
            url = new URL("http://localhost:29218/WebService_war_exploded/services/gallery?wsdl");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        QName qname = new QName("http://webservices/", "GalleryServiceImplService");

        Service service = Service.create(url, qname);

        this.usluga = service.getPort(GalleryService.class);
    }

    public String save(String fileEncoded, String fileType) throws IOException {
        return usluga.upload(fileEncoded, fileType);
    }

    public String getFormattedImages() {
        String result = "";

        int counter = 1;
        //for (String urlBase64 : usluga.getImages(0, 0)) {
        for (String[] img : usluga.getImages(0, 0)) {
            //String url = Base64.base64Decode(urlBase64);

            String imageBase64 = img[0];
            String nameBase64  = encodeBase64(img[1]);

            if (counter % 6 == 1) {
                if (counter != 1)
                    result += "</div>";
                result += "<div class=\"row\">";
            }

            result += "<div class=\"col-xs-6 col-md-3 col-lg-2\">";
            result += "<a href=\"showFull.jsp?name=" + nameBase64 + "\" class=\"thumbnail\"><img alt=\"img\" src=\"data:image/jpeg;base64," + imageBase64 + "\"/></a>";
            //result += "<a href=\"" + url + "\" class=\"thumbnail\"><img alt=\"img\" src=\"file:///" + url + "\"/></a>";
            result += "</div>";
            counter++;
        }

        return result;
    }

    public String getFormattedDeleteImages() {
        String result = "";

        int counter = 1;
        for (String[] img : usluga.getImages(0, 0)) {
            String imageBase64 = img[0];
            String nameBase64  = encodeBase64(img[1]);

            if (counter % 6 == 1) {
                if (counter != 1)
                    result += "</div>";
                result += "<div class=\"row\">";
            }

            result += "<div class=\"col-xs-6 col-md-3 col-lg-2\">";
            result += "<div class=\"thumbnail\">";
            result += "<img alt=\"img\" src=\"data:image/jpeg;base64," + imageBase64 + "\"/>";
            result += "<p><a href=\"deleteFile.jsp?name=" + nameBase64 + "\" class=\"btn btn-danger\" style=\"width: 100%\" role=\"button\">Usuń ten obrazek</a> </p>";
            result += "</div></div>";
            counter++;
        }

        return result;
    }

    public String getFullImage(String name) {
        String imageBase64 = usluga.getImageContent(name);

        String nameDecoded = com.sun.xml.messaging.saaj.util.Base64.base64Decode(name);

        String result = "<img title=\"" + nameDecoded + "\" src=\"data:image/jpeg;base64," + imageBase64 + "\" />";

        return result;
    }

    public void deleteImage(String name) {
        usluga.deleteImage(name);
    }

    private String encodeBase64(String in) {
        byte[] bytes = new byte[0];
        try {
            bytes = IOUtils.toByteArray(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return com.sun.org.apache.xerces.internal.impl.dv.util.Base64.encode(bytes);
    }
}