package nam.kwan.woo;



import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.imageio.ImageIO;

public class Converter {
	public static byte[] convertURLtoByte(String args) {
		try {
			URL url = new URL(args);
			BufferedImage urlImage = ImageIO.read(url);
			
			byte[] imageInByte;
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(urlImage, "jpg", baos);
			
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
			
			return imageInByte;

		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public static BufferedImage convertURLtoBufferedImage(String args) {
		try {
			URL url = new URL(args);
			BufferedImage urlImage = ImageIO.read(url);
					
			return urlImage;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	public static int convertBytetoJpegFile(byte[] args, int index) {
		try {
			InputStream in = new ByteArrayInputStream(args);
			BufferedImage bImageFromConvert = ImageIO.read(in);
			ImageIO.write(bImageFromConvert, "jpg", new File("C:\\Users\\Gram\\Desktop\\images/imgConverted_" + index + ".jpg"));
			return 1; // suc
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return 0; // failed
		}
	}
}