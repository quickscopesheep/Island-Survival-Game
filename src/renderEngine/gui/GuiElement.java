package renderEngine.gui;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GuiElement {
    int texture;
    Vector2f position;
    Vector2f scale;

    Vector3f tint;

    public GuiElement(int texture, Vector2f position, Vector2f scale) {
        this.texture = texture;
        this.position = position;
        this.scale = scale;
        this.tint = new Vector3f(1, 1, 1);
    }

    public void setTint(Vector3f tint) {
        this.tint = tint;
    }

    public Vector3f getTint() {
        return tint;
    }

    public int getTexture() {
        return texture;
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2f getScale() {
        return scale;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setScale(Vector2f scale) {
        this.scale = scale;
    }
}
