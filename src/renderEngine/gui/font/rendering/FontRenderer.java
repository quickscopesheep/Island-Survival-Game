package renderEngine.gui.font.rendering;

import renderEngine.Shader.ShaderProgram;
import renderEngine.core.Renderer;
import renderEngine.gui.font.GUIText;

public class FontRenderer {

	private ShaderProgram shader;

	public FontRenderer(Renderer renderer) {
		shader = new ShaderProgram("res/shaders/gui/fontVertex.glsl", "res/shaders/gui/fontFragment.glsl", renderer);
	}

	public void cleanUp(){
		shader.cleanUp();
	}
	
	private void prepare(){}
	
	private void renderText(GUIText text){

	}
	
	private void endRendering(){}

}
