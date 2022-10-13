#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoords;
layout (location=2) in vec3 normals;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPos;

out vec2 pass_texCoords;

out float visibility;
out vec4 clipSpace;

const float fogDensity = 0.0095;
const float gradient = 2.5;

const float tiling = 10;

void main(){
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPos;

    pass_texCoords = texCoords;

    clipSpace = projectionMatrix * positionRelativeToCam;
    gl_Position = clipSpace;

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * fogDensity), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}