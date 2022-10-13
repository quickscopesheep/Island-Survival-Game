#version 330 core

out vec4 fragColour;

in vec4 clipSpace;

in float visibility;

in vec2 pass_texCoords;

uniform sampler2D colourTexture;
uniform sampler2D depthTexture;

uniform sampler2D dudvtexture;

uniform vec3 skyColour;

uniform float waveTime;

const float dudvStrength = 0.02;
const float waveSpeed = 0.05;

float linearize_depth(float original_depth) {
    float near = 0.01;
    float far = 1000.0;
    return (2.0 * near) / (far + near - original_depth * (far - near));
}

void main(){
    vec2 ndc = (clipSpace.xy/clipSpace.w)/2+0.5;

    vec4 depthColour = texture(depthTexture, ndc);
    float linearDepth = linearize_depth(depthColour.x);

    float waterDistance = linearize_depth(gl_FragCoord.z);

    float waterDepth = linearDepth - waterDistance;

    vec2 refractTexCoords = vec2 (ndc.x, ndc.y);

    vec2 distortCoords = vec2(pass_texCoords.x + waveTime * waveSpeed, pass_texCoords.y + waveTime * waveSpeed);
    vec2 distortion1 = (texture(dudvtexture, distortCoords).rg * 2 - 1) * dudvStrength;

    refractTexCoords += distortion1 * clamp(waterDepth * 20, 0, 1);
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    vec4 refractionColour = texture(colourTexture, refractTexCoords);

    fragColour = mix(refractionColour, vec4(0, .6, .9, 1), .5);
    fragColour = mix(fragColour, vec4(0, 0.4, 1, 1), clamp(waterDepth*10, 0, 1));

    fragColour = mix(vec4(skyColour, 1.0), fragColour, visibility);

    fragColour.a = clamp(waterDepth*750, 0, 1);
}
