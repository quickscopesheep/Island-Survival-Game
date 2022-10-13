package renderEngine.Util;

import java.util.Random;

public class NoiseGenerator {

    float scale;
    float persistence;
    float lacunarity;
    int octaves;

    float terrainHeight;

    private Random random = new Random();
    private int seed;

    public NoiseGenerator(int seed, float scale, float persistence, float lacunarity, int octaves, float terrainHeight) {
        this.seed = seed;
        this.scale = scale;
        this.persistence = persistence;
        this.lacunarity = lacunarity;
        this.octaves = octaves;
        this.terrainHeight = terrainHeight;
    }

    public float generateHeight(int x, int y) {
        x = x < 0 ? -x : x;
        y = y < 0 ? -y : y;

        float amplitude = 1f;
        float frequency = 1f;

        float total = 0;
        for(int i = 0; i < octaves; i++){
            float sampleX = x/scale * frequency;
            float sampleY = y/scale * frequency;

            float value = getInterpolatedNoise(sampleX, sampleY);
            total += value * amplitude;

            amplitude *= persistence;
            frequency *= lacunarity;
        }

        return total * terrainHeight;
    }

    private float getInterpolatedNoise(float x, float z){
        int intX = (int) x;
        int intZ = (int) z;
        float fracX = x - intX;
        float fracZ = z - intZ;

        float v1 = getSmoothNoise(intX, intZ);
        float v2 = getSmoothNoise(intX + 1, intZ);
        float v3 = getSmoothNoise(intX, intZ + 1);
        float v4 = getSmoothNoise(intX + 1, intZ + 1);
        float i1 = interpolate(v1, v2, fracX);
        float i2 = interpolate(v3, v4, fracX);
        return interpolate(i1, i2, fracZ);
    }

    private float interpolate(float a, float b, float blend){
        double theta = blend * Math.PI;
        float f = (float)(1f - Math.cos(theta)) * 0.5f;
        return a * (1f - f) + b * f;
    }

    private float getSmoothNoise(int x, int z) {
        float corners = (getNoise(x - 1, z - 1) + getNoise(x + 1, z - 1) + getNoise(x - 1, z + 1)
                + getNoise(x + 1, z + 1)) / 16f;
        float sides = (getNoise(x - 1, z) + getNoise(x + 1, z) + getNoise(x, z - 1)
                + getNoise(x, z + 1)) / 8f;
        float center = getNoise(x, z) / 4f;
        return corners + sides + center;
    }

    private float getNoise(int x, int z) {
        random.setSeed(x * 49632 + z * 325176 + seed);
        return random.nextFloat() * 2f - 1f;
    }


}
