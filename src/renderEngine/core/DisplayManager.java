package renderEngine.core;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

public class DisplayManager {
    private static int targetFPS;

    public static void CreateDisplay(int width, int height, int targetFps){
        targetFPS = targetFps;
        ContextAttribs attribs = new ContextAttribs(3, 2);
        attribs.withForwardCompatible(true).withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(width, height));
            Display.create(new PixelFormat().withSamples(8));
            GL11.glEnable(GL13.GL_MULTISAMPLE);
        } catch (LWJGLException e) {
            throw new RuntimeException(e);
        }

        GL11.glViewport(0, 0, width, height);
    }

    public static void UpdateDisplay(){
        Display.sync(targetFPS);
        Display.update();
    }

    public static void DestroyDisplay(){
        Display.destroy();
    }
}
