#version 330 core

in vec2 pass_texCoords;

in vec3 surfaceNormal;
in vec3 toLightVector;

in vec3 pass_worldPos;

in float visibility;

out vec4 fragColor;

uniform sampler2D flat_texture;
uniform sampler2D hill_texture;
uniform sampler2D sand_texture;

uniform vec3 lightColour;
uniform vec3 ambientLight;
uniform vec3 skyColour;

uniform float sea_height = -10.0;



void main() {
    float steepness = dot(normalize(surfaceNormal), vec3(0, 1, 0))+.25;
    steepness = exp(-pow(steepness, 10));
    steepness = clamp(steepness, 0, 1);
    steepness = smoothstep(0, 1, steepness);

    vec4 groundTex = texture(flat_texture, pass_texCoords);
    vec4 hillTex = texture(hill_texture, pass_texCoords);
    vec4 sandTex = texture(sand_texture, pass_texCoords);

    vec4 albedo_colour = mix(groundTex, hillTex, steepness);

    if(pass_worldPos.y < sea_height + 2){
        albedo_colour = mix(sandTex, albedo_colour, smoothstep(1, 0, sea_height + 2 - (pass_worldPos.y)));
    }

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float nDot1 = dot(unitNormal, unitLightVector);
    float brightness = max(nDot1, 0.0);

    vec3 diffuse = brightness * lightColour + ambientLight;

    fragColor = vec4(diffuse, 1.0) * albedo_colour;
    fragColor = mix(vec4(skyColour, 1.0), fragColor, visibility);
}