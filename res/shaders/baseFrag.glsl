#version 330 core

in vec2 pass_texCoords;

in vec3 surfaceNormal;
in vec3 toLightVector;

in float visibility;

out vec4 fragColor;

uniform sampler2D textureSampler;

uniform vec3 lightColour;
uniform vec3 ambientLight;
uniform vec3 skyColour;

void main() {
    vec4 albedo_colour = texture(textureSampler, pass_texCoords);

    if(albedo_colour.a < .5){
        discard;
    }

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float nDot1 = dot(unitNormal, unitLightVector);
    float brightness = max(nDot1, 0.0);

    vec3 diffuse = brightness * lightColour + ambientLight;

    fragColor = vec4(diffuse, 1.0) * albedo_colour;
    fragColor = mix(vec4(skyColour, 1.0), fragColor, visibility);
}