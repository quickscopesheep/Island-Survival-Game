#version 330 core

in vec3 texCoords;

out vec4 fragColour;

uniform samplerCube cubemap;

void main(){
    fragColour = texture(cubemap, texCoords);
}