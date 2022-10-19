package renderEngine.gui;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import renderEngine.Model.Model;
import renderEngine.Shader.ShaderProgram;
import renderEngine.Util.MathUtil;
import renderEngine.core.Renderer;

public class GuiRenderer {
    Model quad;
    ShaderProgram guiShader;

    public static int GUI_TEX_BASE;

    public GuiRenderer(Renderer renderer){
        float[] positions = {-1, 1, -1, -1, 1, 1, 1, -1};
        quad = renderer.getLoader().loadToVAO(positions, 2);
        guiShader = new ShaderProgram("res/shaders/guiVert.glsl", "res/shaders/guiFrag.glsl", renderer);
        guiShader.bind();
        guiShader.setUniform("guiTexture", 0);

        GUI_TEX_BASE = renderer.getLoader().loadTexture("res/textures/gui/gui-base.png");
    }

    public void renderGui(GuiElement element){
        GL30.glBindVertexArray(quad.getVaoId());
        GL20.glEnableVertexAttribArray(0);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, element.getTexture());

        guiShader.bind();

        guiShader.setUniform("tint", element.getTint());

        Matrix4f transform = MathUtil.createTransformationMatrix(element.position, element.scale);
        guiShader.setUniform("transformationMatrix", transform);

        GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, quad.getVertexCount());

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_DEPTH_TEST);

        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);

        guiShader.unbind();
    }

    void cleanUp(){
        guiShader.cleanUp();
    }
}
