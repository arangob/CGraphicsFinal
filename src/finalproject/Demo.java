package finalproject;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import org.lwjgl.opengl.GL;

import java.util.Arrays;

public class Demo {
	/***
	 * This class plays an audio track in the background while dispatching
	 * the job of rendering the screen to an array of scenes.
	 * 
	 * For your final project, create a bunch of cool subclasses of Scene
	 * and add them to the sceneList below
	 */
	private static String songFilename = "src/finalproject/BROADCAST_Poem_Of_Dead_Song.ogg"; // TODO: use your own song!
	private static int windowWidth = 500;
	private static int windowHeight = 500;
	private static float fps = 60.0f;
	
	/***
	 * The scenes in this list will be used to draw your demo.
	 * The argument to each constructor indicates the time in the song
	 * that the scene is supposed to start playing (in seconds).
	 * 
	 * Right before a scene begins rendering, its init() method will be called.
	 * After that, its renderFrame() method will be called once per frame.
	 * Finally, right before the next scene starts, the destroy() method will be called.
	 * 
	 * Only one scene will play at a time, and it will play until the next scene
	 * starts (or the program ends).
	 * 
	 * The list will be automatically sorted by scene-start-time before it's used,
	 * but for your own benefit you should probably keep things in sequential order.
	 */
	private static Scene[] sceneList = new Scene[] { // TODO: replace these with your scenes!
			new ExampleScene1(0.00f),
			new ExampleScene3(37.00f),
	};		

	/***
	 * The bulk of the program is just the same kind of run loop you've worked
	 * with a million times before. You shouldn't have to alter this code, unless
	 * you want to.
	 */
	public static void main(String[] args) {
		// Set up window and OpenGL context
		Window window = new Window(windowWidth, windowHeight, "~*~~MiLlS DeMoPaRtY 2017~~*~");
		window.makeActive();
		GL.createCapabilities();
		
		// Set up scene system
		Arrays.sort(sceneList, new Scene.SortByStartTime());		
		int nextSceneIdx = 0;
		Scene activeScene = null;
		
		// Set up and start background music
		SoundFile bgm = new SoundFile(songFilename);
		bgm.play();
		
		// Clear window before first scene plays
		resetGL();
		window.flipBuffers();
		
		// Run loop!!
		float lastTime = bgm.getPlayingTime();
		while(!window.isWindowClosed() && bgm.getPlayingTime() < bgm.getLength()) {
			window.checkForNewEvents();
 
			// Check if it's time to trigger the next scene
			if (nextSceneIdx < sceneList.length && lastTime >= sceneList[nextSceneIdx].startTime) {
				if (activeScene != null) {
					activeScene.destroy(window);
					resetGL();
				}
				activeScene = sceneList[nextSceneIdx];
				activeScene.init(window);
				nextSceneIdx++;
			}

			// If a scene is active, render it
			if (activeScene != null) {
				activeScene.renderFrame(window, lastTime);
			}
			window.flipBuffers();
			
			// FPS limiting code
			float currentTime = bgm.getPlayingTime();
			if (currentTime - lastTime < 1.0/fps) {
				window.sleep((1/fps) - (currentTime-lastTime));
			}
			lastTime = currentTime;
		}

		// Clean up
		if (activeScene != null) {
			activeScene.destroy(window);
		}
		bgm.destroy();
		SoundFile.teardownOpenAL();
		window.destroy();
	}
	
	/***
	 * Clear as much of OpenGL to a known state as possible, to keep your
	 * scenes from interfereing with each other even if they don't properly
	 * clean up after themselves.
	 */
	private static void resetGL() {
		glStencilMask(0xFF);
		glDepthMask(true);
		glColorMask(true, true, true, true);
		glClearColor(0,0,0,1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
		glStencilMask(0);
		
		glDepthFunc(GL_LESS);
		glEnable(GL_DEPTH_TEST);

		glDisable(GL_STENCIL_TEST);
		glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
		glStencilFunc(GL_NEVER, 0, 0);
		
		glDisable(GL_CULL_FACE);
		glFrontFace(GL_CCW);

		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, 0);
		glBindVertexArray(0);
		glUseProgram(0);
	}
}
