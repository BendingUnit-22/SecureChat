import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.io.*;


import javax.imageio.*;		// loading and saving images

import java.awt.image.*;

public class IMAGE_OBJ implements Serializable {
	private byte[] byteImage = null;
	private String extension = ".png";

	public IMAGE_OBJ (String f) {
		this(new File(f));
	}


	public IMAGE_OBJ (File file) {
		try {
			BufferedImage bi = ImageIO.read(file);
			byteImage = toByteArray(bi);
			extension = getFileExtension(file);
		}
		catch(IOException e) {
			System.err.println("ERROR loading image: " + e);
			System.exit(-1);
		}
	}


	private byte[] toByteArray(BufferedImage bufferedImage) {
		if (bufferedImage != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
				ImageIO.write(bufferedImage, "png", baos);
			} catch (IOException e) {
				throw new IllegalStateException(e.toString());
			}
			return baos.toByteArray();
		}
		return new byte[0];
	}


	private BufferedImage fromByteArray() throws IOException {
		return ImageIO.read(new ByteArrayInputStream(byteImage));
	}

	public Image getImage(){
		try {
			BufferedImage bbii = fromByteArray();
			Image image = SwingFXUtils.toFXImage(bbii, null);
			return image;
		}
		catch(Exception e) {
			System.out.println("Exception while saving Image: " + e);
			System.exit(-1);
		}
		return null;
	}

	public void saveImage(String f) {
		try {
			BufferedImage bbii = fromByteArray();
			ImageIO.write(bbii, extension, new File(f));
			System.out.println("Image Saved");
		}
		catch(IOException ee) {
			System.err.println("IO ERROR saving image: " + ee);
			System.exit(-1);
		}
		catch(Exception e) {
			System.out.println("Exception while saving Image: " + e);
			System.exit(-1);
		}
	}

	private  String getFileExtension(File file) {
		String fileName = file.getName();
		if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".")+1);
		else return "";
	}

}
