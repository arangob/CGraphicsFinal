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

public class ExampleScene1 extends Scene {

	private static String vertShaderFile = "src/finalproject/RadialGradientVertShader.glsl";
	private static String fragShaderFile = "src/finalproject/RadialGradientFragShader.glsl";

	private static ShaderProgram shaders;
	private static Mat4 projectionMatrix = new Mat4().perspective(45, 1, 1, 1000);
	private static Stack<Mat4> modelViewMatrixStack = new Stack<Mat4>();
	private static Mat4 modelViewMatrix = new Mat4().identity();
	
	private static float yaw;
	private static Vec3 camPos;
	private static Vec3 centerPos;

	// SKYBOX DATA
	private static TexturedMesh tMesh;

	// TORUS DATA
	private static RadialNormalMesh torusNormalMesh;

	// SPHERE DATA
	private static RadialNormalMesh sphereNormalMesh;

	public ExampleScene1(float startTime) {
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
		yaw = (float) Math.toRadians(45);
		centerPos = new Vec3(0, 0, 0);
		camPos = new Vec3(0, 0, 150);
		
		// SKY BOX INITIALIZATION
		tMesh = new TexturedMesh(shaders, new ImageFile("src/finalproject/pinkgalaxy.png"));
		buildSkybox(tMesh);

		// TORUS INITIALIZATION
		torusNormalMesh = new RadialNormalMesh(shaders);
		torusNormalMesh.startColor = new Vec4(0.7, 0.8, 1, 1);
		torusNormalMesh.endColor = new Vec4(0.7, 0.3, 1, 1);
		torusNormalMesh.c = new Vec2(1, 1);
		torusNormalMesh.r = 2.5f;
		Vec4 torusBodyColor = new Vec4(1, 0, 0.7, 1); // purple around yellow
		buildTorus(torusNormalMesh, 7f, 0.5f, torusBodyColor);
		
		// SPHERE INITIALIZATION
		sphereNormalMesh = new RadialNormalMesh(shaders);
		sphereNormalMesh.startColor = new Vec4(0.8, 0, 1, 1);
		sphereNormalMesh.endColor = new Vec4(0.7, 1, 1, 1);
		sphereNormalMesh.c = new Vec2(1, 1);
		sphereNormalMesh.r = 0.9f;
		float[] centerCoords = new float[] { 0, 0, 0 };
		Vec4 sphereColor = new Vec4(0.9, 1, 0.1, 1); // main yellow
		buildSphere(sphereNormalMesh, 5f, centerCoords, sphereColor);
	}

	@Override
	public void renderFrame(Window window, float musicTime) {
		if (window.isKeyPressed(GLFW_KEY_MINUS)) {
			camPos.z += .2f;
			yaw -= 0.02;
		} else if (!window.isKeyPressed(GLFW_KEY_EQUAL)) {
			camPos.z -= 0.07f;
			yaw += 0.007;
		}		glClearColor(0, 0, 0, 1.0f);
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		glDisable(GL_DEPTH_TEST);
		modelViewMatrix.identity();
		modelViewMatrix.lookAt(camPos, centerPos, new Vec3(0, 1, 0));
		updateMatrixUniforms();
		tMesh.draw();
		glEnable(GL_DEPTH_TEST);
		
		sphereNormalMesh.draw();
		
		pushModelViewMatrix();
		modelViewMatrix.rotate(yaw, 0, 1, 0);
		updateMatrixUniforms();
		torusNormalMesh.draw();
		popModelViewMatrix();
	}

	private static void updateMatrixUniforms() {
		shaders.bind();
		glUniformMatrix4fv(shaders.getUniformLocation("projectionMatrix"), false, projectionMatrix.toDfb_());
		glUniformMatrix4fv(shaders.getUniformLocation("modelviewMatrix"), false, modelViewMatrix.toDfb_());
		glUniformMatrix4fv(shaders.getUniformLocation("normalMatrix"), false,
				modelViewMatrix.inverse().transpose().toDfb_());
	}

	private static void buildTorus(RadialNormalMesh normalMesh, float R, float r, Vec4 ambientColor) {
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

	private void buildSphere(RadialNormalMesh normalMesh, float r, float[] centerCoords, Vec4 sphereColor) {
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
