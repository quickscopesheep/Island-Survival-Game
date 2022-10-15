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

vec3 permute(vec3 x) { return mod(((x*34.0)+1.0)*x, 289.0); }

float snoise(vec2 v){
  const vec4 C = vec4(0.211324865405187, 0.366025403784439,
           -0.577350269189626, 0.024390243902439);
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);
  vec2 i1;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;
  i = mod(i, 289.0);
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
  + i.x + vec3(0.0, i1.x, 1.0 ));
  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy),
    dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;
  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

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
        albedo_colour = mix(sandTex, albedo_colour, smoothstep(1, 0, sea_height + 2 - (snoise(vec2(pass_worldPos.x/5, 0)) + 1.0 / 2.0)*0.5 - (pass_worldPos.y)));
    }

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitLightVector = normalize(toLightVector);

    float nDot1 = dot(unitNormal, unitLightVector);
    float brightness = max(nDot1, 0.0);

    vec3 diffuse = brightness * lightColour + ambientLight;

    fragColor = vec4(diffuse, 1.0) * albedo_colour;
    fragColor = mix(vec4(skyColour, 1.0), fragColor, visibility);
}