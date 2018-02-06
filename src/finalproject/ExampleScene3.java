package finalproject;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.util.Arrays;
import java.util.Stack;
import java.util.Vector;

import org.lwjgl.opengl.GL;

import glm.mat._4.Mat4;
import glm.quat.Quat;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;

public class ExampleScene3 extends Scene {

	private static String vertShaderFile = "src/finalproject/SkyVertShader.glsl";
	private static String fragShaderFile = "src/finalproject/SkyFragShader.glsl";

	private static ShaderProgram shaders;
	private static Mat4 projectionMatrix = new Mat4().perspective(45, 1, 1, 1000);
	private static Stack<Mat4> modelViewMatrixStack = new Stack<Mat4>();
	private static Mat4 modelViewMatrix = new Mat4().identity();
	
	private static float yaw;
	private static Vec3 camPos;
	private static Vec3 centerPos;
	private boolean zoomingOut;

	// SKYBOX DATA
	private static TexturedMesh tMesh;

	// TORUS DATA
	private static NormalMesh torusNormalMesh;
	private static NormalMesh torusNormalMesh3;
	private static NormalMesh torusNormalMesh4;
	private static NormalMesh torusNormalMesh5;

	// SPHERE DATA
	private static NormalMesh sphereNormalMesh;
	private static NormalMesh sphereNormalMesh2;
	private static NormalMesh sphereNormalMesh3;
	private static NormalMesh sphereNormalMesh4;
	private static NormalMesh sphereNormalMesh5;
	private static NormalMesh sphereNormalMesh6;
	private static NormalMesh sphereNormalMesh7;
	private static NormalMesh sphereNormalMesh8;

	public ExampleScene3(float startTime) {
		super(startTime);
	}

	@Override
	public void init(Window window) {
		GL.createCapabilities();

		shaders = new ShaderProgram();
		shaders.attachVertexShaderFile(vertShaderFile);
		shaders.attachFragmentShaderFile(fragShaderFile);
		shaders.link();
		shaders.bind();
		glDepthFunc(GL_LESS);
		glEnable(GL_DEPTH_TEST);
		updateMatrixUniforms();

		// KEY CONTROLS
		yaw = 0;
		centerPos = new Vec3(0, 0, 0);
		camPos = new Vec3(0, 0, 15);

		// SKY BOX INITIALIZATION
		tMesh = new TexturedMesh(shaders, new ImageFile("src/finalproject/pinkgalaxy.png"));
		buildSkybox(tMesh);

		// TORUS INITIALIZATION
		torusNormalMesh = new NormalMesh(shaders);
		Vec4 torusBodyColor = new Vec4(1, 0, 0.7, 1); // purple around yellow
		buildTorus(torusNormalMesh, 7f, 0.5f, torusBodyColor);
		
		// TORUS 3 INITIALIZATION
		torusNormalMesh3 = new NormalMesh(shaders);
		Vec4 torusBodyColor3 = new Vec4(1, 0, 0, 1); // red around blue
		buildTorus(torusNormalMesh3, 10f, 1f, torusBodyColor3);
		
		// TORUS 4 INITIALIZATION
		torusNormalMesh4 = new NormalMesh(shaders);
		Vec4 torusBodyColor4 = new Vec4(1, 0.9, 0.1, 1); // neon turquoise around mint blue
		buildTorus(torusNormalMesh4, 11f, 1f, torusBodyColor4);
		
		// TORUS 5 INITIALIZATION
		torusNormalMesh5 = new NormalMesh(shaders);
		Vec4 torusBodyColor5 = new Vec4(0.7, 1, 0.2, 1); // neon green around purple-pink #8 sphere
		buildTorus(torusNormalMesh5, 18f, 1f, torusBodyColor5);
		
		/****************************************************************************************/
		
		// SPHERE INITIALIZATION
		sphereNormalMesh = new NormalMesh(shaders);
		float[] centerCoords = new float[] { 0, 0, 0 };
		Vec4 sphereColor = new Vec4(0.9, 1, 0.1, 1); // main yellow with purple ring
		buildSphere(sphereNormalMesh, 5f, centerCoords, sphereColor);

		// SPHERE 2 INITIALIZATION
		sphereNormalMesh2 = new NormalMesh(shaders);
		float[] centerCoords2 = new float[] { 15, 15, 15 };
		Vec4 sphereColor2 = new Vec4(0.6, 0.1, 1, 1); // purple
		buildSphere(sphereNormalMesh2, 3f, centerCoords2, sphereColor2);
		
		// SPHERE 3 INITIALIZATION
		sphereNormalMesh3 = new NormalMesh(shaders);
		float[] centerCoords3 = new float[] { -10, -10, -10 };
		Vec4 sphereColor3 = new Vec4(0.9, 1, 0.6, 1);  // pastel green-mint
		buildSphere(sphereNormalMesh3, 2f, centerCoords3, sphereColor3);
		
		// SPHERE 4 INITIALIZATION
		sphereNormalMesh4 = new NormalMesh(shaders);
		float[] centerCoords4 = new float[] { 40, -30, -50 };
		Vec4 sphereColor4 = new Vec4(0.8, 0.7, 0, 1); // mustard
		buildSphere(sphereNormalMesh4, 6f, centerCoords4, sphereColor4);
		
		// SPHERE 5 INITIALIZATION
		sphereNormalMesh5 = new NormalMesh(shaders);
		float[] centerCoords5 = new float[] { 40, -50, -20 };
		Vec4 sphereColor5 = new Vec4(0.8, 1, 0.9, 1); // mint blue with neon turquoise ring #4 torus
		buildSphere(sphereNormalMesh5, 5f, centerCoords5, sphereColor5);
		
		// SPHERE 6 INITIALIZATION
		sphereNormalMesh6 = new NormalMesh(shaders);
		float[] centerCoords6 = new float[] { -30, 30, -10 };
		Vec4 sphereColor6 = new Vec4(0.9,0.4,0.1, 1); // orange with yellow #6 torus
		buildSphere(sphereNormalMesh6, 10f, centerCoords6, sphereColor6);
		
		// SPHERE 7 INITIALIZATION
		sphereNormalMesh7 = new NormalMesh(shaders);
		float[] centerCoords7 = new float[] { -10, 60, 50 };
		Vec4 sphereColor7 = new Vec4(0, 0.3, 1, 1); // blue
		buildSphere(sphereNormalMesh7, 8f, centerCoords7, sphereColor7);
		
		// SPHERE 8 INITIALIZATION
		sphereNormalMesh8 = new NormalMesh(shaders);
		float[] centerCoords8 = new float[] { -20, -40, 50 };
		Vec4 sphereColor8 = new Vec4(0.8, 0.4, 0.8, 1);  //pink-purple with #5 torus
		buildSphere(sphereNormalMesh8, 15f, centerCoords8, sphereColor8);
		
		zoomingOut = window.isKeyPressed(GLFW_KEY_MINUS);
	}

