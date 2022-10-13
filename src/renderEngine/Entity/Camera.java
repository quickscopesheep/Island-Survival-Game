package renderEngine.Entity;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import javax.imageio.stream.ImageOutputStream;
import java.awt.event.MouseEvent;

public class Camera {
    public static final float MOVE_SPEED = .05f;

    Vector3f location;
    float rotX, rotY, rotZ;

    public Camera(Vector3f location, float rotX, float rotY, float rotZ) {
        this.location = location;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;

        Mouse.setGrabbed(true);
    }

    public void Translate(float dx, float dy, float dz){
        location.translate(dx, dy, dz);
    }

    public void Rotate(float dx, float dy, float dz){
        rotX += dx;
        rotY += dy;
        rotZ += dz;
    }

    public void move(){
        float inputX = 0, inputY = 0, inputZ = 0;

        if(Keyboard.isKeyDown(Keyboard.KEY_W)) inputY = 1;
        if(Keyboard.isKeyDown(Keyboard.KEY_S)) inputY = -1;
        if(Keyboard.isKeyDown(Keyboard.KEY_A)) inputX = -1;
        if(Keyboard.isKeyDown(Keyboard.KEY_D)) inputX = 1;
        if(Keyboard.isKeyDown(Keyboard.KEY_Q)) inputZ = -1;
        if(Keyboard.isKeyDown(Keyboard.KEY_E)) inputZ = 1;

        float forwardX = (float)Math.sin(Math.toRadians(rotY - 90));
        float forwardZ = (float)Math.cos(Math.toRadians(rotY - 90));

        float rightX = (float)Math.sin(Math.toRadians(rotY));
        float rightZ = (float)Math.cos(Math.toRadians(rotY));

        Vector3f move = new Vector3f(inputX, inputZ, inputY);
        if(move.length() != 0){
            move.normalise();
            Translate(forwardZ * (move.z * MOVE_SPEED),0, forwardX * (move.z * MOVE_SPEED));
            Translate(rightZ * (move.x * MOVE_SPEED),0, rightX * (move.x * MOVE_SPEED));
            Translate(0, move.y * MOVE_SPEED, 0);
        }

        float mouseX = Mouse.getDX() * .1f;
        float mouseY = Mouse.getDY() * -.1f;

        Rotate(mouseY, mouseX, 0);
    }

    public void invertPitch(){
        this.rotX = -rotX;
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
}
