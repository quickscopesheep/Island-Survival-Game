package renderEngine.core;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.Entity.Camera;
import renderEngine.Entity.Entity;
import renderEngine.Entity.Light;
import renderEngine.Model.Renderable;
import renderEngine.Shader.ShaderProgram;
import renderEngine.Util.MathUtil;
import renderEngine.gui.GuiRenderer;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    public static float FOV = 70;
    public static float NEAR_PLANE = 0.01f;
    public static float FAR_PLANE = 1000;

    Matrix4f projectionMatrix;

    Loader loader;

    SkyboxRenderer skyboxRenderer;
    GuiRenderer guiRenderer;

    Light worldLight;
    Camera camera;

    Vector3f ambientLight;
    Vector3f skyColour;

    Vector4f clipPlane;

    List<ShaderProgram> shaders;

    public Renderer(){
        createProjectionMatrix();
        shaders = new ArrayList<>();

        loader = new Loader();

        clipPlane = new Vector4f(0, -1, 0, 9999);

        skyboxRenderer = new SkyboxRenderer(this);
        guiRenderer = new GuiRenderer(this);
    }

    public void renderScene(List<Entity> entities){
        for(Entity e : entities){
            e.render(this);
        }
    }

    public void render(Renderable renderable, Matrix4f[] transforms){
        renderable.bind();

        renderable.getShader().setUniform("lightPos", worldLight.getPos());
        renderable.getShader().setUniform("lightColour", worldLight.getColour());
        renderable.getShader().setUniform("ambientLight", ambientLight);
        renderable.getShader().setUniform("skyColour", skyColour);
        renderable.getShader().setUniform("clip_plane", clipPlane);
        renderable.getShader().setUniform("cameraPosition", camera.getLocation());

        renderable.getShader().setUniform("viewMatrix", MathUtil.createViewMatrix(camera));

        for(Matrix4f transformation : transforms){
            renderable.getShader().setUniform("transformationMatrix", transformation);

            if(renderable.getModel().isShouldRenderElements())
                GL11.glDrawElements(GL11.GL_TRIANGLES, renderable.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            else
                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, renderable.getModel().getVertexCount());
        }

        renderable.unbind();
    }

    public void render(Renderable renderable, Matrix4f transformation){
        renderable.bind();

        renderable.getShader().setUniform("lightPos", worldLight.getPos());
        renderable.getShader().setUniform("lightColour", worldLight.getColour());
        renderable.getShader().setUniform("ambientLight", ambientLight);
        renderable.getShader().setUniform("skyColour", skyColour);
        renderable.getShader().setUniform("clip_plane", clipPlane);
        renderable.getShader().setUniform("cameraPosition", camera.getLocation());

        renderable.getShader().setUniform("transformationMatrix", transformation);

        renderable.getShader().setUniform("viewMatrix", MathUtil.createViewMatrix(camera));

        if(renderable.getModel().isShouldRenderElements())
            GL11.glDrawElements(GL11.GL_TRIANGLES, renderable.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        else
            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, renderable.getModel().getVertexCount());
        renderable.unbind();
    }

    public void renderEntity(Entity entity){
        render(entity.getRenderable(), MathUtil.createTransformationMatrix(entity.getLocation(), entity.getRotX(), entity.getRotY(),
                entity.getRotZ(), entity.getScale()));
    }

    private void createProjectionMatrix(){
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix = new Matrix4f();
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    public void cleanUp(){
        loader.cleanUp();
        for(ShaderProgram s : shaders){
            s.cleanUp();
        }
    }

    public Loader getLoader(){
        return loader;
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public Light getWorldLight() {
        return worldLight;
    }

    public void setWorldLight(Light worldLight) {
        this.worldLight = worldLight;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public Vector3f getSkyColour() {
        return skyColour;
    }

    public void setSkyColour(Vector3f skyColour) {
        this.skyColour = skyColour;
        GL11.glClearColor(skyColour.x, skyColour.y, skyColour.z, 1.0f);
    }

    public void setClipPlane(Vector4f clipPlane) {
        this.clipPlane = clipPlane;
    }

    public Vector4f getClipPlane() {
        return clipPlane;
    }

    public SkyboxRenderer getSkyboxRenderer() {
        return skyboxRenderer;
    }

    public GuiRenderer getGuiRenderer() {
        return guiRenderer;
    }

    public void addShader(ShaderProgram shader){
        shaders.add(shader);
    }
}
