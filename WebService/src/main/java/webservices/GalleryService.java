package webservices;

import javax.jws.WebMethod;
import javax.jws.WebService;

@WebService
public interface GalleryService {
    @WebMethod
    String upload(String fileEncoded, String fileType);

    @WebMethod
    String[][] getImages(int limit, int offset);

    @WebMethod
    String getImageContent(String name);

    @WebMethod
    void deleteImage(String name);
}