package renderEngine.core;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.w3c.dom.Text;
import renderEngine.Model.Model;
import renderEngine.Model.Renderable;
import renderEngine.Shader.ShaderProgram;
import renderEngine.Texture.TextureData;

public class Loader {

    private List<Integer> vaos = new ArrayList<Integer>();
    private List<Integer> vbos = new ArrayList<Integer>();
    private List<Integer> textures = new ArrayList<Integer>();

    public Model loadToVAO(float[] positions, float[] textureCoords, int[] indices, float[] normals) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        storeDataInAttributeList(2, 3, normals);
        unbindVAO();
        return new Model(vaoID, indices.length, true);
    }

    public Model loadToVAO(float[] positions, int dimensions){
        int vaoId = createVAO();
        storeDataInAttributeList(0, dimensions, positions);
        unbindVAO();

        return new Model(vaoId, positions.length, false);
    }

    private TextureData decodeTextureFile(String fileName) {
        int width = 0;
        int height = 0;
        ByteBuffer buffer = null;
        try {
            FileInputStream in = new FileInputStream(fileName);
            PNGDecoder decoder = new PNGDecoder(in);
            width = decoder.getWidth();
            height = decoder.getHeight();
            buffer = ByteBuffer.allocateDirect(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ", didn't work");
            System.exit(-1);
        }
        return new TextureData(width, height, buffer);
    }

    public int loadCubeMap(String[] paths){
        int texId = GL11.glGenTextures();
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        for(int i = 0; i < paths.length; i++){
            TextureData data = decodeTextureFile(paths[i]);
            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0,
                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
        }

        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        textures.add(texId);

        return texId;
    }

    public int loadTexture(String path){
        Texture texture = null;
        String fileExtension = path.split("\\.")[1];
        try {
            texture = TextureLoader.getTexture(fileExtension, new FileInputStream(path));
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -1);

            if(GLContext.getCapabilities().GL_EXT_texture_filter_anisotropic){
                float amount = Math.min(4f, GL11.glGetFloat(EXTTextureFilterAnisotropic.GL_MAX_TEXTURE_MAX_ANISOTROPY_EXT));
                GL11.glTexParameterf(GL11.GL_TEXTURE_2D, EXTTextureFilterAnisotropic.GL_TEXTURE_MAX_ANISOTROPY_EXT, amount);
            }else {
                System.out.println("anisotropic filtering is not supported by your graphics card");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading texture at path: " + path);
        }

        if(texture.getTextureID() == -1){
            System.err.println("Error loading texture at path: " + path);
            return -1;
        }

        textures.add(texture.getTextureID());
        return texture.getTextureID();
    }

    public void cleanUp() {
        for (int vao : vaos) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            GL15.glDeleteBuffers(texture);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        vaos.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int dimensions, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, dimensions, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        GL30.glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboId = GL15.glGenBuffers();
        vbos.add(vboId);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    public Model loadOBJFile(String path){
        FileReader fr = null;
        try {
            fr = new FileReader(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        BufferedReader reader = new BufferedReader(fr);

        String line;

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2f> texCoords = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();

        float[] verticesArray = null;
        float[] texCoordsArray = null;
        int[] indicesArray = null;
        float[] normalsArray = null;

        try {
            while (true){
                line = reader.readLine();
                String[] currentLine = line.split(" ");

                if(line.startsWith("v ")){
                    vertices.add(new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3])));
                }else if(line.startsWith("vt ")){
                    texCoords.add(new Vector2f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2])));
                }else if(line.startsWith("vn ")){
                    normals.add(new Vector3f(Float.parseFloat(currentLine[1]), Float.parseFloat(currentLine[2]), Float.parseFloat(currentLine[3])));
                }else if(line.startsWith("f ")){
                    texCoordsArray = new float[vertices.size()*2];
                    normalsArray = new float[vertices.size()*3];
                    break;
                }
            }

            System.out.println(vertices.size());
            System.out.println(texCoords.size());

            while (line != null){
                if(!line.startsWith("f ")){
                    continue;
                }

                String[] currentLine = line.split(" ");

                String[] line1 = currentLine[1].split("/");
                String[] line2 = currentLine[2].split("/");
                String[] line3 = currentLine[3].split("/");

                processVertex(line1, indices, texCoords, normals, texCoordsArray, normalsArray);
                processVertex(line2, indices, texCoords, normals, texCoordsArray, normalsArray);
                processVertex(line3, indices, texCoords, normals, texCoordsArray, normalsArray);
                line = reader.readLine();
            }
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        verticesArray = new float[vertices.size()*3];
        indicesArray = new int[indices.size()];

        int vertexPointer = 0;
        for(Vector3f vertex : vertices){
            verticesArray[vertexPointer++] = vertex.x;
            verticesArray[vertexPointer++] = vertex.y;
            verticesArray[vertexPointer++] = vertex.z;
        }

        for(int i = 0; i < indices.size(); i++){
            indicesArray[i] = indices.get(i);
        }

        return loadToVAO(verticesArray, texCoordsArray, indicesArray, normalsArray);
    }

    void processVertex(String[] vertexData, List<Integer> indices, List<Vector2f> texCoords, List<Vector3f> normals, float[] texCoordsArray,
                       float[] normalsArray){
        int currentVertexPointer = Integer.parseInt(vertexData[0])-1;
        indices.add(currentVertexPointer);

        Vector2f currentTex = texCoords.get(Integer.parseInt(vertexData[1])-1);
        texCoordsArray[currentVertexPointer*2] = currentTex.x;
        texCoordsArray[currentVertexPointer*2+1] = 1 - currentTex.y;

        Vector3f currentNormal = normals.get(Integer.parseInt(vertexData[2])-1);
        normalsArray[currentVertexPointer*3] = currentNormal.x;
        normalsArray[currentVertexPointer*3+1] = currentNormal.y;
        normalsArray[currentVertexPointer*3+2] = currentNormal.z;
    }

    public Renderable loadRenderableFromFile(String path, Renderer renderer){
        JSONParser parser = new JSONParser();
        JSONObject object;

        try {
            object = (JSONObject) parser.parse(new FileReader(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Model model;
        ShaderProgram shader;
        List<renderEngine.Texture.Texture> textures = new ArrayList<>();

        model = loadOBJFile((String) object.get("Model"));
        shader = new ShaderProgram((String)object.get("VertexShader"), (String)object.get("FragmentShader"), renderer);

        JSONArray Array = (JSONArray) object.get("Textures");
        for(Object obj : Array){
            JSONObject json = (JSONObject) obj;
            renderEngine.Texture.Texture texture = new renderEngine.Texture.Texture(loadTexture((String) json.get("path")), ((Long)json.get("slot")).intValue());
            model.addTexture(texture.getId(), texture.getSlot(), (boolean)json.get("hasTransparency"));
        }

        return new Renderable(shader, model);
    }
}