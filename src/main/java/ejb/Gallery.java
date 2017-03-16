package ejb;

import javax.ejb.Remote;
import java.io.IOException;

@Remote
public interface Gallery {
    String save(String fileEncoded, String fileType) throws IOException;
    String getFormattedImages();
    String getFullImage(String name);
    String getFormattedDeleteImages();
    void deleteImage(String name);
}