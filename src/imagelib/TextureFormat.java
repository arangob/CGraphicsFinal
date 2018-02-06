package imagelib;

import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT1_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT3_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGBA_S3TC_DXT5_EXT;
import static org.lwjgl.opengl.EXTTextureCompressionS3TC.GL_COMPRESSED_RGB_S3TC_DXT1_EXT;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TextureFormat  {
    
    public static final TextureFormat COMPRESSED_RGB_DXT1 = new TextureFormat("COMPRESSED_RGB_DXT1", GL_COMPRESSED_RGB_S3TC_DXT1_EXT, 3, true);
    public static final TextureFormat COMPRESSED_RGBA_DXT1 = new TextureFormat("COMPRESSED_RGBA_DXT1", GL_COMPRESSED_RGBA_S3TC_DXT1_EXT, 4, true);
    public static final TextureFormat COMPRESSED_RGBA_DXT3 = new TextureFormat("COMPRESSED_RGBA_DXT3", GL_COMPRESSED_RGBA_S3TC_DXT3_EXT, 4, true);
    public static final TextureFormat COMPRESSED_RGBA_DXT5 = new TextureFormat("COMPRESSED_RGBA_DXT5", GL_COMPRESSED_RGBA_S3TC_DXT5_EXT, 4, true);
    
    public static final TextureFormat RGBA = new TextureFormat("RGBA", GL11.GL_RGBA, 4);
    public static final TextureFormat RGB = new TextureFormat("RGB", GL11.GL_RGB, 3);
    
    public static final TextureFormat BGRA = new TextureFormat("BGRA", GL12.GL_BGRA, 4);
    public static final TextureFormat BGR = new TextureFormat("BGR", GL12.GL_BGR, 3);
    
    public static final TextureFormat RED = new TextureFormat("RED", GL11.GL_RED, 1);
    public static final TextureFormat GREEN = new TextureFormat("GREEN", GL11.GL_GREEN, 1);
    public static final TextureFormat BLUE = new TextureFormat("BLUE", GL11.GL_BLUE, 1);
    public static final TextureFormat ALPHA = new TextureFormat("ALPHA", GL11.GL_BLUE, 1);
    
    public static final TextureFormat LUMINANCE = new TextureFormat("LUMINANCE", GL11.GL_LUMINANCE, 1);
    public static final TextureFormat LUMINANCE_ALPHA = new TextureFormat("LUMINANCE_ALPHA", GL11.GL_LUMINANCE_ALPHA, 2);
    
    private int glFormat;
    private int bpp;
    private String debugName = "";
    private boolean compressed;
    
    public TextureFormat(int glFormat, int bpp) {
        this.glFormat = glFormat;
        this.bpp = bpp;
    }
    
    public TextureFormat(String debugName, int glFormat, int bpp) {
    	this(debugName, glFormat, bpp, false);
    }
    
    public TextureFormat(String debugName, int glFormat, int bpp, boolean compressed) {
    	this.glFormat = glFormat;
    	this.bpp = bpp;
    	this.debugName = debugName;
    	this.compressed = compressed;
    }
    
    public boolean isCompressed() {
    	return compressed;
    }
    
    public int getGLFormat() {
        return glFormat;
    }
    
    public int getBytesPerPixel() {
        return bpp;
    }
    
    public String toString() {
    	return debugName!=null&&debugName.length()!=0 ? debugName : super.toString();
    }
    
    public boolean equals(Object o) {
    	return o instanceof TextureFormat && ((TextureFormat)o).getGLFormat()==getGLFormat();
    }

    
}
