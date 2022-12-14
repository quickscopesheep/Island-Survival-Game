package Game;

import Game.Level.Terrain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Entity.Camera;
import renderEngine.Entity.Entity;
import renderEngine.Entity.Light;
import renderEngine.Model.Model;
import renderEngine.Model.Renderable;
import renderEngine.Shader.ShaderProgram;
import renderEngine.core.DisplayManager;
import renderEngine.core.Renderer;
import renderEngine.gui.GuiElement;
import renderEngine.gui.GuiRenderer;

import java.util.ArrayList;
import java.util.List;

public class Game {
    public static Renderer renderer;
    public static Camera camera;
    public static Terrain level;
    public static List<Entity> entities;

    GuiElement crossHair;

    public Game(){
        DisplayManager.CreateDisplay(1280, 720, 120);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        renderer = new Renderer();
        camera = new Camera(new Vector3f(0, 10, 0), 0, 0, 0);

        Light worldLight = new Light(new Vector3f(0, 25, 20), new Vector3f(1, 1, .95f));

        entities = new ArrayList<>();

        renderer.setCamera(camera);
        renderer.setWorldLight(worldLight);
        renderer.setAmbientLight(new Vector3f(.2f, .2f, .2f));
        renderer.setSkyColour(new Vector3f(0.7f, 0.9f, 1f));

        level = new Terrain(300, 300, 1, renderer);
        entities.add(level);

        crossHair = new GuiElement(renderer.getLoader().loadTexture("res/textures/gui/crossHair.png"), new Vector2f(0, 0), new Vector2f(.01f, .01f));

        gameLoop();
    }

    void gameLoop(){
        float now = 0;
        float nanoTime = 1000000000;
        float last = System.nanoTime()/nanoTime;
        while (!Display.isCloseRequested()){
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            now = System.nanoTime()/nanoTime;
            float delta = now - last;

            for (Entity e: entities) {
                e.tick(delta);
            }

            renderer.getSkyboxRenderer().render(camera);
            renderer.renderScene(entities);

            renderer.getGuiRenderer().renderGui(crossHair);

            camera.move();

            DisplayManager.UpdateDisplay();
            last = now;
        }

        renderer.cleanUp();
        DisplayManager.DestroyDisplay();
    }

    public static void main(String[] args){
        new Game();
    }
}
