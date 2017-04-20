package test;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.ImageReader;
import com.google.zxing.common.HybridBinarizer;

public class Test {

	public static void main(String[] args) {
		try{
			File imgsDir = new File("img");
			if (imgsDir.exists() && imgsDir.isDirectory()){
				processFiles(imgsDir);
			}
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}

	}
	
	private static void processFiles(File imgsDir) {
		for (File file:imgsDir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File pathname) {
				if(pathname.exists()){
					if(pathname.isDirectory()){
						return true;
					} else if (pathname.getName().toLowerCase().endsWith(".png")){
						return true;
					} else if (pathname.getName().toLowerCase().endsWith(".jpg")){
						return true;
					} else if (pathname.getName().toLowerCase().endsWith(".jpeg")){
						return true;
					}
				}
				return false;
			}
		})) {
			if (file.isDirectory()) {
				processFiles(file);
			} else {
				System.out.println(file.getAbsolutePath());
				System.out.println(getDecodeText(file.toPath()));
			}
		}
	}

	private static String getDecodeText(Path file) {
	    BufferedImage image;
	    try {
	      image = ImageReader.readImage(file.toUri());
	    } catch (IOException ioe) {
	      return ioe.toString();
	    }
	    LuminanceSource source = new BufferedImageLuminanceSource(image);
	    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	    Result result;
	    try {
	      result = new MultiFormatReader().decode(bitmap);
	    } catch (ReaderException re) {
	      AffineTransform at= new AffineTransform();
	      at.translate(2*image.getWidth(), -2*image.getWidth());
	      at.rotate(Math.PI/2, image.getWidth()/2, image.getHeight()/2);
	      AffineTransformOp op=new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
	      BufferedImage image2 = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
	      op.filter(image, image2);
	      source = new BufferedImageLuminanceSource(image2);
	      bitmap = new BinaryBitmap(new HybridBinarizer(source));
	      try {
	        result = new MultiFormatReader().decode(bitmap);
	      } catch (ReaderException re2) {
	        return re2.toString();
	      }
	    }
	    return String.valueOf(result.getBarcodeFormat().name() + " - " + result.getText());
	  }

}
