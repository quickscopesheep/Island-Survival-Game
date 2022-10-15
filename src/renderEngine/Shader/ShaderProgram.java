package renderEngine.Shader;

import org.lwjgl.BufferUtils;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import renderEngine.core.Renderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ShaderProgram {
    int vertexShaderId;
    int fragmentShaderId;

    int shaderProgramId;

    FloatBuffer matrixBuffer;

    Map<String, Integer> uniformLocations;

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath, Renderer renderer){
        shaderProgramId = GL20.glCreateProgram();

        vertexShaderId = loadShader(vertexShaderPath, GL20.GL_VERTEX_SHADER);
        fragmentShaderId = loadShader(fragmentShaderPath, GL20.GL_FRAGMENT_SHADER);

        GL20.glLinkProgram(shaderProgramId);

        if (vertexShaderId != 0) {
            GL20.glDetachShader(shaderProgramId, vertexShaderId);
            GL20.glDeleteShader(vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            GL20.glDetachShader(shaderProgramId, fragmentShaderId);
            GL20.glDeleteShader(vertexShaderId);
        }

        GL20.glValidateProgram(shaderProgramId);

        if (GL20.glGetProgrami(shaderProgramId, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Warning validating Shader code: " + GL20.glGetProgramInfoLog(shaderProgramId, 1024));
        }

        uniformLocations = new HashMap<>();

        matrixBuffer = BufferUtils.createFloatBuffer(16);

        if(getUniformLocation("projectionMatrix") != -1){
            bind();
            setUniform("projectionMatrix", renderer.getProjectionMatrix());
            unbind();
        }

        renderer.addShader(this);
    }

    public void bind(){
        GL20.glUseProgram(shaderProgramId);
    }

    public void unbind(){
        GL20.glUseProgram(0);
    }

    String ReadSourceFromFile(String path){
        StringBuilder shaderSource = new StringBuilder();
        File myObj = new File(path);
        Scanner myReader;
        try {
            myReader = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (myReader.hasNextLine()) {
            String data = myReader.nextLine() + "\n";
            shaderSource.append(data);
        }
        myReader.close();

        return shaderSource.toString();
    }

    int loadShader(String path, int type){
        int id = GL20.glCreateShader(type);

        GL20.glShaderSource(id, ReadSourceFromFile(path));
        GL20.glCompileShader(id);

        GL20.glAttachShader(shaderProgramId, id);

        if (GL20.glGetShaderi(shaderProgramId, GL20.GL_COMPILE_STATUS) == 0) {
            System.err.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(shaderProgramId, 1024));
            return -1;
        }

        return id;
    }

    int getUniformLocation(String uniformName){
        if(uniformLocations.containsKey(uniformName)){
            return uniformLocations.get(uniformName);
        }else{
            int location = GL20.glGetUniformLocation(shaderProgramId, uniformName);
            if(location != -1){
                uniformLocations.put(uniformName, location);
                return location;
            }else{
                return -1;
            }
        }
    }

    //matrix
    public void setUniform(String uniformName, Matrix4f value) {
        value.store(matrixBuffer);
        matrixBuffer.flip();

        GL20.glUniformMatrix4(getUniformLocation(uniformName), false, matrixBuffer);
    }

    //float
    public void setUniform(String uniformName, float value) {
        GL20.glUniform1f(getUniformLocation(uniformName), value);
    }

    //int
    public void setUniform(String uniformName, int value) {
        GL20.glUniform1i(getUniformLocation(uniformName), value);
    }

    //vec4
    public void setUniform(String uniformName, Vector4f value) {
        GL20.glUniform4f(getUniformLocation(uniformName), value.x, value.y, value.z, value.w);
    }

    //vec3
    public void setUniform(String uniformName, Vector3f value) {
        GL20.glUniform3f(getUniformLocation(uniformName), value.x, value.y, value.z);
    }

    public void cleanUp(){
        GL20.glDeleteProgram(shaderProgramId);
    }
}
