package frameforge.serializable;

import java.io.Serializable;
import java.util.*;

public class ImageKeeper implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private ArrayList<byte[]> images;

    public ImageKeeper(ArrayList<byte[]> i) {
        images = i;
    }

    public ImageKeeper(byte[] bytes) {
        images = new ArrayList<>();
        images.add(bytes);
    }

    public ArrayList<byte[]> getImages() {
        return images;
    }
}
