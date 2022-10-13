package renderEngine.Entity;

import org.lwjgl.util.vector.Vector3f;
import renderEngine.Model.Renderable;
import renderEngine.core.Renderer;

public class Entity {
    protected Vector3f location;
    protected float rotX, rotY, rotZ;
    protected float scale = 1f;

    protected Renderable renderable;

    public Entity(Vector3f location, Renderable renderable){
        this.location = location;
        this.renderable = renderable;
    }

    public void tick(float delta){

    }

    public void render(Renderer renderer){
        renderer.renderEntity(this);
    }

    public void Translate(float dx, float dy, float dz){
        location.translate(dx, dy, dz);
    }

    public void Rotate(float dx, float dy, float dz){
        rotX += dx;
        rotY += dy;
        rotZ += dz;
    }

    public Renderable getRenderable() {
        return renderable;
    }

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public float getRotX() {
        return rotX;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
