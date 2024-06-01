package frameforge.serializable;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.databind.node.ObjectNode;
import java.awt.image.BufferedImage;

public class JsonSerializable implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private ArrayList<BufferedImage> images = null;
    private ObjectNode json = null;

    public void setOnePhoto(BufferedImage image) {
        images = new ArrayList<>();
        images.add(image);
    }

    public void setManyPhotos(ArrayList<BufferedImage> images) {
        this.images = images; 
    }

    public void setJson(ObjectNode json) {
        this.json = json;
    }

    public ArrayList<BufferedImage> getImages() {
        return images;
    }

    public ObjectNode getJson() {
        return json;
    }
}
