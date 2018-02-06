#version 330 core
in vec2 fragment_UV;

in vec4 vertex_Color;
in vec4 vertex_eyespace;
in vec4 vertex_normal;

uniform bool textureEnabled;
uniform mat4 modelviewMatrix;
uniform sampler2D tex;

uniform vec4 startColor;
uniform vec4 endColor;
uniform float r;
uniform vec2 c;

out vec4 fragColor;

void main(){
    if(textureEnabled){
        float brightness = 1;
        brightness = (0.75+0.25*brightness);
        vec4 temp = texture(tex, fragment_UV);
        vec3 temp_2 = temp.xyz * brightness;
        fragColor = vec4(temp_2, temp.w);
    } else {
        vec2 resolution = vec2(500,500);
        vec3 surface_normal = normalize(vertex_normal.xyz);
        vec3 eyespace_normal = normalize(vertex_eyespace.xyz);
        vec4 radial_color = mix(endColor, startColor, distance(vec2(gl_FragCoord.xy / resolution.xy), c) * r);
        fragColor = radial_color;
    }
}

