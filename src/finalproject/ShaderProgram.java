package finalproject;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;


public class ShaderProgram
{
    private int programID;
    private boolean linked;
    private Map<Integer, Integer> shaderID;
    private Map<String, Integer> uniformLocation;
    private Map<String, Integer> attribLocation;

    public ShaderProgram() {
        programID = glCreateProgram();
        shaderID = new HashMap<Integer, Integer>();
        uniformLocation = new HashMap<String, Integer>();
        attribLocation = new HashMap<String, Integer>();
        linked = false;
    }

    private void attachShader(int type, String shaderSource) {
    	if (linked) {
    		throw new RuntimeException ("Shader program has already been linked");
    	}
    	if (shaderID.containsKey(type)) {
    		throw new RuntimeException("Duplicate shader type " + type);
    	}

        int newShaderID = glCreateShader(type);
        glShaderSource(newShaderID, shaderSource);
        glCompileShader(newShaderID);

        if (glGetShaderi(newShaderID, GL_COMPILE_STATUS) == GL_FALSE)
            throw new RuntimeException("Error creating shader:\n"
                                       + glGetShaderInfoLog(newShaderID, glGetShaderi(newShaderID, GL_INFO_LOG_LENGTH)));

        glAttachShader(programID, newShaderID);
        shaderID.put(type, newShaderID);
    }

    public void attachVertexShaderFile(String name) {    	
    	byte[] encodedSource;
		try {
			encodedSource = Files.readAllBytes(Paths.get(name));
	        String shaderSource = new String(encodedSource, StandardCharsets.UTF_8);
	    	attachShader(GL_VERTEX_SHADER, shaderSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void attachFragmentShaderFile(String name) {
    	byte[] encodedSource;
		try {
			encodedSource = Files.readAllBytes(Paths.get(name));
	        String shaderSource = new String(encodedSource, StandardCharsets.UTF_8);
	    	attachShader(GL_FRAGMENT_SHADER, shaderSource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public void attachVertexShaderTxt(String shaderSource) {    	
    	attachShader(GL_VERTEX_SHADER, shaderSource);
    }

    public void attachFragmentShaderTxt(String shaderSource) {
    	attachShader(GL_FRAGMENT_SHADER, shaderSource);
    }
    
    public void link() {
        glLinkProgram(programID);

        if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE)
            throw new RuntimeException("Unable to link shader program:\n"
                                       + glGetProgramInfoLog(programID));

    	linked = true;
        uniformLocation.clear();
        attribLocation.clear();
    }

    public void bind() {
    	if (!linked) {
    		throw new RuntimeException ("Shader program has not yet been linked");
    	}
        glUseProgram(programID);
    }

    public static void unbind() {
        glUseProgram(0);
    }

    public int getUniformLocation(String name) {
    	if (!linked) {
    		throw new RuntimeException ("Shader program has not yet been linked");
    	}
    	if (!uniformLocation.containsKey(name)) {
    		int location = glGetUniformLocation(programID, memUTF8(name));
    		if (location == -1) {
    			throw new RuntimeException("Could not find uniform " + name);
    		}
    		uniformLocation.put(name, location);	
    	}
    	return uniformLocation.get(name);
    }

    public int getAttribLocation(String name) {
    	if (!linked) {
    		throw new RuntimeException ("Shader program has not yet been linked");
    	}
    	if (!attribLocation.containsKey(name)) {
    		int location = glGetAttribLocation(programID, memUTF8(name));
    		if (location == -1) {
    			throw new RuntimeException("Could not find attribute " + name);
    		}
    		attribLocation.put(name, location);	
    	}
    	return attribLocation.get(name);
    }

    public void destroy() {
        unbind();

        for (Map.Entry<Integer, Integer> entry : shaderID.entrySet()) {
        	glDetachShader(programID, entry.getValue());
        	glDeleteShader(entry.getValue());
        }

        glDeleteProgram(programID);
    }

    public int getID() {
        return programID;
    }
}