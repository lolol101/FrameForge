package frameforge.client;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

import java.io.ByteArrayOutputStream;

public class ImageHandler {
    public BufferedImage img;
    public ImgType typeImage;
    public String formatImage;
    
    public enum ImgType {
        SCALED, FULL
    };

    public ImageHandler(String path, ImgType type,
    String format) throws IOException {
        typeImage = type;
        formatImage = format;
        img = ImageIO.read(new File(path));
    }

    public ImageHandler(BufferedImage bufImage, ImgType type,
    String format) throws IOException {
        img = bufImage;
        typeImage = type;
        formatImage = format;
    }

    public void resizeImage(
    int targetWidth, int targetHeight) throws IOException {
        Image resultingImage = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_FAST);
        BufferedImage outImg = new BufferedImage(targetHeight, targetWidth, BufferedImage.TYPE_INT_RGB);
        outImg.getGraphics().drawImage(resultingImage, 0, 0, null);
        img = outImg;
        typeImage = ImgType.SCALED;
    }

    public void writeToFile(String pathFile, String format) throws IOException {
        File input = new File(pathFile);
        ImageIO.write(img, format, input);
    }

    public ByteArrayOutputStream getByteArray() 
    throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, formatImage, out);
        return out;
    } 
}
