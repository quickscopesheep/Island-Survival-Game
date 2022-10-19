package renderEngine.Util;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.Entity.Camera;

public class MathUtil {

    public static Matrix4f identityMatrix = (Matrix4f) (new Matrix4f().setIdentity());

    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        Matrix4f.translate(translation, matrix, matrix);
        Matrix4f.scale(new Vector3f(scale.x * Display.getHeight() / Display.getWidth(), scale.y, 1f), matrix, matrix);
        return matrix;
    }

    public static Matrix4f createTransformationMatrix(Vector3f pos, float rotX, float rotY, float rotZ, float scale){
        Matrix4f mat = new Matrix4f();

        mat.translate(pos);

        mat.rotate(rotX, new Vector3f(1, 0, 0));
        mat.rotate(rotY, new Vector3f(0, 1, 0));
        mat.rotate(rotZ, new Vector3f(0, 0, 1));

        mat.scale(new Vector3f(scale, scale, scale));

        return mat;
    }

    public static Matrix4f createViewMatrix(Camera camera) {
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.setIdentity();
        Matrix4f.rotate((float) Math.toRadians(camera.getRotX()), new Vector3f(1, 0, 0), viewMatrix,
                viewMatrix);
        Matrix4f.rotate((float) Math.toRadians(camera.getRotY()), new Vector3f(0, 1, 0), viewMatrix, viewMatrix);
        Vector3f cameraPos = camera.getLocation();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x,-cameraPos.y,-cameraPos.z);
        Matrix4f.translate(negativeCameraPos, viewMatrix, viewMatrix);
        return viewMatrix;
    }

    public static float lerp(float a, float b, float t){
        return a + t * (b - a);
    }

    public static float clamp(float a, float min, float max){
        if(a > max) a = max;
        else if(a < min) a = min;
        return a;
    }
}
