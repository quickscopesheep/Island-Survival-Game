package renderEngine.Util;

public class MeshPrimatives {
    public static class Plane{
        public static float[] vertices = {
                -0.5f, 0, 0.5f,
                -0.5f, 0, -0.5f,
                0.5f, 0, -0.5f,
                0.5f, 0, 0.5f
        };

        public static float[] texCoords = {
                0,0,
                0,2,
                2,2,
                2,0
        };

        public static float[] normals = {
                0,1,0,
                0,1,0,
                0,1,0,
                0,1,0,
        };

        public static int[] indices = {
                3, 1, 0,
                2, 1, 3
        };
    }
}
