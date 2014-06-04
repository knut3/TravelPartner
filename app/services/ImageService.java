package services;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import services.interfaces.IImageService;
import settings.ImageConfig;
import settings.ImageConfig.ImageShape;

public class ImageService implements IImageService{

	
	@Override
	public void generateResizedImages(String sourceUrl, UUID id, int width, int height) throws IOException {
		
		URL url = new URL(sourceUrl);		
		BufferedImage source = ImageIO.read(url);
		
		// Create a square version of the image
		int diff = width - height;
		int squareWidth = width;
		int squareHeight = height;
		int squareX = 0;
		int squareY = 0;
		
		if(diff < 0){ // height > width
			squareHeight = height + diff; // subtract diff from height
			squareY = diff / (-2); // center square crop
		}
		else{ // height <= width
			squareWidth = width - diff; // subtract diff from width
			squareX = diff / 2; // center square crop
		}
		
		BufferedImage square = Scalr.crop(source, squareX, squareY, squareWidth, squareHeight);

		// Create small image
		BufferedImage tempSource = source;		
		if(ImageConfig.SMALL_SHAPE == ImageShape.SQUARE) 
			tempSource = square;		
		BufferedImage small = Scalr.resize(tempSource, ImageConfig.SMALL_WIDTH);		
		File destFile = new File("public/images/small/" + id.toString() + ".jpg");		   
		ImageIO.write(small, "jpg", destFile);
		
		// Create medium sized image
		tempSource = source;		
		if(ImageConfig.MEDIUM_SHAPE == ImageShape.SQUARE) 
			tempSource = square;	
		BufferedImage medium = Scalr.resize(tempSource, ImageConfig.MEDIUM_WIDTH);		
		destFile = new File("public/images/medium/" + id.toString() + ".jpg");		   
		ImageIO.write(medium, "jpg", destFile);
		
		// Create large image
		tempSource = source;		
		if(ImageConfig.LARGE_SHAPE == ImageShape.SQUARE) 
			tempSource = square;	
		BufferedImage large = Scalr.resize(tempSource, ImageConfig.LARGE_WIDTH);		
		destFile = new File("public/images/large/" + id.toString() + ".jpg");
		ImageIO.write(large, "jpg", destFile);
		
		source.flush();
		square.flush();
	}
	
}
