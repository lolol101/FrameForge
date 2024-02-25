package ru.server;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

public class ImageHandler {

    public ImageHandler() {
        
    }
    
    public static BufferedImage readFromFile(String pathFile, String format) throws IOException {
        //ByteArrayOutputStream outImage = new ByteArrayOutputStream();

        File imageFile = new File(pathFile);
        BufferedImage img = ImageIO.read(imageFile);
            //ImageIO.write(img, format, outImage);

        //return outImage;
        return img;
    }

    public static void writeToFile(String pathFile, String format,
    BufferedImage out) throws IOException {
        File input = new File(pathFile);
        ImageIO.write(out, format, input);
    }
}
