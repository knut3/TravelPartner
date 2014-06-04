package services.interfaces;

import java.io.IOException;
import java.util.UUID;


public interface IImageService {
	
	void generateResizedImages(String sourceUrl, UUID id, int width, int height) throws IOException;
	
}
