package finalproject;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;


public class Window {
	/* This class lets us interact with the operating system by
	 * seting up a window we can drag around and draw in using
	 * OpenGL, as well as leting us query keyboard and mouse state.
	 * 
	 * Under the hood, it uses the GLFW library to do all the
	 * heavy lifting:
	 * http://www.glfw.org/docs/latest/
	 */
	
	/*********************************
	 * Window lifetime management
	 *********************************/
	
	/***
	 * Initialize a new OpenGL window of dimensions width x height
	 * (in pixels) and with the specified titlebar caption.
	 */
	public Window(int width, int height, String title) {
		if (nWindows == 0) {
			GLFWErrorCallback.createPrint(System.err).set();

			if (!glfwInit())
				throw new IllegalStateException("Unable to initialize GLFW");
		}
		nWindows++;

		GlmTest.testGlmSanity();
		
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_DEPTH_BITS, 16);
        screenWidth = width;
        screenHeight = height;
        this.title = title;
        
		window = glfwCreateWindow(screenWidth, screenHeight, title, NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		keys = new boolean[65536];
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true);
		    keys[key] = action != GLFW_RELEASE;
		});

		int [] pWidth = new int[1];
		int [] pHeight = new int[1];

		glfwGetWindowSize(window, pWidth, pHeight);

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		glfwSetWindowPos(
			window,
			(vidmode.width() - pWidth[0]) / 2,
			(vidmode.height() - pHeight[0]) / 2
		);
		
		makeActive();
		glfwSetWindowTitle(window, title);

		glfwSwapInterval(0); // VSYNC off

		glfwShowWindow(window);
	}

	/***
	 * If there are multiple window objects present, selects
	 * this particular one to be the recipient of any OpenGL
	 * function calls. If you're using the GpuRasterizer class,
	 * it will take care of this for you.
	 */
	public void makeActive() {
		glfwMakeContextCurrent(window);
	}

	/***
	 * Once you're done with the window, tear it down with this
	 * method.
	 */
	public void destroy() {
		if (window != NULL) {
			glfwFreeCallbacks(window);
			glfwDestroyWindow(window);
	
			nWindows--;
			window = NULL;
			
			if (nWindows == 0) {
				glfwTerminate();
				glfwSetErrorCallback(null).free();
			}
		}
	}

	
	/*********************************
	 * Window run loop methods
	 *********************************/
	
	/***
	 * Our OpenGL window is double buffered - this method flips
	 * the buffer being displayed and the buffer available for
	 * drawing. Call at the end of your run loop.
	 */
	public void flipBuffers() {
		// Every half second, calculate FPS and display it in the title bar
		double currentTime = glfwGetTime();
		
		if (currentTime - lastFpsUpdateTime >= 0.5) {
			double fps = numFramesSinceFpsUpdate / (currentTime - lastFpsUpdateTime);
			lastFpsUpdateTime = currentTime;
			numFramesSinceFpsUpdate = 0;
			glfwSetWindowTitle(window, title + " (FPS: " + Math.round(fps) + ")");
		} else {
			numFramesSinceFpsUpdate += 1;
		}
		
		// Actually flip the buffers
		glfwSwapBuffers(window);
	}

	/***
	 * Scan for new keyboard/window events. This will
	 * affect future calls to isKeyPressed() and isWindowClosed().
	 * If you don't call this in your run loop, your program
	 * will freeze up!!
	 */
	public void checkForNewEvents() {
		glfwPollEvents();
	}
	
	/***
	 * @return True if the user clicked the window's X button or hit
	 * the escape key
	 */
	public boolean isWindowClosed() {
		return glfwWindowShouldClose(window);
	}

	/***
	 * Pause execution of the program for the specified (possibly fractional)
	 * number of seconds
	 */
	public void sleep(double seconds) {
		try {
			Thread.sleep((long)(seconds*1000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/***
	 * Return the number of seconds elapsed since the program first started.
	 */
	public double getRuntime() {
		return glfwGetTime();
	}

	/*********************************
	 * Window input methods
	 *********************************/
	
	/***
	 * @return True if the keyboard key specified by the given keycode
	 * is currently being held down.
	 * 
	 * You can find a list of keycode names and values at:
	 * http://www.glfw.org/docs/latest/group__keys.html
	 * 
	 * You can use these keycode names in your code by importing this module:
	 * import static org.lwjgl.glfw.GLFW.*;
	 * 
	 */
	public boolean isKeyPressed(int glfwKeyCode) {
		return keys[glfwKeyCode];
	}
	
	/***
	 * @return The (x,y) coordinate of the mouse relative to the top-left
	 * of the window. The x coordinate is at index 0, the y coordinate at
	 * index 1.
	 */
	public double[] getMousePosition() {
		double[] xpos = new double[1];
		double[] ypos = new double[1];
		glfwGetCursorPos(window, xpos, ypos);
		return new double[] {xpos[0], ypos[0]};
	}
	
	/***
	 * Set the coordinates of the mouse relative to the top-left of the
	 * window
	 */
	public void setMousePosition(double x, double y) {
		glfwSetCursorPos(window, x, y);
	}

	/*********************************
	 * Window state
	 *********************************/
	
	private static int nWindows = 0;
	
	private String title;

	private double numFramesSinceFpsUpdate;
	private double lastFpsUpdateTime;

	private int screenWidth, screenHeight;
	private long window;
	private boolean[] keys;
}
