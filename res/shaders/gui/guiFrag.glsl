#version 140

in vec2 textureCoords;

out vec4 fragColour;

uniform sampler2D guiTexture;
uniform vec3 tint;

void main(){
    fragColour = texture(guiTexture,textureCoords) * vec4(tint, 1.0);
}