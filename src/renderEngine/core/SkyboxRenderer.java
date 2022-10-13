package renderEngine.core;

import org.lwjgl.opengl.*;
import renderEngine.Entity.Camera;
import renderEngine.Model.Model;
import renderEngine.Shader.ShaderProgram;
import renderEngine.Util.MathUtil;

public class SkyboxRenderer {
    private static final float SIZE = 500f;

    private static final float[] VERTICES = {
            -SIZE,  SIZE, -SIZE,
            -SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE, -SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,

            -SIZE, -SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE, -SIZE,  SIZE,
            -SIZE, -SIZE,  SIZE,

            -SIZE,  SIZE, -SIZE,
            SIZE,  SIZE, -SIZE,
            SIZE,  SIZE,  SIZE,
            SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE,  SIZE,
            -SIZE,  SIZE, -SIZE,

            -SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE, -SIZE,
            SIZE, -SIZE, -SIZE,
            -SIZE, -SIZE,  SIZE,
            SIZE, -SIZE,  SIZE
    };

    private static final String[] paths = {"res/textures/skybox/side.png", "res/textures/skybox/side.png", "res/textures/skybox/top.png", "res/textures/skybox/bottom.png",
            "res/textures/skybox/side.png", "res/textures/skybox/side.png"};

    Model model;
    int texture;
    ShaderProgram shader;

    public SkyboxRenderer(Renderer masterRenderer){
        model = masterRenderer.getLoader().loadToVAO(VERTICES, 3);
        texture = masterRenderer.loader.loadCubeMap(paths);

        shader = new ShaderProgram("res/shaders/skyboxVert.glsl", "res/shaders/skyboxFrag.glsl", masterRenderer);
        shader.setUniform("cubemap", 0);
    }

    public void render(Camera camera){
        shader.bind();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);

        shader.setUniform("viewMatrix", MathUtil.createViewMatrix(camera));

        GL30.glBindVertexArray(model.getVaoId());
        GL20.glEnableVertexAttribArray(0);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, model.getVertexCount());

        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);

        shader.unbind();
    }

    public void cleanUp(){
        shader.unbind();
        shader.cleanUp();
    }
}
