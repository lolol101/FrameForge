package frameforge.serializable;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonSerializable implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private ArrayList<byte[]> images = null;
    private ObjectNode json = null;

    public void setOnePhoto(byte[] image) {
        images = new ArrayList<>();
        images.add(image);
    }

    public void setManyPhotos(ArrayList<byte[]> images) {
        this.images = images; 
    }

    public void setJson(ObjectNode json) {
        this.json = json;
    }

    public ArrayList<byte[]> getImages() {
        return images;
    }

    public ObjectNode getJson() {
        return json;
    }
}
