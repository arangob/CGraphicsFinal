package finalproject;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.Vector;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;

public class TexturedMesh {
	private int vaoID;
	private int vertexBufferID;

	private int textureID;
	private int textureBufferID;
		
	private ShaderProgram shaders;

	private int nVertices;

	public TexturedMesh(ShaderProgram shaders, ImageFile textureImage) {
		this.shaders = shaders;
		shaders.bind();
		nVertices = 0;

		vaoID = glGenVertexArrays();
		textureID = glGenTextures(); // Generate a handle to a new texture object

		int[] buffers = new int[2];
		glGenBuffers(buffers);
		vertexBufferID = buffers[0];
		textureBufferID = buffers[1];

		glBindVertexArray(vaoID);
		glEnableVertexAttribArray(shaders.getAttribLocation("position"));
		glEnableVertexAttribArray(shaders.getAttribLocation("vertex_UV"));

		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		glVertexAttribPointer(shaders.getAttribLocation("position"), 4, GL_FLOAT, false, 0, 0);

		glBindTexture(GL_TEXTURE_2D, textureID);
		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, textureImage.getWidth(), textureImage.getHeight(), 0,
				textureImage.getPixelFormat().getGLFormat(), GL_UNSIGNED_BYTE, textureImage.getImageData());
		glBindBuffer(GL_ARRAY_BUFFER, textureBufferID); 
		glVertexAttribPointer(shaders.getAttribLocation("vertex_UV"), 2, GL_FLOAT, false, 0, 0);
	}

	private float[] flattenVector(Vec4[] v) {
		float[] flattened = new float[v.length * 4];
		for (int i = 0; i < v.length; i++) {
			flattened[i * 4 + 0] = v[i].x;
			flattened[i * 4 + 1] = v[i].y;
			flattened[i * 4 + 2] = v[i].z;
			flattened[i * 4 + 3] = v[i].w;
		}
		return flattened;
	}

	private float[] flattenVector(Vec2[] v) {
		float[] flattened = new float[v.length * 2];
		for (int i = 0; i < v.length; i++) {
			flattened[i * 2 + 0] = v[i].x;
			flattened[i * 2 + 1] = v[i].y;
		}
		return flattened;
	}

	public void updateData(Vec4[] positions, Vec2[] texCoords) {
		glBindVertexArray(vaoID);

		float[] flatPositions = flattenVector(positions);
		glBindBuffer(GL_ARRAY_BUFFER, vertexBufferID);
		glBufferData(GL_ARRAY_BUFFER, flatPositions, GL_STATIC_DRAW);

		float[] flatTextureCoords = flattenVector(texCoords);
		glBindBuffer(GL_ARRAY_BUFFER, textureBufferID); // Tell OpenGL to use specific Vertex Buffer Object (VBO) by binding it
		glBufferData(GL_ARRAY_BUFFER, flatTextureCoords, GL_STATIC_DRAW); // load our data in the appropriate VBO
		
		nVertices = positions.length;
	}

	public void draw() {
		shaders.bind();
		glBindVertexArray(vaoID);
		glUniform1i(shaders.getUniformLocation("textureEnabled"), 1);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, textureID);
		glUniform1i(shaders.getUniformLocation("tex"), 0);
		glDrawArrays(GL_TRIANGLES, 0, nVertices);
	}

	public void destroy() {
		glDeleteVertexArrays(vaoID);
		glDeleteBuffers(vertexBufferID);
		glDeleteBuffers(textureBufferID);
		glDeleteTextures(textureID);
	}

}
