#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoords;
layout (location=2) in vec3 normals;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPos;

out vec3 surfaceNormal;
out vec3 toLightVector;
out vec3 pass_worldPos;

out float visibility;

out vec2 pass_texCoords;

const float fogDensity = 0.0095;
const float gradient = 2.5;

uniform vec4 clip_plane;

void main() {
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPos;

    float clip = dot(worldPos, clip_plane);
    gl_ClipDistance[0] = clip;

    pass_worldPos = worldPos.xyz;

    gl_Position = projectionMatrix * positionRelativeToCam;
    pass_texCoords = texCoords;

    surfaceNormal = (transformationMatrix * vec4(normals, 0.0)).xyz;
    toLightVector = lightPos - worldPos.xyz;

    float distance = length(positionRelativeToCam.xyz);
    visibility = exp(-pow((distance * fogDensity), gradient));
    visibility = clamp(visibility, 0.0, 1.0);
}