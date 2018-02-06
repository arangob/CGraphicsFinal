package finalproject;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.Arrays;
import java.util.Vector;

import glm.vec._3.Vec3;
import glm.vec._4.Vec4;

public class NormalMesh {


	private ShaderProgram shaders;
	private float[] positionData;
	private float[] colorData;
	private float[] normalData;

	private int[] buffers;
	private int vaoID;


	private float[] flattenVector(Vec4[] v) {
		float[] flatVerts = new float[4 * v.length];
		for (int i = 0; i < v.length; i++) {
			flatVerts[i * 4 + 0] = v[i].x;
			flatVerts[i * 4 + 1] = v[i].y;
			flatVerts[i * 4 + 2] = v[i].z;
			flatVerts[i * 4 + 3] = v[i].w;
		}
		return flatVerts;
	}

	public NormalMesh(ShaderProgram shaders) {
		this.shaders = shaders;
		buffers = new int[3]; 
		glGenBuffers(buffers);

		vaoID = glGenVertexArrays();
		glBindVertexArray(vaoID);

		int positionBufferID = buffers[0];
		int colorBufferID = buffers[1];
		int normalBufferID = buffers[2];
		
		glEnableVertexAttribArray(shaders.getAttribLocation("position")); 
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferID); 
		glVertexAttribPointer(shaders.getAttribLocation("position"), 4, GL_FLOAT, false, 0, 0);

		glEnableVertexAttribArray(shaders.getAttribLocation("color"));
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID);
		glVertexAttribPointer(shaders.getAttribLocation("color"), 4, GL_FLOAT, false, 0, 0);
		
		glEnableVertexAttribArray(shaders.getAttribLocation("normal"));
		glBindBuffer(GL_ARRAY_BUFFER, normalBufferID);
		glVertexAttribPointer(shaders.getAttribLocation("normal"), 4, GL_FLOAT, false, 0, 0);
	}

	public void updateData(Vector<Vec4> positions, Vector<Vec4> colors, Vector<Vec4> normals) {
		updateData(positions.toArray(new Vec4[positions.size()]), colors.toArray(new Vec4[positions.size()]), normals.toArray(new Vec4[positions.size()]));
	}

	public void updateData(Vec4[] positions, Vec4[] colors, Vec4[] normals) {
		positionData = flattenVector(positions);
		colorData = flattenVector(colors);
		normalData = flattenVector(normals);

		
		glBindVertexArray(vaoID); 
		
		int positionBufferID = buffers[0]; 
		int colorBufferID = buffers[1]; 
		int normalBufferID = buffers[2];
		
		glBindBuffer(GL_ARRAY_BUFFER, positionBufferID);
		glBufferData(GL_ARRAY_BUFFER, positionData, GL_STATIC_DRAW); 
		glBindBuffer(GL_ARRAY_BUFFER, 0); 
		
		glBindBuffer(GL_ARRAY_BUFFER, colorBufferID); 
		glBufferData(GL_ARRAY_BUFFER, colorData, GL_STATIC_DRAW); 
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		
		glBindBuffer(GL_ARRAY_BUFFER, normalBufferID); 
		glBufferData(GL_ARRAY_BUFFER, normalData, GL_STATIC_DRAW); 
		glBindBuffer(GL_ARRAY_BUFFER, 0); 
	}

	public void draw() {
		shaders.bind();
		glBindVertexArray(vaoID);
		glUniform1i(shaders.getUniformLocation("textureEnabled"), 0);
		glDrawArrays(GL_TRIANGLE_STRIP, 0, positionData.length/4);
	}

	public void destroy() {
		glDeleteBuffers(buffers);
		glDeleteVertexArrays(vaoID);
	}
}
