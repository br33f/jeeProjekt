package ejb;

import org.apache.commons.io.IOUtils;
import org.glassfish.tyrus.core.Base64Utils;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
public class GalleryBean {
    @EJB
    Gallery gallery;
    private Part file;
    private String result;

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String save() throws IOException {
        byte[] bytes = IOUtils.toByteArray(file.getInputStream());
        String fileEncoded = Base64Utils.encodeToString(bytes, false);
        String fileType = file.getContentType();
        result = gallery.save(fileEncoded, fileType);
        return "success";
    }

    public void validate(FacesContext ctx, UIComponent comp, Object value) {
        List<FacesMessage> msgs = new ArrayList<FacesMessage>();
        Part file = (Part) value;
        if (file.getSize() > 10*1000000) {
            msgs.add(new FacesMessage("Plik  za duży"));
        }
        if (!file.getContentType().equals("image/jpeg") && !file.getContentType().equals("image/jpg") &&!file.getContentType().equals("image/png") && !file.getContentType().equals("image/gif")) {
            msgs.add(new FacesMessage("Niedozwolony typ pliku"));
        }
        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
    }

}