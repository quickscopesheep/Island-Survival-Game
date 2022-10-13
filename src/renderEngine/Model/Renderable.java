package renderEngine.Model;

import renderEngine.Shader.ShaderProgram;

public class Renderable {
    ShaderProgram shader;
    Model model;

    public Renderable(ShaderProgram shader, Model model) {
        this.shader = shader;
        this.model = model;
    }

    public void bind(){
        model.bind();
        shader.bind();
    }

    public void unbind(){
        model.unbind();
        shader.unbind();
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public Model getModel() {
        return model;
    }
}
