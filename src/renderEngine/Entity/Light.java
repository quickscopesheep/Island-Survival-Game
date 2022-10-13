package renderEngine.Entity;

import org.lwjgl.util.vector.Vector3f;

public class Light {
    Vector3f pos;
    Vector3f colour;

    public Light(Vector3f pos, Vector3f colour) {
        this.pos = pos;
        this.colour = colour;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getColour() {
        return colour;
    }
}
