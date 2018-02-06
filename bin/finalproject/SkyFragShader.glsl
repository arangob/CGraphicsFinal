#version 330 core
in vec2 fragment_UV;

in vec4 vertex_Color;
in vec4 vertex_eyespace;
in vec4 vertex_normal;

uniform bool textureEnabled;
uniform mat4 modelviewMatrix;
uniform sampler2D tex;

out vec4 fragColor;

void main(){
    if(textureEnabled){
        float brightness = 1;
        brightness = (0.75+0.25*brightness);
        vec4 temp = texture(tex, fragment_UV);
        vec3 temp_2 = temp.xyz * brightness;
        fragColor = vec4(temp_2, temp.w);
    } else {
        vec3 surface_normal = normalize(vertex_normal.xyz);
        vec3 eyespace_normal = normalize(vertex_eyespace.xyz);
        vec3 light = normalize((modelviewMatrix * vec4(0,0,10,0)).xyz);
        vec4 diffuse = vertex_Color * clamp(dot(light, surface_normal), 0 ,1);
        fragColor = vec4(diffuse.xyz, 1);
    }
}
