package finalproject;

import glm.vec._4.Vec4;

public class GlmTest {
	public static void testGlmSanity() {
		Vec4 a = new Vec4(0,0,1,1);
		Vec4 b = a.mul_(0.5f);
		if (a.x != b.x*2 || a.y != b.y*2 || a.z != b.z*2 || a.w != b.w*2) {
			throw new RuntimeException("GLM appears corrupt! Contact Leo as soon as possible so he can help you figure out what's going on.");
		}		
	}
	public static void main(String[] args) {
		testGlmSanity();
		System.out.println("GLM appears to be in working order :)");
	}
}
