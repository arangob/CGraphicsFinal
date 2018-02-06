package finalproject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;

import org.lwjgl.BufferUtils;

import imagelib.ImageDecoder;
import imagelib.ImageDecoderBMP;
import imagelib.ImageDecoderDDS;
import imagelib.ImageDecoderJPEG;
import imagelib.ImageDecoderPNG;
import imagelib.ImageDecoderTGA;
import imagelib.TextureFormat;

public class ImageFile {
	/********************************************
	 * Class represents a single image file on your computer,
	 * allowing you to pull the raw pixel data out of it as
	 * well as some metadata. 
	 * 
	 * Supports .png, .jpeg, .bmp, .tga, and .dds files.
	 *  
	 * To load an image into your code:
	 * ImageFile partyparrot = new ImageFile("path/to/your/partyparrot.png")
	 * 
	 * Adapted from mattdesl's "slim" LWJGL library:
	 * https://github.com/mattdesl/slim
	 */

	/********************************************
	 * Methods useful for your OpenGL code
	 */
	
	/***
	 * @return the width of the image, in pixels
	 */
	public int getWidth() {
		return width;
	}
	
	
	/***
	 * @return the height of the image, in pixels
	 */
	public int getHeight() {
		return height;
	}
	
	/***
	 * @return the number of bytes used to represent a singe pixel
	 */
	public int getBytesPerPixel() {
		return bpp;
	}
	
	/***
	 * To pass this format to OpenGL code, use the returned TextureFormat's
	 * getGLFormat() method.
	 * 
	 * @return the format of the bytes used to represent a single pixel
	 */
	public TextureFormat getPixelFormat() {
		return format;
	}
	
	/***
	 * @return a buffer containing the image data itself
	 */
	public ByteBuffer getImageData() {
		return data;
	}
	
	
	/********************************************
	 * Internal functions and state
	 */

	public ImageFile(String filename) {
		URL fileURL = null;
		try {
			fileURL = new File(filename).toURI().toURL();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
			System.exit(1);
		}
		
		ImageDecoder decoder = null;
        String p = fileURL.getPath();
        if (endsWithIgnoreCase(p, ".png")) {
            decoder = new ImageDecoderPNG(fileURL);
        } else if (endsWithIgnoreCase(p, ".tga")) {
        	decoder = new ImageDecoderTGA(fileURL);
        } else if (endsWithIgnoreCase(p, ".jpg") || endsWithIgnoreCase(p, ".jpeg")) {
        	decoder = new ImageDecoderJPEG(fileURL);
        } else if (endsWithIgnoreCase(p, ".dds")) {
        	decoder = new ImageDecoderDDS(fileURL);
        } else if (endsWithIgnoreCase(p, ".bmp")) {
        	decoder = new ImageDecoderBMP(fileURL);
        } else {
        	throw new RuntimeException("Unrecognized image format for image '"+p+"'");
        }			
        
		try {
			decoder.open();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		width = decoder.getWidth();
		height = decoder.getHeight();
		format = decoder.getFormat();
		bpp = format.getBytesPerPixel();
		
		data = BufferUtils.createByteBuffer(bpp * width * height);
		try {
			decoder.decode(data);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		data.flip();

		decoder.close();
	}
		
    public final static boolean endsWithIgnoreCase(String str, String end) {
        return str.regionMatches(true, str.length()-
                end.length(), end, 0, end.length());
    }
	
	private int width;
	private int height;
	private int bpp;
	private TextureFormat format;
	private ByteBuffer data; 
}
