package renderEngine.Model;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import renderEngine.Texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Model {
    int vaoId;

    int vertexCount;

    boolean shouldRenderElements;

    List<Texture> textures;
    boolean textureHasTransparency;

    public Model(int vaoId, int vertexCount, boolean shouldRenderElements) {
        this.vaoId = vaoId;
        this.vertexCount = vertexCount;
        this.shouldRenderElements = shouldRenderElements;
        textures = new ArrayList<>();
    }

    public void addTexture(int id, int slot, boolean hasTransparency){
        if(textures.size() < 32){
            textures.add(new Texture(id, slot));
            textureHasTransparency = hasTransparency;
        }else{
            System.out.println("failed to load texture: " + id + " to model: " + vaoId + " a maximum of 32 textures can be bound at once");
        }
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void bind(){
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);

        if(textureHasTransparency)
            GL11.glDisable(GL11.GL_CULL_FACE);
        else
            GL11.glEnable(GL11.GL_CULL_FACE);

        for(Texture tex : textures){
            GL13.glActiveTexture(GL13.GL_TEXTURE0 + tex.getSlot());
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, tex.getId());
        }
    }

    public List<Texture> getTextures() {
        return textures;
    }

    public void unbind(){
        GL30.glBindVertexArray(0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    public boolean isShouldRenderElements() {
        return shouldRenderElements;
    }

    public boolean isTextureHasTransparency() {
        return textureHasTransparency;
    }
}