	@Override
	public void renderFrame(Window window, float musicTime) {

		if (!zoomingOut) {
			camPos.z += .15f;
		}
		
		if(camPos.z >= 187f && yaw >= -0.96) {
			zoomingOut = true;
			yaw -= 0.001f;
		}
		
		if (window.getRuntime() >= 60 && window.getRuntime() <= 82) {
			camPos.y -= 0.1f;
		}
		if (window.getRuntime() >= 85 && window.getRuntime() <= 90) {
			camPos.z -= .23f;
		}
		if (window.getRuntime() >= 90) {
			yaw += 0.001f;
		}
		
		glClearColor(0, 0, 0, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glDisable(GL_DEPTH_TEST);
		modelViewMatrix.identity();
		modelViewMatrix.lookAt(camPos, centerPos, new Vec3(0, 1, 0));
		modelViewMatrix.rotate(yaw, 0, 1, 0);
		updateMatrixUniforms();
		tMesh.draw();
		glEnable(GL_DEPTH_TEST);

		modelViewMatrix.identity();
		modelViewMatrix.lookAt(camPos, centerPos, new Vec3(0, 1, 0));
		modelViewMatrix.rotate(yaw, 0, 1, 0);
		updateMatrixUniforms();
		
		sphereNormalMesh.draw();
		sphereNormalMesh2.draw();
		sphereNormalMesh3.draw();
		sphereNormalMesh4.draw();
		sphereNormalMesh5.draw();
		sphereNormalMesh6.draw();
		sphereNormalMesh7.draw();	
		sphereNormalMesh8.draw();
		
		//1***
		pushModelViewMatrix();
		modelViewMatrix.translate(0, 0, 0);
		modelViewMatrix.rotate((float) Math.cos(window.getRuntime()), (float) Math.sin(window.getRuntime()), 1, 0);
		updateMatrixUniforms();
		torusNormalMesh.draw(); // purple torus, yellow sphere
		popModelViewMatrix();	

		pushModelViewMatrix();
		popModelViewMatrix();
		
		//3***
		pushModelViewMatrix();
		modelViewMatrix.translate(40, -50, -20);
		modelViewMatrix.rotate((float) Math.sin(window.getRuntime()), 0, 1, 0);
		updateMatrixUniforms();
		torusNormalMesh3.draw(); // red torus, blue sphere
		popModelViewMatrix();	

		pushModelViewMatrix();
		popModelViewMatrix();
		
		//5***
		pushModelViewMatrix();
		modelViewMatrix.translate(-20, -40, 50);
		modelViewMatrix.rotate(1f, (float) Math.sin(window.getRuntime()), 1, (float) Math.cos(window.getRuntime()));
		updateMatrixUniforms();
		torusNormalMesh5.draw(); // neon-green torus, purple-pink sphere
		popModelViewMatrix();		
		
		pushModelViewMatrix();
		popModelViewMatrix();
		
		//6***
		pushModelViewMatrix();
		modelViewMatrix.translate(-10, 60, 50);
		modelViewMatrix.rotate((float) Math.cos(window.getRuntime()), (float) Math.cos(window.getRuntime()), 1, 0);
		updateMatrixUniforms();
		torusNormalMesh4.draw(); // yellow torus, blue sphere
		popModelViewMatrix();
		
		modelViewMatrix.identity();
		modelViewMatrix.lookAt(camPos, centerPos, new Vec3(0, 1, 0));
		modelViewMatrix.rotate(yaw, 0, 1, 0);
		updateMatrixUniforms();
	}


	private static void buildTorus(NormalMesh normalMesh, float R, float r, Vec4 ambientColor) {
		Vector<Vec4> positions = new Vector<Vec4>();
		Vector<Vec4> colors = new Vector<Vec4>();
		Vector<Vec4> normals = new Vector<Vec4>();

		int N = 50;
		int n = 50;
		int maxn = 50;

		n = Math.min(n, maxn - 1);
		N = Math.min(N, maxn - 1);

		float rr = 1.5f * r;
		double dv = 2 * Math.PI / n;
		double dw = 2 * Math.PI / N;
		double v = 0.0f;
		double w = 0.0f;

		while (w < 2 * Math.PI + dw) {
			v = 0.0f;
			while (v < 2 * Math.PI + dv) {
				normals.add(new Vec4(
						((R + rr * Math.cos(v)) * Math.cos(w) - (R + r * Math.cos(v)) * Math.cos(w)) +1.0,
						((R + rr * Math.cos(v)) * Math.sin(w) - (R + r * Math.cos(v)) * Math.sin(w)) +1.0,
						(float) ((rr * Math.sin(v) - r * Math.sin(v))) +1.0, 1));
				positions.add(new Vec4(
						((R + r * Math.cos(v)) * Math.cos(w)),
						((R + r * Math.cos(v)) * Math.sin(w)), 
						(r * Math.sin(v)), 1));
				colors.add(ambientColor);

				normals.add(new Vec4(
						((R + rr * Math.cos(v + dv)) * Math.cos(w + dw) - (R + r * Math.cos(v + dv)) * Math.cos(w + dw)) +1.0,
						((R + rr * Math.cos(v + dv)) * Math.sin(w + dw) - (R + r * Math.cos(v + dv)) * Math.sin(w + dw)) +1.0,
						(rr * Math.sin(v + dv) - r * Math.sin(v + dv)) +1.0, 1));
				positions.add(new Vec4(
						((R + r * Math.cos(v + dv)) * Math.cos(w + dw)), 
						((R + r * Math.cos(v + dv)) * Math.sin(w + dw)),
						(r * Math.sin(v + dv)), 1));
				colors.add(ambientColor);
				v += dv;
			} 
			w += dw;
		}
		normalMesh.updateData(positions, colors, normals);
	}

	
	private void buildSphere(NormalMesh normalMesh, float r, float[] centerCoords, Vec4 sphereColor) {
		Vector<Vec4> positions = new Vector<Vec4>();
		Vector<Vec4> colors = new Vector<Vec4>();
		Vector<Vec4> normals = new Vector<Vec4>();

		float cx = centerCoords[0];
		float cy = centerCoords[1];
		float cz = centerCoords[2];
		int precision = 100;

		float theta1 = 0.0f, theta2 = 0.0f, theta3 = 0.0f;
		float ex = 0.0f, ey = 0.0f, ez = 0.0f;
		float px = 0.0f, py = 0.0f, pz = 0.0f;

		for (int i = 0; i < precision / 2; ++i) {
			theta1 = (float) (i * (Math.PI * 2) / precision - Math.PI / 2);
			theta2 = (float) ((i + 1) * (Math.PI * 2) / precision - Math.PI / 2);

			for (int j = 0; j <= precision; ++j) {
				theta3 = (float) (j * (Math.PI * 2) / precision);

				ex = (float) (Math.cos(theta2) * Math.cos(theta3));
				ey = (float) Math.sin(theta2);
				ez = (float) (Math.cos(theta2) * Math.sin(theta3));
				px = cx + r * ex;
				py = cy + r * ey;
				pz = cz + r * ez;
				positions.add(new Vec4(px, py, pz, 1));
				normals.add(new Vec4(ex + 1.5, ey + 1.5, ez + 1.5, 1));
				colors.add(sphereColor);

				ex = (float) (Math.cos(theta1) * Math.cos(theta3));
				ey = (float) Math.sin(theta1);
				ez = (float) (Math.cos(theta1) * Math.sin(theta3));
				px = cx + r * ex;
				py = cy + r * ey;
				pz = cz + r * ez;
				positions.add(new Vec4(px, py, pz, 1));
				normals.add(new Vec4(ex + 1.5, ey + 1.5, ez + 1.5, 1));
				colors.add(sphereColor);
			}
		}
		normalMesh.updateData(positions, colors, normals);
	}

	
	private void buildSkybox(TexturedMesh tMesh) {
		Vec4[] positions = new Vec4[] { new Vec4(-250, -250, -250, 1), new Vec4(250, -250, -250, 1),
				new Vec4(250, 250, -250, 1), new Vec4(-250, -250, -250, 1), new Vec4(-250, 250, -250, 1),
				new Vec4(250, 250, -250, 1), new Vec4(-250.0, -250.0, 250.0, 1), new Vec4(250.0, -250.0, 250.0, 1),
				new Vec4(250.0, 250.0, 250.0, 1), new Vec4(-250.0, -250.0, 250.0, 1), new Vec4(-250.0, 250.0, 250.0, 1),
				new Vec4(250.0, 250.0, 250.0, 1), new Vec4(250.0, -250.0, -250.0, 1), new Vec4(250.0, 250.0, -250.0, 1),
				new Vec4(250.0, 250.0, 250.0, 1), new Vec4(250.0, -250.0, -250.0, 1), new Vec4(250.0, -250.0, 250.0, 1),
				new Vec4(250.0, 250.0, 250.0, 1), new Vec4(-250.0, -250.0, -250.0, 1),
				new Vec4(-250.0, 250.0, -250.0, 1), new Vec4(-250.0, 250.0, 250.0, 1),
				new Vec4(-250.0, -250.0, -250.0, 1), new Vec4(-250.0, -250.0, 250.0, 1),
				new Vec4(-250.0, 250.0, 250.0, 1), new Vec4(-250.0, 250.0, -250.0, 1),
				new Vec4(250.0, 250.0, -250.0, 1), new Vec4(250.0, 250.0, 250.0, 1), new Vec4(-250.0, 250.0, -250.0, 1),
				new Vec4(-250.0, 250.0, 250.0, 1), new Vec4(250.0, 250.0, 250.0, 1),
				new Vec4(-250.0, -250.0, -250.0, 1), new Vec4(250.0, -250.0, -250.0, 1),
				new Vec4(250.0, -250.0, 250.0, 1), new Vec4(-250.0, -250.0, -250.0, 1),
				new Vec4(-250.0, -250.0, 250.0, 1), new Vec4(250.0, -250.0, 250.0, 1) };

		Vec2[] texCoords = new Vec2[] { new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1), new Vec2(0, 0), new Vec2(1, 0),
				new Vec2(1, 1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1), new Vec2(0, 0), new Vec2(1, 0),
				new Vec2(1, 1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1), new Vec2(0, 0), new Vec2(1, 0),
				new Vec2(1, 1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1), new Vec2(0, 0), new Vec2(1, 0),
				new Vec2(1, 1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1), new Vec2(0, 0), new Vec2(1, 0),
				new Vec2(1, 1), new Vec2(0, 0), new Vec2(0, 1), new Vec2(1, 1), new Vec2(0, 0), new Vec2(1, 0),
				new Vec2(1, 1) };
		tMesh.updateData(positions, texCoords);
	}
	
	
	private static void updateMatrixUniforms() {
		shaders.bind();
		glUniformMatrix4fv(shaders.getUniformLocation("projectionMatrix"), false, projectionMatrix.toDfb_());
		glUniformMatrix4fv(shaders.getUniformLocation("modelviewMatrix"), false, modelViewMatrix.toDfb_());
		glUniformMatrix4fv(shaders.getUniformLocation("normalMatrix"), false,
				modelViewMatrix.inverse().transpose().toDfb_());
	}

	/*
	 * You can save the current state of the modelview matrix for later
	 * by calling this function
	 */
	private static void pushModelViewMatrix() {
		modelViewMatrixStack.push(modelViewMatrix);
		modelViewMatrix = new Mat4().set(modelViewMatrix);
		updateMatrixUniforms();	
	}

	/*
	 * You can restore the previous state of the modelview matrix
	 * by calling this function
	 */
	private static void popModelViewMatrix() {
		modelViewMatrix = modelViewMatrixStack.pop();
		updateMatrixUniforms();
	}
	
	@Override
	public void destroy(Window window) {
		// Nothing to tear down...
	}
}
