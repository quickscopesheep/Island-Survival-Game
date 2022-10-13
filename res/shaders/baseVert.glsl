#version 330 core

layout (location=0) in vec3 position;
layout (location=1) in vec2 texCoords;
layout (location=2) in vec3 normals;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3 lightPos;

out vec3 surfaceNormal;

out vec2 pass_texCoords;

uniform vec4 clip_plane;

void main() {
    vec4 worldPos = transformationMatrix * vec4(position, 1.0);
    vec4 positionRelativeToCam = viewMatrix * worldPos;

    float clip = dot(worldPos, clip_plane);
    gl_ClipDistance[0] = clip;

    gl_Position = projectionMatrix * positionRelativeToCam;
    pass_texCoords = texCoords;

    surfaceNormal = (transformationMatrix * vec4(normals, 0.0)).xyz;
}