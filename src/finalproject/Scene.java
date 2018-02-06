package finalproject;

import java.util.Comparator;

public abstract class Scene {
	/***************
	 * A class representing a generic scene that might be
	 * rendered during our demo.
	 * 
	 * Create child classes of this class that implement the
	 * abstract methods below
	 */
	public float startTime;
	public Scene (float startTime) {
		this.startTime = startTime;
	}
	public static class SortByStartTime implements Comparator<Scene>
	{
	    public int compare(Scene a, Scene b)
	    {
	        return (int) (a.startTime - b.startTime);
	    }
	}
	
	/***************
	 * Called right before the first renderFrame.
	 * This function should contain the code that comes before the run loop
	 * 
	 * @param window - The demo's window object
	 */
	public abstract void init(Window window);

	/***************
	 * Called within the run loop, roughly 60 times per second. Expected
	 * to draw the next frame to the screen.
	 * 
	 * @param window - The demo's window object (what you should draw to)
	 * @param musicTime - The number of seconds ellapsed since the start of the
	 * 						background music
	 */
	public abstract void renderFrame(Window window, float musicTime);
	
	/***************
	 * Called right after the last call to renderFrame.
	 * This function should contain the code that comes after the run loop
	 * 
	 * @param window - The demo's window object
	 */
	public abstract void destroy(Window window);
}
