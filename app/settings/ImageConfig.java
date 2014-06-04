package settings;

public final class ImageConfig {

	public static enum ImageShape{
		SQUARE,
		ORIGINAL
	}
	
	public static final int SMALL_WIDTH = 50;
	public static final ImageShape SMALL_SHAPE = ImageShape.SQUARE;	
	public static final int MEDIUM_WIDTH = 100;
	public static final ImageShape MEDIUM_SHAPE = ImageShape.SQUARE;	
	public static final int LARGE_WIDTH = 300;
	public static final ImageShape LARGE_SHAPE = ImageShape.ORIGINAL;
}
