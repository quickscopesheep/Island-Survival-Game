package Game.Level;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.Entity.Entity;
import renderEngine.Model.Model;
import renderEngine.Model.Renderable;
import renderEngine.Shader.ShaderProgram;
import renderEngine.Util.FrameBuffer;
import renderEngine.Util.MathUtil;
import renderEngine.Util.MeshPrimatives;
import renderEngine.Util.NoiseGenerator;
import renderEngine.core.Renderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Terrain extends Entity {
    public static final float TILE_SIZE = 1f;
    public static final float TEXTURE_SCALE = .15f;
    public static final float SEA_LEVEL = 0;
    public static final float WATER_SCALE = 2;

    FrameBuffer waterRefractionBuffer;

    Renderable terrainRenderable;
    Renderable waterRenderable;

    int width, height;
    int seed;

    float waveTime;

    NoiseGenerator noise1;
    NoiseGenerator noise2;

    public Terrain(int width, int height, int seed, Renderer renderer){
        super(new Vector3f(0, 0, 0), null);

        this.width = width;
        this.height = height;
        this.seed = seed;

        waterRefractionBuffer = new FrameBuffer(Display.getWidth(), Display.getHeight(), FrameBuffer.DEPTH_TEXTURE);

        ShaderProgram shader = new ShaderProgram("res/shaders/terrainVert.glsl", "res/shaders/terrainFrag.glsl", renderer);
        shader.bind();
        shader.setUniform("flat_texture", 0);
        shader.setUniform("hill_texture", 1);
        shader.setUniform("sand_texture", 2);

        shader.setUniform("sea_height", SEA_LEVEL);

        System.out.println("Generating Terrain Mesh");
        Model model = generateTerrainMesh(renderer);
        System.out.println("Generated Terrain Mesh");

        model.addTexture(renderer.getLoader().loadTexture("res/textures/terrain/grass.jpg"), 0, false);
        model.addTexture(renderer.getLoader().loadTexture("res/textures/terrain/dirt.jpg"), 1, false);
        model.addTexture(renderer.getLoader().loadTexture("res/textures/terrain/sand.jpg"), 2, false);

        terrainRenderable = new Renderable(shader, model);
        System.out.println("Generating Water Mesh");
        waterRenderable = generateWaterRenderable(renderer);
        System.out.println("Generated Water Mesh");
    }

    @Override
    public void tick(float delta) {
        waveTime += delta;
        waterRenderable.getShader().bind();
        waterRenderable.getShader().setUniform("waveTime", waveTime);
    }

    @Override
    public void render(Renderer renderer) {
        renderer.render(terrainRenderable, MathUtil.identityMatrix);

        waterRefractionBuffer.bindFrameBuffer();
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glEnable(GL11.GL_CLIP_PLANE0);
        renderer.setClipPlane(new Vector4f(0, -1, 0, SEA_LEVEL));

        renderer.render(terrainRenderable, MathUtil.identityMatrix);
        waterRefractionBuffer.unbindFrameBuffer();

        GL11.glDisable(GL11.GL_CLIP_PLANE0);
        renderer.setClipPlane(new Vector4f(0, -1, 0, 1000));

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        renderer.render(waterRenderable, MathUtil.identityMatrix);

        GL11.glDisable(GL11.GL_BLEND);
    }

    Model generateTerrainMesh(Renderer renderer){
        float[] vertices = new float[((width + 1) * (height+1))*3];
        float[] normals = new float[((width + 1) * (height+1))*3];
        float[] texCoords = new float[((width + 1) * (height+1))*2];

        System.out.println("Generating Height Maps");

        noise1 = new NoiseGenerator(seed, 100, .7f, 2f, 5, 75);
        noise2 = new NoiseGenerator(seed, 150, .4f, 2f, 4, 20);

        System.out.println("Generated Height Maps");

        System.out.println("Building Terrain Mesh");
        int i = 0;
        for(int y = 0; y <= height; y++){
            for(int x = 0; x <= width; x++){
                vertices[(i*3)] = x * TILE_SIZE - (width*TILE_SIZE)/2; //x axis
                vertices[(i*3)+1] = getNoiseValue(x, y); //y axis
                vertices[(i*3)+2] = y * TILE_SIZE - (height*TILE_SIZE)/2; //z axis

                texCoords[(i*2)] = x * TEXTURE_SCALE;
                texCoords[(i*2)+1] = y*TEXTURE_SCALE;

                Vector3f normal = calculateNormal(x, y);

                normals[(i*3)] = normal.x;
                normals[(i*3)+1] = normal.y;
                normals[(i*3)+2] = normal.z;

                i++;
            }
        }

        int[] indices = new int[width * height * 6];

        int vert = 0;
        int tris = 0;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                indices[tris] = vert;
                indices[tris+1] = vert+width+1;
                indices[tris+2] = vert+1;
                indices[tris+3] = vert+1;
                indices[tris+4] = vert+width+1;
                indices[tris+5] = vert+width+2;

                vert++;
                tris+=6;
            }
            vert++;
        }
        System.out.println("Built Terrain Mesh");

        return renderer.getLoader().loadToVAO(vertices, texCoords, indices, normals);
    }

    Renderable generateWaterRenderable(Renderer renderer){

    float[] vertices = new float[((width + 1) * (height+1))*3];
    float[] normals = new float[((width + 1) * (height+1))*3];
    float[] texCoords = new float[((width + 1) * (height+1))*2];

    int i = 0;
    for(int y = 0; y <= height; y++){
        for(int x = 0; x <= width; x++){
            vertices[(i*3)] = x * TILE_SIZE*WATER_SCALE - (width*TILE_SIZE*WATER_SCALE)/2; //x axis
            vertices[(i*3)+1] = SEA_LEVEL; //y axis
            vertices[(i*3)+2] = y * TILE_SIZE*WATER_SCALE - (height*TILE_SIZE*WATER_SCALE)/2; //z axis

            texCoords[(i*2)] = x * TEXTURE_SCALE;
            texCoords[(i*2)+1] = y * TEXTURE_SCALE;

            normals[(i*3)] = 0;
            normals[(i*3)+1] = 1;
            normals[(i*3)+2] = 0;

            i++;
        }
    }

    int[] indices = new int[width * height * 6];

    int vert = 0;
    int tris = 0;
    for(int y = 0; y < height; y++){
        for(int x = 0; x < width; x++){
            indices[tris] = vert;
            indices[tris+1] = vert+width+1;
            indices[tris+2] = vert+1;
            indices[tris+3] = vert+1;
            indices[tris+4] = vert+width+1;
            indices[tris+5] = vert+width+2;

            vert++;
            tris+=6;
        }
        vert++;
    }

        ShaderProgram waterShader = new ShaderProgram("res/shaders/waterVert.glsl", "res/shaders/waterFrag.glsl", renderer);
        waterShader.bind();
        waterShader.setUniform("colourTexture", 0);
        waterShader.setUniform("depthTexture", 1);
        waterShader.setUniform("dudvtexture", 2);
        waterShader.setUniform("normalMap", 3);

        Model waterModel = renderer.getLoader().loadToVAO(vertices, texCoords, indices, normals);
        waterModel.addTexture(waterRefractionBuffer.getColourTexture(), 0, false);
        waterModel.addTexture(waterRefractionBuffer.getDepthTexture(), 1, false);
        waterModel.addTexture(renderer.getLoader().loadTexture("res/textures/water/water_dudv.png"), 2, false);
        waterModel.addTexture(renderer.getLoader().loadTexture("res/textures/water/water_normal.png"), 3, false);
        return new Renderable(waterShader, waterModel);
    }

    private Vector3f calculateNormal(int x, int y){
        float heightL = getNoiseValue(x-1,y);
        float heightR = getNoiseValue(x+1,y);
        float heightU = getNoiseValue(x,y+1);
        float heightD = getNoiseValue(x,y-1);

        Vector3f normal = new Vector3f(heightL-heightR, 2f, heightD-heightU);
        normal.normalise();

        return normal;
    }

    float getNoiseValue(int x, int y){
        float value1 = noise1.generateHeight(x, y);
        float value2 = noise2.generateHeight(x, y);

        return MathUtil.lerp(value1, value2, 0.3f) - getFalloff(x, y)*20;
    }

    float getFalloff(int x, int y){
        float sampleX = x / (float)width * 2 - 1;
        float sampleY = y / (float)height * 2 - 1;

        return Math.max(Math.abs(sampleX), Math.abs(sampleY));
    }

}
